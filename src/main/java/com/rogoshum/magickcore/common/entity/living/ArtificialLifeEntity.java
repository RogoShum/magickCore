package com.rogoshum.magickcore.common.entity.living;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.api.enums.ManaLimit;
import com.rogoshum.magickcore.api.mana.IManaCapacity;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.client.entity.easyrender.ArtificialLifeEntityRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.item.MagickContextItem;
import com.rogoshum.magickcore.common.item.placeable.EntityItem;
import com.rogoshum.magickcore.common.item.tool.WandItem;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.magick.context.SpellContext;
import com.rogoshum.magickcore.api.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.api.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.util.EntityInteractHelper;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.network.NetworkHooks;

public class ArtificialLifeEntity extends LivingEntity implements ISpellContext, IManaRefraction, IEntityAdditionalSpawnData {
    private final SpellContext spellContext = SpellContext.create();
    public static final NonNullList<ItemStack> ARMOR_ITEMS = NonNullList.withSize(1, ItemStack.EMPTY);
    private static final EntityDataAccessor<Direction> DIRECTION = SynchedEntityData.defineId(ArtificialLifeEntity.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Boolean> FOCUS = SynchedEntityData.defineId(ArtificialLifeEntity.class, EntityDataSerializers.BOOLEAN);
    private Vec3 originPos;
    private BlockPos originBlockPos;
    private boolean power = false;
    private int powerCount;
    private final HashSet<Vec3> vectorSet = new HashSet<>();
    public ArtificialLifeEntity(EntityType<? extends LivingEntity> type, Level worldIn) {
        super(type, worldIn);
        this.entityData.define(DIRECTION, Direction.UP);
        this.entityData.define(FOCUS, false);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        MagickCore.proxy.addRenderer(() -> new ArtificialLifeEntityRenderer(this));
    }

    public HashSet<Vec3> getVectorSet() {
        return vectorSet;
    }

    public void setDirection(Direction direction) {
        this.entityData.set(DIRECTION, direction);
    }

    public Direction getDirection() {
        return this.entityData.get(DIRECTION);
    }

    public void setFocus(boolean focus) {
        this.entityData.set(FOCUS, focus);
    }

    public boolean isFocus() {
        return this.entityData.get(FOCUS);
    }

    public void checkRelease() {
        if(originBlockPos == null) return;
        int power = 0;
        Direction powerDirection = null;
        for(Direction direction : Direction.values()) {
            int j = level.getSignal(originBlockPos.relative(direction), direction);

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
        Vec3 self = this.position().add(0, 0.5 * getBbHeight(), 0);
        boolean focus = isFocus();
        if(focus && !this.getVectorSet().isEmpty()) {
            for(Vec3 vec : this.getVectorSet()) {
                position = PositionContext.create(vec);
                MagickContext context = MagickContext.create(this.level, spellContext())
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
            directionContext = DirectionContext.create(Vec3.atLowerCornerOf(direction.getOpposite().getNormal()));
            position = PositionContext.create(self);
        }

        MagickContext context = MagickContext.create(this.level, spellContext())
                .replenishChild(directionContext)
                .<MagickContext>replenishChild(position)
                .caster(this).projectile(this)
                .victim(null);
        if(focus)
            context.doBlock();
        return MagickReleaseHelper.releaseMagick(context);
    }

    public Vec3 getTracePos() {
        Vec3 vec = this.position().add(0, this.getEyeHeight(), 0);
        for(int i = 1; i<=8; ++i) {
            Vec3 end = vec.add(Vec3.atLowerCornerOf(getDirection().getNormal()).scale(i));
            BlockPos pos = new BlockPos(end);
            if(!level.isEmptyBlock(pos))
                return end;
        }

        return vec.add(Vec3.atLowerCornerOf(getDirection().getNormal()).scale(8));
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
    public InteractionResult interact(Player player, InteractionHand hand) {
        InteractionResult ret = super.interact(player, hand);
        if (ret.consumesAction()) return ret;
        this.playSound(SoundEvents.SLIME_BLOCK_PLACE, 0.5F, MagickCore.rand.nextFloat() * 2);
        ItemStack stack = player.getItemInHand(hand);
        if(stack.getItem() instanceof BlockItem || stack.getItem() instanceof EntityItem) {
            return EntityInteractHelper.placeBlock(player, hand, stack, this);
        } else if(stack.getItem() instanceof WandItem) {
            if(!isFocus()) {
                setFocus(true);
                HashSet<Vec3> vector3ds = NBTTagHelper.getVectorSet(stack.getOrCreateTagElement(WandItem.SET_KEY));
                vector3ds.removeIf(vec -> vec.distanceToSqr(this.getEyePosition()) >= 36);
                if(!vector3ds.isEmpty()) {
                    this.getVectorSet().clear();
                    this.getVectorSet().addAll(vector3ds);
                } else {
                    Vec3 vector3d = player.getLookAngle();
                    setDirection(Direction.getNearest(vector3d.x, vector3d.y, vector3d.z).getOpposite());
                }
            } else {
                setFocus(false);
                this.getVectorSet().clear();
            }
            return InteractionResult.CONSUME;
        } else if(stack.getItem() instanceof MagickContextItem) {
            spellContext().copy(ExtraDataUtil.itemManaData(stack).spellContext());
            return InteractionResult.CONSUME;
        } else if(player.isShiftKeyDown())
            spellContext().clear();

        return InteractionResult.PASS;
    }

    @Override
    public void tick() {
        this.fallDistance = 0;
        super.tick();
        if (this.tickCount == 1) {
            this.playSound(SoundEvents.SLIME_BLOCK_PLACE, 0.5F, 1.0F + MagickCore.rand.nextFloat());
        }
        if(originPos == null) {
            originBlockPos = new BlockPos(this.position());
            originPos = Vec3.atCenterOf(originBlockPos).subtract(0, getBbHeight() * 0.5, 0);
        } else
            this.setPos(originPos.x, originPos.y, originPos.z);

        List<? extends Entity> capacities = level.getEntities(this, getBoundingBox().inflate(8), entity -> entity instanceof IManaCapacity);
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
        }

        if(isFocus() && this.getVectorSet().isEmpty() && level.isClientSide()) {
            Vec3i vector3i = getDirection().getNormal();
            double scale = (0.5+random.nextFloat());
            LitParticle litPar = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                    , this.position().add(0, getEyeHeight(), 0).add(vector3i.getX()*scale, vector3i.getY()*scale, vector3i.getZ()*scale)
                    , 0.1f, 0.1f, 0.8f, 10, spellContext().element.getRenderer());
            litPar.setGlow();
            litPar.setParticleGravity(0f);
            litPar.setLimitScale();
            MagickCore.proxy.addMagickParticle(litPar);
        }
        checkRelease();
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(!(source.getEntity() instanceof Player)) return false;
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource p_21014_) {
        super.die(p_21014_);
        if(!level.isClientSide()) {
            ItemStack stack = new ItemStack(ModItems.ARTIFICIAL_LIFE.get());
            level.addFreshEntity(new ItemEntity(level, this.position().x, this.position().y+0.5, this.position().z, stack));
        }
    }

    @Nonnull
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
        if(compound.contains("focus"))
            this.setFocus(compound.getBoolean("focus"));
        if(compound.contains("direction"))
            this.setDirection(Direction.byName(compound.getString("direction")));
        deserializeBlockSet(compound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        spellContext().serialize(compound);
        compound.putBoolean("focus", isFocus());
        compound.putString("direction", getDirection().getName());
        serializeBlockSet(compound);
    }

    @Override
    public boolean refraction(SpellContext context) {
        return true;
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
        CompoundTag tag = new CompoundTag();
        NBTTagHelper.saveVectorSet(tag, this.vectorSet);
        compoundNBT.put("blockVec", tag);
    }

    public void deserializeBlockSet(CompoundTag compoundNBT) {
        if(compoundNBT.contains("blockVec")) {
            CompoundTag tag = compoundNBT.getCompound("blockVec");
            HashSet<Vec3> vector3ds = NBTTagHelper.getVectorSet(tag);
            if(!vector3ds.isEmpty()) {
                this.vectorSet.clear();
                this.vectorSet.addAll(vector3ds);
            }
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
