package com.rogoshum.magickcore.entity.base;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.magick.Color;
import com.rogoshum.magickcore.magick.MagickElement;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.context.SpellContext;
import com.rogoshum.magickcore.tool.EntityLightSourceHandler;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ManaEntity extends Entity implements IManaEntity, ILightSourceEntity, IEntityAdditionalSpawnData {
    protected final ConcurrentHashMap<Integer, VectorHitReaction> hitReactions = new ConcurrentHashMap<>();
    public boolean cansee;
    protected TrailParticle trail;
    public boolean initial;
    private final SpellContext spellContext = SpellContext.create();
    private UUID owner_uuid;
    private int owner_id;
    private static final DataParameter<Optional<UUID>> dataUUID = EntityDataManager.createKey(ManaEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private static final DataParameter<Integer> DAMAGE = EntityDataManager.createKey(ManaEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Float> HEIGHT = EntityDataManager.createKey(ManaEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> WIDTH = EntityDataManager.createKey(ManaEntity.class, DataSerializers.FLOAT);
    private int lastDamageTick;

    public ManaEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.dataManager.register(dataUUID, Optional.of(MagickCore.emptyUUID));
        this.dataManager.register(DAMAGE, 3);
        this.dataManager.register(HEIGHT, this.getType().getHeight());
        this.dataManager.register(WIDTH, this.getType().getWidth());
    }

    @Override
    protected float getEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return sizeIn.height * -0.5f;
    }
    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (HEIGHT.equals(key) || WIDTH.equals(key)) {
            this.recalculateSize();
        }
        super.notifyDataManagerChange(key);
    }

    @Override
    public EntitySize getSize(Pose poseIn) {
        return EntitySize.flexible(this.getDataManager().get(WIDTH), this.getDataManager().get(HEIGHT));
    }

    public void setHeight(float height) {
        this.getDataManager().set(HEIGHT, height);
    }
    public void setWidth(float width) {
        this.getDataManager().set(WIDTH, width);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        CompoundNBT addition = new CompoundNBT();
        writeAdditional(addition);
        buffer.writeCompoundTag(addition);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleStatusUpdate(byte id) {
        if(id == 3)
            this.remove();
        else
            super.handleStatusUpdate(id);
    }

    @Override
    public SpellContext spellContext() {
        return spellContext;
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        readAdditional(additionalData.readCompoundTag());
    }

    @Override
    public void setOwner(@Nullable Entity entityIn) {
        if (entityIn != null) {
            this.owner_uuid = entityIn.getUniqueID();
            this.owner_id = entityIn.getEntityId();
            this.setOwnerUUID(entityIn.getUniqueID());
        }
    }

    @Override
    public boolean isImmuneToFire() {
        return true;
    }

    @Override
    public void forceFireTicks(int ticks) {
    }

    @Override
    public int getFireTimer() {
        return 0;
    }

    @Override
    @Nullable
    public Entity getOwner() {
        if (this.owner_uuid != null && this.world instanceof ServerWorld) {
            return ((ServerWorld)this.world).getEntityByUuid(this.owner_uuid);
        } else if(this.world instanceof ServerWorld){
            return this.owner_id != 0 ? this.world.getEntityByID(this.owner_id) : null;
        }
        else{
            ArrayList<Entity> list = new ArrayList<>();
            ((ClientWorld)this.world).getAllEntities().forEach((list::add));

            for (Entity entity : list)
            {
                if(entity.getUniqueID().equals(getOwnerUUID()))
                    return entity;
            }

            return null;
        }
    }

    public TrailParticle getTrail() {return trail;}
    public void setTrail(TrailParticle trail) {this.trail = trail;}

    @Override
    protected void registerData() {

    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_GENERIC_HURT;
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
        return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
    }

    @Override
    public boolean isAlive() {
        if(!world.isRemote && getDamage() <= 0) {
            this.world.setEntityState(this, (byte) 3);
            if(!removed)
                this.remove();
        }
        return !this.removed && this.getDamage() > 0;
    }

    public void damageEntity() {
        if(this.ticksExisted - lastDamageTick < 10) return;
        playHurtSound(null);
        setDamage(getDamage() - 1);
        lastDamageTick = this.ticksExisted;
    }

    public void setDamage(int damage) {
        this.dataManager.set(DAMAGE, damage);
    }

    public int getDamage() {
       return this.dataManager.get(DAMAGE);
    }

    @Override
    public void setOwnerUUID(UUID uuid) {
        this.getDataManager().set(dataUUID, Optional.of(uuid));
    }

    public UUID getOwnerUUID() {
        try{
            if(this.getDataManager().get(dataUUID).isPresent())
                return this.getDataManager().get(dataUUID).get();
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
        collideWithNearbyEntities();
        if(!this.world.isRemote)
            makeSound();
        if(this.world.isRemote) {
            MagickCore.proxy.addTask(this::doClientTask);
        } else
            MagickCore.proxy.addTask(this::doServerTask);
        if(getDamage() < 3 && this.ticksExisted - lastDamageTick > 200)
            setDamage(getDamage() + 1);

        releaseMagick();
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
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }

    protected void collideWithNearbyEntities() {
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), EntityPredicates.pushableBy(this));
        if (!list.isEmpty()) {
            for(int l = 0; l < list.size(); ++l) {
                Entity entity = list.get(l);
                //this.applyEntityCollision(entity);
            }
        }

    }
    @Override
    protected void readAdditional(CompoundNBT compound) {
        if (compound.hasUniqueId("Owner")) {
            this.owner_uuid = compound.getUniqueId("Owner");
            this.setOwnerUUID(this.owner_uuid);
        }
        spellContext().deserialize(compound);
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        if (this.owner_uuid != null) {
            compound.putUniqueId("Owner", this.owner_uuid);
        }
        spellContext().serialize(compound);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        EntityLightSourceHandler.addLightSource(this);
    }

    @Override
    public float getSourceLight() {
        return this.getWidth() * 1.5f;
    }

    @Override
    public boolean alive() {
        return isAlive();
    }

    @Override
    public Vector3d positionVec() {
        return getPositionVec();
    }

    @Override
    public World world() {
        return getEntityWorld();
    }

    @Override
    public float eyeHeight() {
        return this.getHeight() / 2;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public Color getColor() {
        return this.spellContext().element.color();
    }
}
