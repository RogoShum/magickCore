package com.rogoshum.magickcore.common.entity.living;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.api.enums.ManaLimit;
import com.rogoshum.magickcore.api.mana.IManaCapacity;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.item.MagickContextItem;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.List;

public class ArtificialLifeEntity extends LivingEntity implements ISpellContext, IManaRefraction, IEntityAdditionalSpawnData {
    private final SpellContext spellContext = SpellContext.create();
    public static final NonNullList<ItemStack> ARMOR_ITEMS = NonNullList.withSize(1, ItemStack.EMPTY);
    private Vector3d originPos;
    private BlockPos originBlockPos;
    private boolean power = false;
    public ArtificialLifeEntity(EntityType<? extends LivingEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public boolean shouldRelease() {
        if(originBlockPos == null) return false;
        int power = 0;
        Direction powerDirection = null;
        for(Direction direction : Direction.values()) {
            int j = world.getRedstonePower(originBlockPos.offset(direction), direction);

            if (j > power) {
                power = j;
                powerDirection = direction;
            }
        }

        if(power > 0) {
            if(!this.power) {
                this.power = true;
                return release(powerDirection);
            } else if (ticksExisted % power == 0) {
                return release(powerDirection);
            }
        } else
            this.power = false;
        return true;
    }

    public boolean release(Direction direction) {
        MagickContext context = MagickContext.create(this.world, spellContext())
                .replenishChild(DirectionContext.create(Vector3d.copy(direction.getOpposite().getDirectionVec())))
                .<MagickContext>replenishChild(PositionContext.create(this.getPositionVec().add(0, 0.5 * getHeight(), 0)))
                .caster(this).projectile(this)
                .victim(null);
        return MagickReleaseHelper.releaseMagick(context);
    }

    @Override
    public boolean canBeCollidedWith() {
        return super.canBeCollidedWith();
    }

    @Override
    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        ActionResultType ret = super.processInitialInteract(player, hand);
        if (ret.isSuccessOrConsume()) return ret;
        ItemStack stack = player.getHeldItem(hand);
        if(stack.getItem() instanceof MagickContextItem) {
            spellContext().copy(ExtraDataUtil.itemManaData(stack).spellContext());
            return ActionResultType.CONSUME;
        } else if(player.isSneaking())
            spellContext().clear();

        return ActionResultType.PASS;
    }

    @Override
    public void tick() {
        super.tick();
        if(originPos == null) {
            originBlockPos = new BlockPos(this.getPositionVec());
            originPos = Vector3d.copyCentered(originBlockPos).subtract(0, getHeight() * 0.5, 0);
        } else
            this.setPosition(originPos.x, originPos.y, originPos.z);

        List<? extends Entity> capacities = world.getEntitiesInAABBexcluding(this, getBoundingBox().grow(8), entity -> entity instanceof IManaCapacity);
        int count = capacities.size();
        float manaNeed = 0;
        EntityStateData state = ExtraDataUtil.entityStateData(this);
        state.setMaxManaValue(ManaLimit.MAX_MANA.getValue());
        if(state.getManaValue() < state.getMaxManaValue()) {
            manaNeed = state.getMaxManaValue() - state.getManaValue();
        }

        for(int i = 0; i < count; ++i) {
            IManaCapacity capacity = (IManaCapacity) capacities.get(i);
            if(manaNeed > 0) {
                float mana = capacity.manaCapacity().extractMana(manaNeed);
                manaNeed -= mana;
                state.setManaValue(state.getManaValue() + mana);
            }

            if(world.isRemote)
                spawnSupplierParticle((Entity) capacity);
        }
        shouldRelease();
    }

    @Override
    public boolean func_241845_aY() {
        return this.isAlive();
    }

    public void spawnSupplierParticle(Entity supplier) {
        Vector3d center = new Vector3d(0, this.getHeight() * 0.5, 0);
        Vector3d end = this.getPositionVec().add(center);
        Vector3d start = supplier.getPositionVec().add(0, supplier.getHeight() * 0.5, 0);
        double dis = start.subtract(end).length();
        if(dis < 0.2)
            dis = 0.2;
        int distance = (int) (8 * dis);
        if(distance < 1)
            distance = 1;
        float directionPoint = (float) (supplier.ticksExisted % distance) / distance;
        int c = (int) (directionPoint * distance);

        Vector3d direction = Vector3d.ZERO;
        Vector3d origin = start.subtract(end);
        double y = -origin.y;
        double x = Math.abs(origin.x);
        double z = Math.abs(origin.z);
        if(x > z)
            direction = new Vector3d(x, y, 0);
        else if(z > x)
            direction = new Vector3d(0, y, z);
        float scale;
        float alpha = 1.0f;

        MagickElement element = ModElements.ORIGIN;
        if(supplier instanceof ISpellContext) {
            element = ((ISpellContext) supplier).spellContext().element;
        }
        for (int i = 0; i < distance; ++i) {
            if(i == c)
                scale = 0.3f;
            else
                scale = 0.15f;

            double trailFactor = i / (distance - 1.0D);
            Vector3d pos = ParticleUtil.drawParabola(start, end, trailFactor, dis / 3, direction);
            LitParticle par = new LitParticle(this.world, element.getRenderer().getParticleTexture()
                    , new Vector3d(pos.x, pos.y, pos.z), scale, scale, alpha, 3, element.getRenderer());
            par.setParticleGravity(0);
            par.setLimitScale();
            par.setGlow();
            MagickCore.addMagickParticle(par);
        }
    }

    @Nonnull
    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return ARMOR_ITEMS;
    }

    @Nonnull
    @Override
    public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {}

    @Nonnull
    @Override
    public HandSide getPrimaryHand() {
        return HandSide.LEFT;
    }

    @Override
    public SpellContext spellContext() {
        return spellContext;
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        spellContext().deserialize(compound);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        spellContext().serialize(compound);
    }

    @Override
    public boolean refraction(SpellContext context) {
        return true;
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        CompoundNBT tag = new CompoundNBT();
        spellContext().serialize(tag);
        buffer.writeCompoundTag(tag);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        spellContext().deserialize(additionalData.readCompoundTag());
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
