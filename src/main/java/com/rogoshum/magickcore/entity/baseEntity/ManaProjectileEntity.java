package com.rogoshum.magickcore.entity.baseEntity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.*;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.api.entity.IOwnerEntity;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.capability.IManaData;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.tool.EntityLightSourceHandler;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.enums.EnumManaType;
import com.rogoshum.magickcore.enums.EnumTargetType;
import com.rogoshum.magickcore.tool.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.ReleaseAttribute;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public abstract class ManaProjectileEntity extends ThrowableEntity implements IMagickElementObject, IOwnerEntity, ILightSourceEntity {
    public boolean cansee;
    protected TrailParticle trail;
    private Vector3d homePos;
    private static final DataParameter<CompoundNBT> dataUUID = EntityDataManager.createKey(ManaProjectileEntity.class, DataSerializers.COMPOUND_NBT);

    public ManaProjectileEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
        if (worldIn.isRemote)
            this.setTickTime(20);
    }

    public TrailParticle getTrail() {
        return trail;
    }

    public void setTrail(TrailParticle trail) {
        this.trail = trail;
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
    public void setShooter(Entity entityIn) {
        super.setShooter(entityIn);
        if (entityIn != null)
            this.setOwnerUUID(entityIn.getUniqueID());
    }

    @Override
    public void setOwner(Entity entityIn) {
        this.setShooter(entityIn);
    }

    @Override
    public Entity getOwner() {
        return this.func_234616_v_();
    }

    @Nullable
    @Override
    public Entity func_234616_v_() {
        Entity entity = super.func_234616_v_();

        if (entity == null && this.world.isRemote) {
            ArrayList<Entity> list = new ArrayList<>();
            ((ClientWorld) this.world).getAllEntities().forEach((list::add));

            for (Entity e : list) {
                if (e.getUniqueID().equals(getOwnerUUID()))
                    return e;
            }
        }
        return entity;
    }

    @Override
    public void tick() {
        super.tick();
        /*if (this.homePos == null)
            this.homePos = this.getPositionVec();
        else if (this.homePos.subtract(this.getPositionVec()).length() > this.getRange())
            this.remove();

         */
        if (world.isRemote)
            applyParticle();
        else
            makeSound();

        traceTarget();
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        EntityLightSourceHandler.addLightSource(this);
    }

    @Override
    public int getSourceLight() {
        return 7;
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
        return getEyeHeight();
    }

    @Override
    public float[] getColor() {
        if(this.getElement() != null && this.getElement().getRenderer() != null)
            return this.getElement().getRenderer().getColor();
        return RenderHelper.ORIGIN;
    }

    protected void makeSound() {
        if (this.ticksExisted == 1) {
            this.playSound(SoundEvents.ENTITY_ENDER_PEARL_THROW, 1.5F, 1.0F + this.rand.nextFloat());
        }
    }

    @Override
    protected void registerData() {
        this.dataManager.register(dataUUID, new CompoundNBT());
    }

    @Override
    public void setOwnerUUID(UUID uuid) {
        CompoundNBT tag = new CompoundNBT();
        tag.putUniqueId("UUID", uuid);
        this.getDataManager().set(dataUUID, tag);
    }

    public UUID getOwnerUUID() {
        CompoundNBT tag = this.getDataManager().get(dataUUID);
        if (tag.hasUniqueId("UUID"))
            return tag.getUniqueId("UUID");
        return MagickCore.emptyUUID;
    }

    protected void traceTarget() {
        if (this.getTraceTarget() != MagickCore.emptyUUID && !this.world.isRemote) {
            Entity entity = ((ServerWorld) this.world).getEntityByUuid(this.getTraceTarget());

            if (entity != null) {
                Vector3d goal = new Vector3d(entity.getPosX(), entity.getPosY() + entity.getHeight() / 1.5f, entity.getPosZ());
                Vector3d self = new Vector3d(this.getPosX(), this.getPosY(), this.getPosZ());

                Vector3d motion = goal.subtract(self).normalize().scale(this.getMotion().length() * 0.06);
                this.setMotion(motion.add(this.getMotion()));
            }
        }
    }

    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);
    }

    protected void applyParticle() {
        if (this.world.isRemote() && this.getElement() != null) {
            LitParticle par = new LitParticle(this.world, this.getElement().getRenderer().getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() + this.getPosX()
                    , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosY() + this.getHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                    , 0.1f, 0.1f, 1.0f, 40, this.getElement().getRenderer());
            par.setGlow();
            MagickCore.addMagickParticle(par);

            for (int i = 0; i < 2; ++i) {
                LitParticle litPar = new LitParticle(this.world, this.getElement().getRenderer().getMistTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                        , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosY() + this.getHeight() / 2
                        , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosZ())
                        , this.getWidth() + (this.rand.nextFloat() * this.getWidth()), this.getWidth() + (this.rand.nextFloat() * this.getWidth()), 0.8f, this.getElement().getRenderer().getParticleRenderTick(), this.getElement().getRenderer());
                litPar.setGlow();
                litPar.setParticleGravity(0f);
                litPar.setShakeLimit(15.0f);
                litPar.setLimitScale();
                MagickCore.addMagickParticle(litPar);
            }
        }
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        EntityEvents.HitEntityEvent event = new EntityEvents.HitEntityEvent(this, p_213868_1_.getEntity());
        MinecraftForge.EVENT_BUS.post(event);
        ReleaseAttribute attribute = new ReleaseAttribute(this.func_234616_v_(), this, p_213868_1_.getEntity(), this.getTickTime(), this.getForce());
        MagickReleaseHelper.applyElementFunction(this.getElement(), this.getManaType(), attribute);

        if (!this.world.isRemote) {
            this.remove();
        }
        super.onEntityHit(p_213868_1_);
    }

    @Override
    protected void func_230299_a_(BlockRayTraceResult p_230299_1_) {
        BlockState blockstate = this.world.getBlockState(p_230299_1_.getPos());
        blockstate.onProjectileCollision(this.world, blockstate, p_230299_1_, this);
        if (this.getManaData() != null)
            this.getElement().getAbility().hitBlock(this.world, p_230299_1_.getPos(), this.getTickTime());
        if (!this.world.isRemote) {
            this.remove();
        }
        super.func_230299_a_(p_230299_1_);
    }

    @Override
    public void remove() {
        if (!this.world.isRemote) {
            this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.5F, 1.0F + this.rand.nextFloat());
        }
        if (this.world.isRemote() && this.getElement() != null) {
            for (int c = 0; c < 15; ++c) {
                LitParticle par = new LitParticle(this.world, this.getElement().getRenderer().getParticleTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() + this.getPosX()
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosY() + this.getHeight() / 2
                        , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                        , 0.125f, 0.125f, MagickCore.rand.nextFloat(), 80, this.getElement().getRenderer());
                par.setGlow();
                par.setShakeLimit(15.0f);
                par.addMotion(MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10);
                MagickCore.addMagickParticle(par);
            }
            for (int i = 0; i < 5; ++i) {
                LitParticle litPar = new LitParticle(this.world, this.getElement().getRenderer().getMistTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                        , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosY() + this.getHeight() / 2
                        , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosZ())
                        , this.getWidth() + (this.rand.nextFloat() * this.getWidth()), this.getWidth() + (this.rand.nextFloat() * this.getWidth()), 0.5f * MagickCore.rand.nextFloat(), this.getElement().getRenderer().getParticleRenderTick(), this.getElement().getRenderer());
                litPar.setGlow();
                litPar.setParticleGravity(0f);
                litPar.setShakeLimit(15.0f);
                litPar.addMotion(MagickCore.getNegativeToOne() / 15, MagickCore.getNegativeToOne() / 15, MagickCore.getNegativeToOne() / 15);
                MagickCore.addMagickParticle(litPar);
            }
        }
        super.remove();
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected float getGravityVelocity() {
        return 0.005F;
    }

    @Override
    public IManaElement getElement() {
        if (this.getManaData() != null)
            return this.getManaData().getElement();
        return null;
    }

    @Override
    public void setElement(IManaElement manaElement) {
        if (this.getManaData() != null)
            this.getManaData().setElement(manaElement);
    }

    @Override
    public float getRange() {
        if (this.getManaData() != null)
            return this.getManaData().getRange();
        return 0;
    }

    @Override
    public void setRange(float range) {
        if (this.getManaData() != null)
            this.getManaData().setRange(range);
    }

    @Override
    public float getForce() {
        if (this.getManaData() != null)
            return this.getManaData().getForce();
        return 0;
    }

    @Override
    public void setForce(float force) {
        if (this.getManaData() != null)
            this.getManaData().setForce(force);
    }

    @Override
    public EnumTargetType getTargetType() {
        if (this.getManaData() != null)
            return this.getManaData().getTargetType();
        return EnumTargetType.NONE;
    }

    @Override
    public void setTargetType(EnumTargetType targetType) {
        if (this.getManaData() != null)
            this.getManaData().setTargetType(targetType);
    }

    @Override
    public EnumManaType getManaType() {
        if (this.getManaData() != null)
            return this.getManaData().getManaType();
        return EnumManaType.NONE;
    }

    @Override
    public void setManaType(EnumManaType manaType) {
        if (this.getManaData() != null)
            this.getManaData().setManaType(manaType);
    }

    @Override
    public int getTickTime() {
        if (this.getManaData() != null)
            return this.getManaData().getTickTime();
        return 0;
    }

    @Override
    public void setTickTime(int tick) {
        if (this.getManaData() != null)
            this.getManaData().setTickTime(tick);
    }

    @Override
    public UUID getTraceTarget() {
        if (this.getManaData() != null)
            return this.getManaData().getTraceTarget();
        return MagickCore.emptyUUID;
    }

    @Override
    public void setTraceTarget(UUID traceTarget) {
        if (this.getManaData() != null)
            this.getManaData().setTraceTarget(traceTarget);
    }

    @Nullable
    @Override
    public IManaData getManaData() {
        IManaData data = this.getCapability(MagickCore.manaData, null).orElse(null);
        if (data != null)
            return data;
        return null;
    }

    @Override
    public void hitMixing(IMagickElementObject a) {

    }
}
