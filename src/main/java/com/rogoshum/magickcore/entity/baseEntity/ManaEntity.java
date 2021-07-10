package com.rogoshum.magickcore.entity.baseEntity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.EnumManaType;
import com.rogoshum.magickcore.api.EnumTargetType;
import com.rogoshum.magickcore.api.IMagickElementObject;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.capability.IManaData;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.element.MagickElement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.*;

public abstract class ManaEntity extends Entity implements IMagickElementObject {
    protected HashMap<Integer, VectorHitReaction> hitReactions = new HashMap<Integer, VectorHitReaction>();
    public boolean cansee;
    protected TrailParticle trail;
    protected HashMap<Integer, TrailParticle> traceEntity = new HashMap<Integer, TrailParticle>();

    private UUID owner_uuid;
    private int owner_id;

    public ManaEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        if(worldIn.isRemote)
            this.setTickTime(20);
    }

    public void setOwner(@Nullable Entity entityIn) {
        if (entityIn != null) {
            this.owner_uuid = entityIn.getUniqueID();
            this.owner_id = entityIn.getEntityId();
        }
    }

    @Nullable
    public Entity getOwner() {
        if (this.owner_uuid != null && this.world instanceof ServerWorld) {
            return ((ServerWorld)this.world).getEntityByUuid(this.owner_uuid);
        } else {
            return this.owner_id != 0 ? this.world.getEntityByID(this.owner_id) : null;
        }
    }

    public TrailParticle getTrail() {return trail;}
    public void setTrail(TrailParticle trail) {this.trail = trail;}

    public List<Entity> getEntity(double scale)
    {
        return this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().grow(scale));
    }

    public List<LivingEntity> getLivingEntity(double scale)
    {
        List<LivingEntity> living = new ArrayList<>();

        for(Entity entity : getEntity(scale))
        {
            if(entity instanceof LivingEntity)
                living.add((LivingEntity) entity);
        }
        return living;
    }

    public TrailParticle createTrail(Entity entity, Vector3d vec, float spaceing, int length)
    {
        TrailParticle trail = new TrailParticle(this, vec, (int)(this.getPositionVec().subtract(entity.getPositionVec()).length() * length), spaceing);
        Vector3d vec3 = this.getPositionVec().add(0, this.getHeight(), 0).subtract(entity.getPositionVec().add(0, entity.getHeight() / 2, 0));
        trail.setMotion(vec3.scale(100));
        trail.tick();
        return trail;
    }

    public HashMap<Integer, TrailParticle> getTraceEntity()
    {
        return this.traceEntity;
    }

    public void traceEntity(Class clas, double scale, Vector3d vec, float spaceing, int length)
    {
        List<LivingEntity> living = this.getLivingEntity(scale);
        HashMap<Integer, TrailParticle> traceEntity = new HashMap<Integer, TrailParticle>();
        if(clas != null)
        {
            for(LivingEntity entity : living)
            {
                if(entity.getClass() == clas)
                {
                    traceEntity.put(entity.getEntityId(), createTrail(entity, vec, spaceing, length));
                }
            }
        }
        else
        {
            for(LivingEntity entity : living)
            {
                TrailParticle trail = createTrail(entity, vec, spaceing, length);

                traceEntity.put(entity.getEntityId(), trail);
            }
        }
        this.traceEntity = traceEntity;
    }

    @Override
    protected void registerData() {

    }

    public ManaEntity(EntityType<?> entityTypeIn, World worldIn, IManaElement manaElement) {
        super(entityTypeIn, worldIn);
        if(this.getManaData() != null)
            this.setElement(manaElement);
    }

    @Override
    public void tick() {
        super.tick();
        collideWithNearbyEntities();
    }

    public VectorHitReaction[] getHitReactions()
    {
        int length = hitReactions.size();
        int number = 0;

        VectorHitReaction[] reactions = new VectorHitReaction[length];

        Iterator<Integer> iter = hitReactions.keySet().iterator();
        while (iter.hasNext()) {
            VectorHitReaction reaction = hitReactions.get(iter.next());
            reactions[number] = reaction;
            number++;
        }

        return reactions;
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
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        if (this.owner_uuid != null) {
            compound.putUniqueId("Owner", this.owner_uuid);
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public IManaElement getElement() { if(this.getManaData() != null) return this.getManaData().getElement(); return null; }

    @Override
    public void setElement(IManaElement manaElement) {
        if(this.getManaData() != null)
            this.getManaData().setElement(manaElement);
    }

    @Override
    public float getRange() {
        if(this.getManaData() != null)
            return this.getManaData().getRange();
        return 0;
    }

    @Override
    public void setRange(float range) {
        if(this.getManaData() != null)
            this.getManaData().setRange(range);
    }

    @Override
    public float getForce() {
        if(this.getManaData() != null)
            return  this.getManaData().getForce();
        return 0;
    }

    @Override
    public void setForce(float force) {
        if(this.getManaData() != null)
            this.getManaData().setForce(force);
    }

    @Override
    public EnumTargetType getTargetType() {
        if(this.getManaData() != null)
            return this.getManaData().getTargetType();
        return EnumTargetType.NONE;
    }

    @Override
    public void setTargetType(EnumTargetType targetType) {
        if(this.getManaData() != null)
            this.getManaData().setTargetType(targetType);
    }

    @Override
    public EnumManaType getManaType() {
        if(this.getManaData() != null)
            return this.getManaData().getManaType();
        return EnumManaType.NONE;
    }

    @Override
    public void setManaType(EnumManaType manaType) {
        if(this.getManaData() != null)
            this.getManaData().setManaType(manaType);
    }

    @Override
    public int getTickTime() {
        if(this.getManaData() != null)
            return this.getManaData().getTickTime();
        return 0;
    }

    @Override
    public void setTickTime(int tick) {
        if(this.getManaData() != null)
            this.getManaData().setTickTime(tick);
    }

    @Override
    public UUID getTraceTarget() {
        if(this.getManaData() != null)
            return this.getManaData().getTraceTarget();
        return MagickCore.emptyUUID;
    }

    @Override
    public void setTraceTarget(UUID traceTarget) {
        if(this.getManaData() != null)
            this.getManaData().setTraceTarget(traceTarget);
    }

    @Nullable
    @Override
    public IManaData getManaData() {
        IManaData data = this.getCapability(MagickCore.manaData, null).orElse(null);
        if(data != null)
            return data;
        return null;
    }

    @Override
    public void hitMixing(IMagickElementObject a) {

    }
}
