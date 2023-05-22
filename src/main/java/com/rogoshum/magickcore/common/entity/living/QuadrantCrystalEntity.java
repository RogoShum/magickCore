package com.rogoshum.magickcore.common.entity.living;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.api.entity.IQuadrantEntity;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.client.entity.easyrender.QuadrantEntityRenderer;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.magick.context.SpellContext;
import com.rogoshum.magickcore.common.util.LootUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;

public class QuadrantCrystalEntity extends LivingEntity implements ISpellContext, IQuadrantEntity, IManaRefraction, IEntityAdditionalSpawnData {
    private final SpellContext spellContext = SpellContext.create();
    private Vec3 finalPos;
    public static final NonNullList<ItemStack> ARMOR_ITEMS = NonNullList.withSize(1, ItemStack.EMPTY);
    public int attackCount;

    public QuadrantCrystalEntity(EntityType<? extends LivingEntity> type, Level worldIn) {
        super(type, worldIn);
        ExtraDataUtil.entityStateData(this).setFinalMaxElementShield(25);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        MagickCore.proxy.addRenderer(() -> new QuadrantEntityRenderer(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if(this.getLastHurtByMob() != null && tickCount % 20 == 0) {
            EntityType<?> manaType = ModEntities.MANA_STAR.get();
            if(level.isDay())
                manaType = ModEntities.LEAF.get();
            SpellContext context = LootUtil.createEntityType(manaType, 0, 200, 2, true, spellContext().element());
            LootUtil.attackType(context, 2, 100, 3, spellContext().element());
            MagickContext magickContext = MagickContext.create(level, context).caster(this).victim(this.getLastHurtByMob()).noCost();
            MagickReleaseHelper.releaseMagick(magickContext);
        }
    }

    public boolean release(Direction direction) {
        return false;
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return sizeIn.height * 0.5F;
    }

    @Override
    public boolean isPickable() {
        return super.isPickable();
    }

    @Override
    public void tick() {
        super.tick();
        if(finalPos == null)
            finalPos = this.position();
        setPos(finalPos);
        ExtraDataUtil.entityStateData(this).setElement(spellContext().element());
        if(tickCount < 10) {
            BlockPos blockpos = new BlockPos(this.position());
            int y = blockpos.getY();

            if(level.getBlockState(blockpos).isAir()) {
                while (y > 0 && level.getBlockState(blockpos).isAir()) {
                    y--;
                    blockpos = new BlockPos(getX(), y, getZ());
                }
            } else {
                while (y < 256 && !level.getBlockState(blockpos).isAir()) {
                    y++;
                    blockpos = new BlockPos(getX(), y, getZ());
                }
            }
            finalPos = new Vec3(getX(), y + spellContext().range() * 0.8, getZ());
            setPos(finalPos);
            tickCount=11;
        }
    }

    public void init() {
        this.tickCount = -10;
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(source.getMsgId().contains("fall") || source.getMsgId().contains("starve")
                || source.getMsgId().contains("cactus") || source.getMsgId().contains("flyIntoWall")
                || source.getMsgId().contains("fallingBlock") || source.getMsgId().contains("drown")
                || source.getMsgId().contains("inWall") || source.getMsgId().contains("cramming")
                || source.getMsgId().contains("anvil") || source.getMsgId().contains("hotFloor"))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource p_21014_) {
        super.die(p_21014_);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return ARMOR_ITEMS;
    }

    @Nonnull
    @Override
    public ItemStack getItemBySlot(EquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slotIn, ItemStack stack) {}

    @Nonnull
    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.LEFT;
    }

    @Override
    public SpellContext spellContext() {
        return spellContext;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        spellContext().deserialize(compound);
        deserializeBlockSet(compound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        spellContext().serialize(compound);
        serializeBlockSet(compound);
    }

    @Override
    public boolean refraction(SpellContext context) {
        return false;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        CompoundTag tag = new CompoundTag();
        spellContext().serialize(tag);
        buffer.writeNbt(tag);
        tag = new CompoundTag();
        serializeBlockSet(tag);
        buffer.writeNbt(tag);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        spellContext().deserialize(additionalData.readNbt());
        deserializeBlockSet(additionalData.readNbt());
    }

    public void serializeBlockSet(CompoundTag compoundNBT) {
    }

    public void deserializeBlockSet(CompoundTag compoundNBT) {
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public int range() {
        return Math.min((int) (spellContext().range() * 2), 32);
    }

    @Override
    public void magnify(SpellContext context) {
        if(spellContext().force() > 3)
            spellContext().force(3);
        if(context.element() == spellContext().element()) {
            context.force(context.force() * spellContext().force());
            context.range(context.range() * spellContext().force());
            context.tick((int) (context.tick() * spellContext().force()));
        } else {
            context.force(context.force() * 0.5f);
            context.range(context.range() * 0.5f);
            context.tick((int) (context.tick() * 0.5f));
        }
    }
}
