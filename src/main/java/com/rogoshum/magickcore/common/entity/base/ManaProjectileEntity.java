package com.rogoshum.magickcore.common.entity.base;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.entity.IManaRefraction;
import com.rogoshum.magickcore.api.enums.TargetType;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.base.ManaEntityRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.base.ManaProjectileRenderer;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.*;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.api.enums.ApplyType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class ManaProjectileEntity extends ThrowableEntity implements IManaEntity, ILightSourceEntity, IEntityAdditionalSpawnData {
    private final SpellContext spellContext = SpellContext.create();
    private static final DataParameter<Optional<UUID>> dataUUID = EntityDataManager.defineId(ManaProjectileEntity.class, DataSerializers.OPTIONAL_UUID);
    private static final DataParameter<Float> HEIGHT = EntityDataManager.defineId(ManaProjectileEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> WIDTH = EntityDataManager.defineId(ManaProjectileEntity.class, DataSerializers.FLOAT);
    public Entity victim;
    public double maxMotion;
    private boolean released = false;

    public ManaProjectileEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
        this.entityData.define(dataUUID, Optional.of(MagickCore.emptyUUID));
        this.entityData.define(HEIGHT, this.getType().getHeight());
        this.entityData.define(WIDTH, this.getType().getWidth());
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
        this.setBoundingBox(new AxisAlignedBB(this.getX() - d0, this.getY(), this.getZ() - d0, this.getX() + d0, this.getY() + (double)entitysize1.height, this.getZ() + d0));
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
    protected float getEyeHeight(Pose poseIn, EntitySize sizeIn) {
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
    public void writeSpawnData(PacketBuffer buffer) {
        CompoundNBT addition = new CompoundNBT();
        addAdditionalSaveData(addition);
        buffer.writeNbt(addition);
    }

    @OnlyIn(Dist.CLIENT)
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
    public void readSpawnData(PacketBuffer additionalData) {
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
    public void setOwner(Entity entityIn) {
        super.setOwner(entityIn);
        if (entityIn != null)
            this.setOwnerUUID(entityIn.getUUID());
    }

    @Nullable
    @Override
    public Entity getOwner() {
        Entity entity = super.getOwner();
        UUID uuid = getOwnerUUID();
        if(uuid == MagickCore.emptyUUID) return entity;
        if (entity == null && this.level.isClientSide) {
            Iterator<Entity> it = ((ClientWorld) this.level).entitiesForRendering().iterator();
            while (it.hasNext()) {
                Entity entity1 = it.next();
                if(entity1 != null && entity1.getUUID().equals(uuid)) {
                    setOwner(entity1);
                    return entity1;
                }
            }
        }
        return entity;
    }

    @Override
    public void tick() {
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
            Vector3d vector3d = this.getDeltaMovement();
            vector3d = vector3d.normalize().scale(getBbWidth() * 0.5);
            World world = this.level;
            Vector3d vector3d1 = this.position();
            Vector3d vector3d2 = vector3d1.add(vector3d);
            EntityRayTraceResult raytraceResult = ProjectileHelper.getEntityHitResult(world, this, vector3d1, vector3d2, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), Entity::isAlive);
            if (raytraceResult != null) {
                Entity target = raytraceResult.getEntity();
                if(target instanceof IManaRefraction) {
                    if(!((IManaRefraction) target).refraction(spellContext()))
                        this.victim = target;
                } else
                    this.victim = target;
                if (victim != null && !this.level.isClientSide) {
                    EntityRayTraceResult result = new EntityRayTraceResult(target);
                    onHitEntity(result);
                }
            }
        }
    }

    public void reSize() {
        float height = getType().getHeight() + spellContext().range * 0.1f;
        if(getBbHeight() != height)
            this.setHeight(height);
        float width = getType().getWidth() + spellContext().range * 0.1f;
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
    public Vector3d positionVec() {
        return position();
    }

    @Override
    public World world() {
        return getCommandSenderWorld();
    }

    @Override
    public float eyeHeight() {
        return this.getBbHeight() * 0.5f;
    }

    @Override
    public Color getColor() {
        return this.spellContext().element.color();
    }

    protected void makeSound() {
        if (this.tickCount == 1) {
            this.playSound(ModSounds.glitter_another.get(), 0.25F, 1.0F + MagickCore.rand.nextFloat());
        }
    }

    @Override
    public SoundCategory getSoundSource() {
        return SoundCategory.NEUTRAL;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void setOwnerUUID(UUID uuid) {
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
            entity = ((ServerWorld) this.level).getEntity(traceContext.uuid);
            traceContext.entity = entity;
        } else if(entity != null && entity.isAlive()) {
            Vector3d goal = new Vector3d(entity.getX(), entity.getY() + entity.getBbHeight() / 1.5f, entity.getZ());
            Vector3d self = new Vector3d(this.getX(), this.getY(), this.getZ());

            double length = maxMotion * 0.3;
            Vector3d motion = goal.subtract(self).normalize().scale(Math.max(length * 0.2, 0.02));
            this.setDeltaMovement(motion.add(this.getDeltaMovement().scale(0.8)));
        }
    }

    protected void onHit(RayTraceResult result) {
        if(result.getType() == RayTraceResult.Type.ENTITY) {
            EntityRayTraceResult result1 = ((EntityRayTraceResult)result);
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
        LitParticle par = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                , new Vector3d(MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getX()
                , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getY() + this.getBbHeight() * 0.5
                , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getZ())
                , 0.1f, 0.1f, 1.0f, 40, MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.setGlow();
        MagickCore.addMagickParticle(par);

        if(tickCount % 5 != 0) return;
        LitParticle litPar = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element.type()).getMistTexture()
                , new Vector3d(MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getX()
                , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getY() + this.getBbHeight() * 0.5
                , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getZ())
                , 0.5f * this.getBbWidth(), 0.5f * this.getBbWidth(), 0.8f, spellContext().element.getRenderer().getParticleRenderTick(), spellContext().element.getRenderer());
        litPar.setGlow();
        litPar.setParticleGravity(0f);
        litPar.setShakeLimit(15.0f);
        litPar.setLimitScale();
        MagickCore.addMagickParticle(litPar);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("Owner")) {
            UUID ownerUUID = compound.getUUID("Owner");
            this.setOwnerUUID(ownerUUID);
        }
        spellContext().deserialize(compound);
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        if (this.getOwner() != null) {
            compound.putUUID("Owner", this.getOwner().getUUID());
        }
        spellContext().serialize(compound);
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult p_213868_1_) {
        if(suitableEntity(p_213868_1_.getEntity())) {
            ConditionContext condition = null;
            if(spellContext().containChild(LibContext.CONDITION))
                condition = spellContext().getChild(LibContext.CONDITION);
            AtomicReference<Boolean> pass = new AtomicReference<>(true);
            if(condition != null) {
                if(!condition.test(this.getOwner(), p_213868_1_.getEntity()))
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
                this.remove();
            }
        }
        super.onHitEntity(p_213868_1_);
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult p_230299_1_) {
        if(spellContext().containChild(LibContext.CONDITION)) {
            ConditionContext condition = spellContext().getChild(LibContext.CONDITION);
            if(!condition.<Block>test(null, level.getBlockState(p_230299_1_.getBlockPos()).getBlock()))
                return;
        }
        BlockState blockstate = this.level.getBlockState(p_230299_1_.getBlockPos());
        blockstate.onProjectileHit(this.level, blockstate, p_230299_1_, this);
        MagickContext context = MagickContext.create(level, spellContext().postContext).<MagickContext>applyType(ApplyType.HIT_BLOCK).noCost().caster(this.getOwner()).projectile(this);
        PositionContext positionContext = PositionContext.create(Vector3d.atLowerCornerOf(p_230299_1_.getBlockPos()));
        context.addChild(positionContext);
        MagickReleaseHelper.releaseMagick(beforeCast(context));

        context = MagickContext.create(level, spellContext().postContext).doBlock().noCost().caster(this.getOwner()).projectile(this);
        context.addChild(positionContext);
        MagickReleaseHelper.releaseMagick(beforeCast(context));

        if (hitBlockRemove(p_230299_1_) && !this.level.isClientSide) {
            this.remove();
        }
    }

    @Override
    public void remove() {
        if(victim == null)
            victim = this;
        if(!released)
            releaseMagick();
        removeEffect();
        if(!level.isClientSide)
            level.broadcastEntityEvent(this, (byte) 3);
        super.remove();
    }

    @Override
    public Vector3d getPostDirection(Entity entity) {
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
                LitParticle par = new LitParticle(this.level, spellContext().element.getRenderer().getParticleTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getX()
                        , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getY() + this.getBbHeight() * 0.5
                        , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getZ())
                        , 0.125f, 0.125f, MagickCore.rand.nextFloat(), 80, spellContext().element.getRenderer());
                par.setGlow();
                par.setShakeLimit(15.0f);
                par.addMotion(MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10);
                MagickCore.addMagickParticle(par);
            }
            for (int i = 0; i < 5; ++i) {
                LitParticle litPar = new LitParticle(this.level, spellContext().element.getRenderer().getMistTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getX()
                        , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getY() + this.getBbHeight() * 0.5
                        , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + this.getZ())
                        , this.getBbWidth() * 0.5f, this.getBbWidth() * 0.5f, 0.5f * MagickCore.rand.nextFloat(), spellContext().element.getRenderer().getParticleRenderTick(), spellContext().element.getRenderer());
                litPar.setGlow();
                litPar.setParticleGravity(0f);
                litPar.setShakeLimit(15.0f);
                litPar.addMotion(MagickCore.getNegativeToOne() / 15, MagickCore.getNegativeToOne() / 15, MagickCore.getNegativeToOne() / 15);
                MagickCore.addMagickParticle(litPar);
            }
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
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

    public boolean hitEntityRemove(EntityRayTraceResult entityRayTraceResult) {
        return true;
    }

    public boolean hitBlockRemove(BlockRayTraceResult blockRayTraceResult) {
        return true;
    }

    @Override
    public AxisAlignedBB boundingBox() {
        return getBoundingBox();
    }
}
