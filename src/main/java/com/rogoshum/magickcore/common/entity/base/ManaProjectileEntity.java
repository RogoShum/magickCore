package com.rogoshum.magickcore.common.entity.base;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.base.ManaProjectileRenderer;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.api.magick.ManaFactor;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.magick.context.child.*;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.api.magick.context.SpellContext;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.api.enums.ApplyType;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class ManaProjectileEntity extends ThrowableProjectile implements IManaEntity, ILightSourceEntity, IEntityAdditionalSpawnData {
    private final SpellContext spellContext = SpellContext.create();
    private static final EntityDataAccessor<Optional<UUID>> dataUUID = SynchedEntityData.defineId(ManaProjectileEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(ManaProjectileEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> WIDTH = SynchedEntityData.defineId(ManaProjectileEntity.class, EntityDataSerializers.FLOAT);
    public Entity victim;
    public double maxMotion;
    private boolean released = false;
    private int shootCount;

    public ManaProjectileEntity(EntityType<? extends ThrowableProjectile> type, Level worldIn) {
        super(type, worldIn);
        this.entityData.define(dataUUID, Optional.of(MagickCore.emptyUUID));
        this.entityData.define(HEIGHT, this.getType().getHeight());
        this.entityData.define(WIDTH, this.getType().getWidth());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (HEIGHT.equals(key) || WIDTH.equals(key)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(key);
    }

    public void refreshDimensions() {
        EntityDimensions entitysize = ObfuscationReflectionHelper.getPrivateValue(Entity.class, this, "f_19815_");
        Pose pose = this.getPose();
        EntityDimensions entitysize1 = this.getDimensions(pose);
        net.minecraftforge.event.entity.EntityEvent.Size sizeEvent = net.minecraftforge.event.ForgeEventFactory.getEntitySizeForge(this, pose, entitysize, entitysize1, this.getEyeHeight(pose, entitysize1));
        entitysize1 = sizeEvent.getNewSize();
        ObfuscationReflectionHelper.setPrivateValue(Entity.class, this, entitysize1,  "f_19815_");
        ObfuscationReflectionHelper.setPrivateValue(Entity.class, this, sizeEvent.getNewEyeHeight(),  "f_19816_");
        double d0 = (double)entitysize1.width * 0.5;
        //double d1 = (entitysize1.height - entitysize.height) * 0.5;
        this.setBoundingBox(new AABB(this.getX() - d0, this.getY(), this.getZ() - d0, this.getX() + d0, this.getY() + (double)entitysize1.height, this.getZ() + d0));
    }

    @Override
    public EntityDimensions getDimensions(Pose poseIn) {
        return EntityDimensions.scalable(this.getEntityData().get(WIDTH), this.getEntityData().get(HEIGHT));
    }

    public void setHeight(float height) {
        this.getEntityData().set(HEIGHT, height);
    }
    public void setWidth(float width) {
        this.getEntityData().set(WIDTH, width);
    }

    @Override
    protected float getEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return sizeIn.height * -0.5f;
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        List<Entity> entities = new ArrayList<>();
        if(victim != null && (predicate == null || predicate.test(victim)))
            entities.add(victim);
        return entities;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        CompoundTag addition = new CompoundTag();
        addAdditionalSaveData(addition);
        buffer.writeNbt(addition);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte id) {
        if(id == 3)
            this.remove(RemovalReason.DISCARDED);
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
    public void setCaster(Entity entityIn) {
        super.setOwner(entityIn);
        if (entityIn != null)
            this.setCasterUUID(entityIn.getUUID());
    }

    @Nullable
    @Override
    public Entity getCaster() {
        Entity entity = super.getOwner();
        UUID uuid = getOwnerUUID();
        if(uuid == MagickCore.emptyUUID) return entity;
        if (entity == null && this.level.isClientSide) {
            Iterator<Entity> it = ((ClientLevel) this.level).entitiesForRendering().iterator();
            while (it.hasNext()) {
                Entity entity1 = it.next();
                if(entity1 != null && entity1.getUUID().equals(uuid)) {
                    setCaster(entity1);
                    return entity1;
                }
            }
        }
        return entity;
    }

    protected void shootCheck() {
        if(spellContext().containChild(LibContext.TRACE) && this.getCaster() != null)
            shoot(getCaster());
        else {
            double range = 8 + 4 * spellContext().range();
            List<LivingEntity> livings = level.getEntitiesOfClass(LivingEntity.class, this.boundingBox().inflate(range), (entity -> entity.isAlive() && entity != this.getCaster()));
            Optional<LivingEntity> living = livings.stream().filter((entity) -> entity.hasLineOfSight(this)).min(Comparator.comparing((entity -> entity.distanceToSqr(this))));
            living.ifPresent(this::shoot);
        }
    }

    protected void shoot(Entity entity) {
        MagickContext context = MagickContext.create(level, spellContext()).caster(this.getCaster()).victim(entity).projectile(this).noCost()
                .applyType(ApplyType.SPAWN_ENTITY).tick(spellContext().tick()).force(spellContext().force() * 0.2f).range(spellContext().range() * 0.2f);
        context.removeChild(LibContext.SELF);
        context.addChild(PositionContext.create(this.positionVec()));
        context.addChild(ExtraManaFactorContext.create(ManaFactor.create(0.2f, 0.2f, 0.25f)));
        context.addChild(DirectionContext.create(IManaEntity.super.getPostDirection(entity)));
        context.addChild(TraceContext.create(entity));
        MagickReleaseHelper.releaseMagick(context);
    }

    @Override
    public void tick() {
        if(spellContext().containChild(LibContext.SELF)) {
            baseTick();
            float height = 0.3f;
            if(getBbHeight() != height)
                this.setHeight(height);
            float width = 0.3f;
            if(getBbWidth() != width)
                this.setWidth(width);

            if(this.getCaster() != null) {
                Vec3 rota = Vec3.directionFromRotation(getId() + (tickCount % 180) * 2, 90);
                Vec3 dire = this.getCaster().position().add(0, getCaster().getBbHeight() * 0.5, 0)
                        .subtract(rota.x * getCaster().getBbWidth() * 1.5, 0, rota.y * getCaster().getBbWidth() * 1.5).subtract(this.positionVec()).scale(0.2);
                this.setDeltaMovement(dire);
                Vec3 pos = getDeltaMovement().add(this.position());
                this.setPos(pos.x, pos.y, pos.z);
            }

            if(!level.isClientSide()) {
                int force = Math.max(5, (80 - (int) spellContext().force() *5));
                if(this.tickCount == 1 || this.tickCount % force == 0) {
                    shootCount+=1;
                }

                if(tickCount % 5 == 0 && shootCount > 0) {
                    shootCount--;
                    shootCheck();
                }
            } else {
                float scale = Math.max(this.getBbWidth(), 0.3f) * 0.3f;
                Vec3 pos =  new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() * 0.25 + this.getX()
                        , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.25 + this.getY() + this.getBbHeight() / 2
                        , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.25 + this.getZ());
                LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleTexture()
                        , pos
                        , scale, scale, 0.5f, 15, MagickCore.proxy.getElementRender(spellContext().element().type()));
                par.setGlow();
                par.setParticleGravity(0f);
                par.setLimitScale();
                par.setShakeLimit(5f);
                MagickCore.addMagickParticle(par);
            }

            return;
        }
        victim = null;
        super.tick();
        normalCollision();
        if (!level.isClientSide)
            makeSound();

        traceTarget();
        reSize();
        if(this.level.isClientSide) {
            MagickCore.proxy.addTask(this::doClientTask);
        } else
            MagickCore.proxy.addTask(this::doServerTask);
        double length = getDeltaMovement().length();
        if(length > maxMotion)
            maxMotion = length;
    }

    public void normalCollision() {
        if(victim == null) {
            Vec3 vector3d = this.getDeltaMovement();
            vector3d = vector3d.normalize().scale(getBbWidth() * 0.5);
            Level world = this.level;
            Vec3 vector3d1 = this.position().add(0, this.getBbHeight()*0.5, 0);
            Vec3 vector3d2 = vector3d1.add(vector3d);
            EntityHitResult raytraceResult = ProjectileUtil.getEntityHitResult(world, this, vector3d1, vector3d2, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), Entity::isAlive);
            if (raytraceResult != null) {
                Entity target = raytraceResult.getEntity();
                if(target instanceof IManaRefraction) {
                    if(!((IManaRefraction) target).refraction(spellContext()))
                        this.victim = target;
                } else
                    this.victim = target;
                if (victim != null && !this.level.isClientSide) {
                    EntityHitResult result = new EntityHitResult(target);
                    onHitEntity(result);
                }
            }
        }
    }

    public void reSize() {
        float height = getType().getHeight() + spellContext().range() * 0.1f;
        if(getBbHeight() != height)
            this.setHeight(height);
        float width = getType().getWidth() + spellContext().range() * 0.1f;
        if(getBbWidth() != width)
            this.setWidth(width);
    }

    protected void doClientTask() {
        applyParticle();
    }

    protected void doServerTask() {

    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        EntityLightSourceManager.addLightSource(this);
        if(level.isClientSide) {
            Supplier<EasyRenderer<? extends ManaProjectileEntity>> renderer = getRenderer();
            if(renderer == null)
                MagickCore.proxy.addRenderer(() -> new ManaProjectileRenderer(this));
            else
                MagickCore.proxy.addRenderer(renderer::get);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaProjectileEntity>> getRenderer() {
        return null;
    }

    @Override
    public float getSourceLight() {
        return 7;
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
    public Color getColor() {
        return this.spellContext().element().primaryColor();
    }

    protected void makeSound() {
        if (this.tickCount == 1) {
            this.playSound(ModSounds.glitter_another.get(), 0.25F, 1.0F + MagickCore.rand.nextFloat());
        }
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.NEUTRAL;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void setCasterUUID(UUID uuid) {
        this.getEntityData().set(dataUUID, Optional.of(uuid));
    }

    public UUID getOwnerUUID() {
        Optional<UUID> uuid = this.getEntityData().get(dataUUID);
        return uuid.orElse(MagickCore.emptyUUID);
    }

    protected void traceTarget() {
        if (!this.spellContext().containChild(LibContext.TRACE) || this.level.isClientSide) return;
        TraceContext traceContext = spellContext().getChild(LibContext.TRACE);
        Entity entity = traceContext.entity;
        if(entity == null && traceContext.uuid != MagickCore.emptyUUID) {
            entity = ((ServerLevel) this.level).getEntity(traceContext.uuid);
            traceContext.entity = entity;
        } else if(entity != null && entity.isAlive()) {
            Vec3 goal = new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() / 1.5f, entity.getZ());
            Vec3 self = new Vec3(this.getX(), this.getY(), this.getZ());

            double length = maxMotion * 0.3;
            Vec3 motion = goal.subtract(self).normalize().scale(Math.max(length * 0.2, 0.02));
            this.setDeltaMovement(motion.add(this.getDeltaMovement().scale(0.8)));
        }
    }

    protected void onHit(HitResult result) {
        if(result.getType() == HitResult.Type.ENTITY) {
            EntityHitResult result1 = ((EntityHitResult)result);
            if(result1.getEntity() instanceof IManaRefraction) {
                if(!((IManaRefraction) result1.getEntity()).refraction(spellContext()))
                    this.victim = result1.getEntity();
            } else
                this.victim = result1.getEntity();
        }
        super.onHit(result);
    }

    @OnlyIn(Dist.CLIENT)
    protected void applyParticle() {
        LitParticle par = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element().type()).getParticleTexture()
                , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getX()
                , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getY() + this.getBbHeight() * 0.5
                , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getZ())
                , 0.1f, 0.1f, 1.0f, 40, MagickCore.proxy.getElementRender(spellContext().element().type()));
        par.setGlow();
        MagickCore.addMagickParticle(par);

        if(tickCount % 5 != 0) return;
        LitParticle litPar = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element().type()).getMistTexture()
                , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getX()
                , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getY() + this.getBbHeight() * 0.5
                , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getZ())
                , 0.5f * this.getBbWidth(), 0.5f * this.getBbWidth(), 0.8f, spellContext().element().getRenderer().getParticleRenderTick(), spellContext().element().getRenderer());
        litPar.setGlow();
        litPar.setParticleGravity(0f);
        litPar.setShakeLimit(5.0f);
        litPar.setLimitScale();
        MagickCore.addMagickParticle(litPar);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("Owner")) {
            UUID ownerUUID = compound.getUUID("Owner");
            this.setCasterUUID(ownerUUID);
        }
        spellContext().deserialize(compound);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getCaster() != null) {
            compound.putUUID("Owner", this.getCaster().getUUID());
        }
        spellContext().serialize(compound);
    }

    @Override
    protected void onHitEntity(EntityHitResult p_213868_1_) {
        if(suitableEntity(p_213868_1_.getEntity())) {
            ConditionContext condition = null;
            if(spellContext().containChild(LibContext.CONDITION))
                condition = spellContext().getChild(LibContext.CONDITION);
            AtomicReference<Boolean> pass = new AtomicReference<>(true);
            if(condition != null) {
                if(!condition.test(this.getCaster(), p_213868_1_.getEntity()))
                    pass.set(false);
            }
            if(pass.get()) {
                EntityEvents.HitEntityEvent event = new EntityEvents.HitEntityEvent(this, p_213868_1_.getEntity());
                MinecraftForge.EVENT_BUS.post(event);
            }
        }

        if (victim != null && !this.level.isClientSide) {
            boolean success = releaseMagick();
            if(success && hitEntityRemove(p_213868_1_)) {
                released = true;
                this.remove(RemovalReason.DISCARDED);
            }
        }
        super.onHitEntity(p_213868_1_);
    }

    @Override
    protected void onHitBlock(BlockHitResult p_230299_1_) {
        if(spellContext().containChild(LibContext.CONDITION)) {
            ConditionContext condition = spellContext().getChild(LibContext.CONDITION);
            if(!condition.<Block>test(null, level.getBlockState(p_230299_1_.getBlockPos()).getBlock()))
                return;
        }
        BlockState blockstate = this.level.getBlockState(p_230299_1_.getBlockPos());
        blockstate.onProjectileHit(this.level, blockstate, p_230299_1_, this);
        MagickContext context = MagickContext.create(level, spellContext().postContext()).<MagickContext>applyType(ApplyType.HIT_BLOCK).noCost().caster(this.getCaster()).projectile(this);
        PositionContext positionContext = PositionContext.create(Vec3.atLowerCornerOf(p_230299_1_.getBlockPos()));
        context.addChild(positionContext);
        MagickReleaseHelper.releaseMagick(beforeCast(context));

        context = MagickContext.create(level, spellContext().postContext()).doBlock().noCost().caster(this.getCaster()).projectile(this);
        context.addChild(positionContext);
        MagickReleaseHelper.releaseMagick(beforeCast(context));

        if (hitBlockRemove(p_230299_1_) && !this.level.isClientSide) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public void remove(RemovalReason removalReason) {
        if(victim == null)
            victim = this;
        if(!released)
            releaseMagick();
        removeEffect();
        if(!level.isClientSide)
            level.broadcastEntityEvent(this, (byte) 3);
        super.remove(removalReason);
    }

    @Override
    public Vec3 getPostDirection(Entity entity) {
        if(entity == this) {
            if(spellContext().containChild(LibContext.DIRECTION))
                return spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction;
        }
        return IManaEntity.super.getPostDirection(entity);
    }

    public void removeEffect() {
        if (!this.level.isClientSide) {
            this.playSound(SoundEvents.ENDER_EYE_DEATH, 0.5F, 1.0F + this.random.nextFloat());
        }
        if (this.level.isClientSide()) {
            for (int c = 0; c < 10; ++c) {
                LitParticle par = new LitParticle(this.level, spellContext().element().getRenderer().getParticleTexture()
                        , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getX()
                        , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getY() + this.getBbHeight() * 0.5
                        , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getZ())
                        , 0.125f, 0.125f, MagickCore.rand.nextFloat(), 80, spellContext().element().getRenderer());
                par.setGlow();
                par.setShakeLimit(5.0f);
                par.addMotion(MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10);
                MagickCore.addMagickParticle(par);
            }
            for (int i = 0; i < 5; ++i) {
                LitParticle litPar = new LitParticle(this.level, spellContext().element().getRenderer().getMistTexture()
                        , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getX()
                        , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getY() + this.getBbHeight() * 0.5
                        , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getZ())
                        , this.getBbWidth() * 0.5f, this.getBbWidth() * 0.5f, 0.5f * MagickCore.rand.nextFloat(), spellContext().element().getRenderer().getParticleRenderTick(), spellContext().element().getRenderer());
                litPar.setGlow();
                litPar.setParticleGravity(0f);
                litPar.setShakeLimit(5.0f);
                litPar.addMotion(MagickCore.getNegativeToOne() / 15, MagickCore.getNegativeToOne() / 15, MagickCore.getNegativeToOne() / 15);
                MagickCore.addMagickParticle(litPar);
            }
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected float getGravity() {
        return 0.005F;
    }

    @Override
    public boolean spawnGlowBlock() {
        return true;
    }

    public boolean hitEntityRemove(EntityHitResult entityRayTraceResult) {
        return true;
    }

    public boolean hitBlockRemove(BlockHitResult blockRayTraceResult) {
        return true;
    }

    @Override
    public AABB boundingBox() {
        return getBoundingBox();
    }
}
