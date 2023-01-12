package com.rogoshum.magickcore.common.entity.living;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.api.enums.ManaLimit;
import com.rogoshum.magickcore.api.mana.IManaCapacity;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.client.entity.easyrender.ArtificialLifeEntityRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.item.MagickContextItem;
import com.rogoshum.magickcore.common.item.placeable.EntityItem;
import com.rogoshum.magickcore.common.item.tool.WandItem;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.util.EntityInteractHelper;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ArtificialLifeEntity extends LivingEntity implements ISpellContext, IManaRefraction, IEntityAdditionalSpawnData {
    private final SpellContext spellContext = SpellContext.create();
    public static final NonNullList<ItemStack> ARMOR_ITEMS = NonNullList.withSize(1, ItemStack.EMPTY);
    private static final DataParameter<Direction> DIRECTION = EntityDataManager.createKey(ArtificialLifeEntity.class, DataSerializers.DIRECTION);
    private static final DataParameter<Boolean> FOCUS = EntityDataManager.createKey(ArtificialLifeEntity.class, DataSerializers.BOOLEAN);
    private Vector3d originPos;
    private BlockPos originBlockPos;
    private boolean power = false;
    private int powerCount;
    private final HashSet<Vector3d> vectorSet = new HashSet<>();
    public ArtificialLifeEntity(EntityType<? extends LivingEntity> type, World worldIn) {
        super(type, worldIn);
        this.dataManager.register(DIRECTION, Direction.UP);
        this.dataManager.register(FOCUS, false);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        MagickCore.proxy.addRenderer(() -> new ArtificialLifeEntityRenderer(this));
    }

    public HashSet<Vector3d> getVectorSet() {
        return vectorSet;
    }

    public void setDirection(Direction direction) {
        this.dataManager.set(DIRECTION, direction);
    }

    public Direction getDirection() {
        return this.dataManager.get(DIRECTION);
    }

    public void setFocus(boolean focus) {
        this.dataManager.set(FOCUS, focus);
    }

    public boolean isFocus() {
        return this.dataManager.get(FOCUS);
    }

    public void checkRelease() {
        if(originBlockPos == null) return;
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
            powerCount++;
            if(!this.power) {
                this.power = true;
                powerCount = 0;
                release(powerDirection);
            } else if (powerCount >= power * 2) {
                powerCount = 0;
                release(powerDirection);
            }
        } else {
            powerCount = 0;
            this.power = false;
        }
    }

    public boolean release(Direction direction) {
        PositionContext position;
        DirectionContext directionContext;
        Vector3d self = this.getPositionVec().add(0, 0.5 * getHeight(), 0);
        boolean focus = isFocus();
        if(focus && !this.getVectorSet().isEmpty()) {
            for(Vector3d vec : this.getVectorSet()) {
                position = PositionContext.create(vec);
                MagickContext context = MagickContext.create(this.world, spellContext())
                        .replenishChild(DirectionContext.create(position.pos.subtract(self)))
                        .<MagickContext>replenishChild(position)
                        .caster(this).projectile(this)
                        .victim(null).doBlock();
                MagickReleaseHelper.releaseMagick(context);
            }
            return true;
        }
        if(focus) {
            position = PositionContext.create(getTracePos());
            directionContext = DirectionContext.create(position.pos.subtract(self));
        } else {
            directionContext = DirectionContext.create(Vector3d.copy(direction.getOpposite().getDirectionVec()));
            position = PositionContext.create(self);
        }

        MagickContext context = MagickContext.create(this.world, spellContext())
                .replenishChild(directionContext)
                .<MagickContext>replenishChild(position)
                .caster(this).projectile(this)
                .victim(null);
        if(focus)
            context.doBlock();
        return MagickReleaseHelper.releaseMagick(context);
    }

    public Vector3d getTracePos() {
        Vector3d vec = this.getPositionVec().add(0, this.getEyeHeight(), 0);
        for(int i = 1; i<=8; ++i) {
            Vector3d end = vec.add(Vector3d.copy(getDirection().getDirectionVec()).scale(i));
            BlockPos pos = new BlockPos(end);
            if(!world.isAirBlock(pos))
                return end;
        }

        return vec.add(Vector3d.copy(getDirection().getDirectionVec()).scale(8));
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return sizeIn.height * 0.5F;
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
        if(stack.getItem() instanceof BlockItem || stack.getItem() instanceof EntityItem) {
            return EntityInteractHelper.placeBlock(player, hand, stack, this);
        } else if(stack.getItem() instanceof WandItem) {
            if(!isFocus()) {
                setFocus(true);
                HashSet<Vector3d> vector3ds = NBTTagHelper.getVectorSet(stack.getOrCreateChildTag(WandItem.SET_KEY));
                if(!vector3ds.isEmpty()) {
                    this.getVectorSet().clear();
                    this.getVectorSet().addAll(vector3ds);
                } else {
                    Vector3d vector3d = player.getLookVec();
                    setDirection(Direction.getFacingFromVector(vector3d.x, vector3d.y, vector3d.z).getOpposite());
                }
            } else {
                setFocus(false);
                this.getVectorSet().clear();
            }
            return ActionResultType.CONSUME;
        } else if(stack.getItem() instanceof MagickContextItem) {
            spellContext().copy(ExtraDataUtil.itemManaData(stack).spellContext());
            return ActionResultType.CONSUME;
        } else if(player.isSneaking())
            spellContext().clear();

        return ActionResultType.PASS;
    }

    @Override
    public void tick() {
        this.fallDistance = 0;
        super.tick();
        if (this.ticksExisted == 1) {
            this.playSound(SoundEvents.BLOCK_SLIME_BLOCK_PLACE, 0.5F, 1.0F + MagickCore.rand.nextFloat());
        }
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

        if(isFocus() && this.getVectorSet().isEmpty() && world.isRemote()) {
            Vector3i vector3i = getDirection().getDirectionVec();
            double scale = (0.5+rand.nextFloat());
            LitParticle litPar = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                    , this.getPositionVec().add(0, getEyeHeight(), 0).add(vector3i.getX()*scale, vector3i.getY()*scale, vector3i.getZ()*scale)
                    , 0.1f, 0.1f, 0.8f, 10, spellContext().element.getRenderer());
            litPar.setGlow();
            litPar.setParticleGravity(0f);
            litPar.setLimitScale();
            MagickCore.proxy.addMagickParticle(litPar);
        }
        checkRelease();
    }

    @Override
    public boolean func_241845_aY() {
        return this.isAlive();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if(source.damageType.equals(DamageSource.FALL.damageType)) return false;
        return super.attackEntityFrom(source, amount);
    }

    public void spawnSupplierParticle(Entity supplier) {
        Vector3d center = new Vector3d(0, this.getHeight() * 0.5, 0);
        Vector3d end = this.getPositionVec().add(center);
        Vector3d start = supplier.getPositionVec().add(0, supplier.getHeight() * 0.5, 0);
        double dis = start.subtract(end).length();
        if(dis < 0.2)
            dis = 0.2;
        int distance = (int) (6 * dis);
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
                scale = 0.1f;
            else
                scale = 0.05f;

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
        if(compound.contains("focus"))
            this.setFocus(compound.getBoolean("focus"));
        if(compound.contains("direction"))
            this.setDirection(Direction.byName(compound.getString("direction")));
        deserializeBlockSet(compound);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        spellContext().serialize(compound);
        compound.putBoolean("focus", isFocus());
        compound.putString("direction", getDirection().getName2());
        serializeBlockSet(compound);
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
        tag = new CompoundNBT();
        serializeBlockSet(tag);
        buffer.writeCompoundTag(tag);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        spellContext().deserialize(additionalData.readCompoundTag());
        deserializeBlockSet(additionalData.readCompoundTag());
    }

    public void serializeBlockSet(CompoundNBT compoundNBT) {
        CompoundNBT tag = new CompoundNBT();
        NBTTagHelper.saveVectorSet(tag, this.vectorSet);
        compoundNBT.put("blockVec", tag);
    }

    public void deserializeBlockSet(CompoundNBT compoundNBT) {
        if(compoundNBT.contains("blockVec")) {
            CompoundNBT tag = compoundNBT.getCompound("blockVec");
            HashSet<Vector3d> vector3ds = NBTTagHelper.getVectorSet(tag);
            if(!vector3ds.isEmpty()) {
                this.vectorSet.clear();
                this.vectorSet.addAll(vector3ds);
            }
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
