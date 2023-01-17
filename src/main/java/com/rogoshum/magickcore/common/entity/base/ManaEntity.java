package com.rogoshum.magickcore.common.entity.base;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.render.IEasyRender;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.base.ManaEntityRenderer;
import com.rogoshum.magickcore.common.network.EntityCompoundTagPack;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.client.vertex.VectorHitReaction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ManaEntity extends Entity implements IManaEntity, ILightSourceEntity, IEntityAdditionalSpawnData {
    protected final ConcurrentHashMap<Integer, VectorHitReaction> hitReactions = new ConcurrentHashMap<>();
    public boolean cansee;
    public boolean initial;
    private final SpellContext spellContext = SpellContext.create();
    private UUID owner_uuid;
    private int owner_id;
    private static final DataParameter<Optional<UUID>> dataUUID = EntityDataManager.defineId(ManaEntity.class, DataSerializers.OPTIONAL_UUID);
    private static final DataParameter<Integer> DAMAGE = EntityDataManager.defineId(ManaEntity.class, DataSerializers.INT);
    private static final DataParameter<Float> HEIGHT = EntityDataManager.defineId(ManaEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> WIDTH = EntityDataManager.defineId(ManaEntity.class, DataSerializers.FLOAT);
    private int lastDamageTick;

    public ManaEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        this.entityData.define(dataUUID, Optional.of(MagickCore.emptyUUID));
        this.entityData.define(DAMAGE, 3);
        this.entityData.define(HEIGHT, this.getType().getHeight());
        this.entityData.define(WIDTH, this.getType().getWidth());
    }

    @Override
    protected float getEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return sizeIn.height * -0.5f;
    }
    @Override
    public void onSyncedDataUpdated(DataParameter<?> key) {
        if (HEIGHT.equals(key) || WIDTH.equals(key)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(key);
    }

    public void refreshDimensions() {
        EntitySize entitysize = ObfuscationReflectionHelper.getPrivateValue(Entity.class, this, "dimensions");
        Pose pose = this.getPose();
        EntitySize entitysize1 = this.getDimensions(pose);
        net.minecraftforge.event.entity.EntityEvent.Size sizeEvent = net.minecraftforge.event.ForgeEventFactory.getEntitySizeForge(this, pose, entitysize, entitysize1, this.getEyeHeight(pose, entitysize1));
        entitysize1 = sizeEvent.getNewSize();
        ObfuscationReflectionHelper.setPrivateValue(Entity.class, this, entitysize1,  "dimensions");
        ObfuscationReflectionHelper.setPrivateValue(Entity.class, this, sizeEvent.getNewEyeHeight(),  "eyeHeight");
        double d0 = (double)entitysize1.width * 0.5;
        //double d1 = (entitysize1.height - entitysize.height) * 0.5;
        this.setBoundingBox(new AABB(this.getX() - d0, this.getY(), this.getZ() - d0, this.getX() + d0, this.getY() + (double)entitysize1.height, this.getZ() + d0));
    }

    @Override
    public boolean spawnGlowBlock() {
        return true;
    }

    @Override
    public EntitySize getDimensions(Pose poseIn) {
        return EntitySize.scalable(this.getEntityData().get(WIDTH), this.getEntityData().get(HEIGHT));
    }

    public void setHeight(float height) {
        this.getEntityData().set(HEIGHT, height);
    }
    public void setWidth(float width) {
        this.getEntityData().set(WIDTH, width);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        CompoundTag addition = new CompoundTag();
        addAdditionalSaveData(addition);
        buffer.writeNbt(addition);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void handleEntityEvent(byte id) {
        if(id == 3)
            this.remove();
        else
            super.handleEntityEvent(id);
    }

    @Override
    public SpellContext spellContext() {
        return spellContext;
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        readAdditionalSaveData(additionalData.readNbt());
    }

    @Override
    public void setOwner(@Nullable Entity entityIn) {
        if (entityIn != null) {
            this.owner_uuid = entityIn.getUUID();
            this.owner_id = entityIn.getId();
            this.setOwnerUUID(entityIn.getUUID());
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void setRemainingFireTicks(int ticks) {
    }

    @Override
    public int getRemainingFireTicks() {
        return 0;
    }

    @Override
    @Nullable
    public Entity getOwner() {
        if (this.owner_uuid != null && this.level instanceof ServerLevel) {
            return ((ServerLevel)this.level).getEntity(this.owner_uuid);
        } else if(this.level instanceof ServerLevel){
            return this.owner_id != 0 ? this.level.getEntity(this.owner_id) : null;
        }
        else{
            ArrayList<Entity> list = new ArrayList<>();
            ((ClientLevel)this.level).entitiesForRendering().forEach((list::add));

            for (Entity entity : list)
            {
                if(entity.getUUID().equals(getOwnerUUID()))
                    return entity;
            }

            return null;
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.GENERIC_HURT;
    }

    protected void playHurtSound(DamageSource source) {
        SoundEvent soundevent = this.getHurtSound(source);
        if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    protected float getSoundVolume() {
        return 1.0F;
    }

    /**
     * Gets the pitch of living sounds in living entities.
     */
    protected float getSoundPitch() {
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    @Override
    public boolean isAlive() {
        return !this.removed && this.getDamage() > 0;
    }

    public void damageEntity() {
        if(this.tickCount - lastDamageTick < 10) return;
        playHurtSound(null);
        setDamage(getDamage() - 1);
        lastDamageTick = this.tickCount;
        if(!level.isClientSide && getDamage() <= 0) {
            this.level.broadcastEntityEvent(this, (byte) 3);
            if(!removed)
                this.remove();
        }
    }

    public void setDamage(int damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public int getDamage() {
       return this.entityData.get(DAMAGE);
    }

    @Override
    public void setOwnerUUID(UUID uuid) {
        this.getEntityData().set(dataUUID, Optional.of(uuid));
    }

    public UUID getOwnerUUID() {
        try{
            if(this.getEntityData().get(dataUUID).isPresent())
                return this.getEntityData().get(dataUUID).get();
        }
        catch (Exception ignored)
        {
        }
        return MagickCore.emptyUUID;
    }

    public ManaEntity(EntityType<?> entityTypeIn, World worldIn, MagickElement manaElement) {
        super(entityTypeIn, worldIn);
        this.spellContext().element(manaElement);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 vector3d = this.getDeltaMovement();
        double d2 = this.getX() + vector3d.x;
        double d0 = this.getY() + vector3d.y;
        double d1 = this.getZ() + vector3d.z;
        this.setDeltaMovement(vector3d.scale(0.9));
        this.setPos(d2, d0, d1);
        collideWithNearbyEntities();
        if(!this.level.isClientSide)
            makeSound();
        if(this.level.isClientSide) {
            MagickCore.proxy.addTask(this::doClientTask);
        } else
            MagickCore.proxy.addTask(this::doServerTask);
        if(getDamage() > 0 && getDamage() < 3 && this.tickCount - lastDamageTick > 200)
            setDamage(getDamage() + 1);
        reSize();
        releaseMagick();
    }

    public void reSize() {
        float height = getType().getHeight() + spellContext().range * 0.1f;
        if(getBbHeight() != height)
            this.setHeight(height);
        float width = getType().getWidth() + spellContext().range * 0.1f;
        if(getBbWidth() != width)
            this.setWidth(width);
    }

    protected abstract void applyParticle();

    protected void doClientTask() {
        applyParticle();
        hitReactions.values().removeIf((reaction) -> {
            reaction.tick();
            return reaction.isInvalid();
        });
    }

    protected void doServerTask() {
    }

    protected void makeSound() {}

    public VectorHitReaction[] getHitReactions() {
        Map<Integer, VectorHitReaction> hitReactions = new ConcurrentHashMap<>(this.hitReactions);
        return hitReactions.values().stream().filter(Objects::nonNull).toArray(VectorHitReaction[]::new);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    protected void collideWithNearbyEntities() {
        List<Entity> list = this.level.getEntities(this, this.getBoundingBox(), EntityPredicates.pushableBy(this));
        if (!list.isEmpty()) {
            for(int l = 0; l < list.size(); ++l) {
                Entity entity = list.get(l);
                //this.applyEntityCollision(entity);
            }
        }

    }
    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            this.owner_uuid = compound.getUUID("Owner");
            this.setOwnerUUID(this.owner_uuid);
        }
        spellContext().deserialize(compound);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.owner_uuid != null) {
            compound.putUUID("Owner", this.owner_uuid);
        }
        spellContext().serialize(compound);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        EntityLightSourceManager.addLightSource(this);
        if(level.isClientSide) {
            Supplier<EasyRenderer<? extends ManaEntity>> renderer = getRenderer();
            if(renderer == null)
                MagickCore.proxy.addRenderer(() -> new ManaEntityRenderer(this));
            else
                MagickCore.proxy.addRenderer(renderer::get);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return null;
    }

    @Override
    public float getSourceLight() {
        return this.getBbWidth() * 1.5f;
    }

    @Override
    public boolean alive() {
        return isAlive();
    }

    @Override
    public Vec3 positionVec() {
        return position();
    }

    @Override
    public Level world() {
        return getCommandSenderWorld();
    }

    @Override
    public float eyeHeight() {
        return this.getBbHeight() * 0.5f;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public Color getColor() {
        return this.spellContext().element.color();
    }

    @Override
    public AABB boundingBox() {
        return getBoundingBox();
    }
}
