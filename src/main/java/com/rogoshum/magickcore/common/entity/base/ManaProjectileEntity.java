package com.rogoshum.magickcore.common.entity.base;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.common.api.entity.IManaEntity;
import com.rogoshum.magickcore.common.api.entity.IManaRefraction;
import com.rogoshum.magickcore.common.api.event.EntityEvents;
import com.rogoshum.magickcore.client.entity.easyrender.base.ManaProjectileFrameRenderer;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.child.ExtraApplyTypeContext;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.api.enums.ApplyType;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class ManaProjectileEntity extends ThrowableEntity implements IManaEntity, ILightSourceEntity, IEntityAdditionalSpawnData {
    private final SpellContext spellContext = SpellContext.create();
    private static final DataParameter<Optional<UUID>> dataUUID = EntityDataManager.createKey(ManaProjectileEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private static final DataParameter<Float> HEIGHT = EntityDataManager.createKey(ManaProjectileEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> WIDTH = EntityDataManager.createKey(ManaProjectileEntity.class, DataSerializers.FLOAT);
    public Entity victim;

    public ManaProjectileEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
        this.dataManager.register(dataUUID, Optional.of(MagickCore.emptyUUID));
        this.dataManager.register(HEIGHT, this.getType().getHeight());
        this.dataManager.register(WIDTH, this.getType().getWidth());
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (HEIGHT.equals(key) || WIDTH.equals(key)) {
            this.recalculateSize();
        }
        super.notifyDataManagerChange(key);
    }

    public void recalculateSize() {
        EntitySize entitysize = ObfuscationReflectionHelper.getPrivateValue(Entity.class, this, "field_213325_aI");
        Pose pose = this.getPose();
        EntitySize entitysize1 = this.getSize(pose);
        net.minecraftforge.event.entity.EntityEvent.Size sizeEvent = net.minecraftforge.event.ForgeEventFactory.getEntitySizeForge(this, pose, entitysize, entitysize1, this.getEyeHeight(pose, entitysize1));
        entitysize1 = sizeEvent.getNewSize();
        ObfuscationReflectionHelper.setPrivateValue(Entity.class, this, entitysize1,  "field_213325_aI");
        ObfuscationReflectionHelper.setPrivateValue(Entity.class, this, sizeEvent.getNewEyeHeight(),  "field_213326_aJ");
        double d0 = (double)entitysize1.width * 0.5;
        //double d1 = (entitysize1.height - entitysize.height) * 0.5;
        this.setBoundingBox(new AxisAlignedBB(this.getPosX() - d0, this.getPosY(), this.getPosZ() - d0, this.getPosX() + d0, this.getPosY() + (double)entitysize1.height, this.getPosZ() + d0));
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
        victim = null;
        super.tick();
        /*if (this.homePos == null)
            this.homePos = this.getPositionVec();
        else if (this.homePos.subtract(this.getPositionVec()).length() > this.getRange())
            this.remove();

         */
        if (!world.isRemote)
            makeSound();

        traceTarget();
        reSize();
        if(this.world.isRemote) {
            MagickCore.proxy.addTask(this::doClientTask);
        } else
            MagickCore.proxy.addTask(this::doServerTask);
    }

    public void reSize() {
        float height = getType().getHeight() + spellContext().range * 0.1f;
        if(getHeight() != height)
            this.setHeight(height);
        float width = getType().getWidth() + spellContext().range * 0.1f;
        if(getWidth() != width)
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
        MagickCore.proxy.addRenderer(() -> new ManaProjectileFrameRenderer(this));
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
    public Color getColor() {
        return this.spellContext().element.color();
    }

    protected void makeSound() {
        if (this.ticksExisted == 1) {
            this.playSound(SoundEvents.ENTITY_ENDER_PEARL_THROW, 1.5F, 1.0F + this.rand.nextFloat());
        }
    }

    @Override
    protected void registerData() {

    }

    @Override
    public void setOwnerUUID(UUID uuid) {
        this.getDataManager().set(dataUUID, Optional.of(uuid));
    }

    public UUID getOwnerUUID() {
        Optional<UUID> uuid = this.getDataManager().get(dataUUID);
        return uuid.orElse(MagickCore.emptyUUID);
    }

    protected void traceTarget() {
        if (!this.spellContext().containChild(LibContext.TRACE) || this.world.isRemote) return;
        TraceContext traceContext = spellContext().getChild(LibContext.TRACE);
        Entity entity = traceContext.entity;
        if(entity == null && traceContext.uuid != MagickCore.emptyUUID) {
            entity = ((ServerWorld) this.world).getEntityByUuid(traceContext.uuid);
            traceContext.entity = entity;
        } else if(entity != null) {
            Vector3d goal = new Vector3d(entity.getPosX(), entity.getPosY() + entity.getHeight() / 1.5f, entity.getPosZ());
            Vector3d self = new Vector3d(this.getPosX(), this.getPosY(), this.getPosZ());

            double length = this.getMotion().length();
            Vector3d motion = goal.subtract(self).normalize().scale(Math.max(length * 0.2, 0.02));
            this.setMotion(motion.add(this.getMotion().scale(0.8)));
        }
    }

    protected void onImpact(RayTraceResult result) {
        if(result.getType() == RayTraceResult.Type.ENTITY) {
            EntityRayTraceResult result1 = ((EntityRayTraceResult)result);
            if(result1.getEntity() instanceof IManaRefraction) {
                if(!((IManaRefraction) result1.getEntity()).refraction(spellContext()))
                    this.victim = result1.getEntity();
            } else
                this.victim = result1.getEntity();
        }
        super.onImpact(result);
    }

    @OnlyIn(Dist.CLIENT)
    protected void applyParticle() {
        LitParticle par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + this.getPosX()
                , MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + this.getPosY() + this.getHeight() * 0.5
                , MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + this.getPosZ())
                , 0.1f, 0.1f, 1.0f, 40, MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.setGlow();
        MagickCore.addMagickParticle(par);

        for (int i = 0; i < 2; ++i) {
            LitParticle litPar = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getMistTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + this.getPosX()
                    , MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + this.getPosY() + this.getHeight() * 0.5
                    , MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + this.getPosZ())
                    , 0.5f * this.getWidth(), 0.5f * this.getWidth(), 0.8f, spellContext().element.getRenderer().getParticleRenderTick(), spellContext().element.getRenderer());
            litPar.setGlow();
            litPar.setParticleGravity(0f);
            litPar.setShakeLimit(15.0f);
            litPar.setLimitScale();
            MagickCore.addMagickParticle(litPar);
        }
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.hasUniqueId("Owner")) {
            UUID ownerUUID = compound.getUniqueId("Owner");
            this.setOwnerUUID(ownerUUID);
        }
        spellContext().deserialize(compound);
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if (this.getOwner() != null) {
            compound.putUniqueId("Owner", this.getOwner().getUniqueID());
        }
        spellContext().serialize(compound);
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        if(suitableEntity(p_213868_1_.getEntity())) {
            EntityEvents.HitEntityEvent event = new EntityEvents.HitEntityEvent(this, p_213868_1_.getEntity());
            MinecraftForge.EVENT_BUS.post(event);
        }

        if (victim != null && !this.world.isRemote) {
            if(hitEntityRemove(p_213868_1_))
                this.remove();
            else
                releaseMagick();
        }
        super.onEntityHit(p_213868_1_);
    }

    @Override
    protected void func_230299_a_(BlockRayTraceResult p_230299_1_) {
        BlockState blockstate = this.world.getBlockState(p_230299_1_.getPos());
        blockstate.onProjectileCollision(this.world, blockstate, p_230299_1_, this);
        MagickContext context = MagickContext.create(world, spellContext().postContext).<MagickContext>applyType(ApplyType.HIT_BLOCK).noCost().caster(this.func_234616_v_()).projectile(this);
        PositionContext positionContext = PositionContext.create(Vector3d.copy(p_230299_1_.getPos()));
        context.addChild(positionContext);
        if (spellContext().postContext != null)
            context.addChild(ExtraApplyTypeContext.create(spellContext().postContext.applyType));
        MagickReleaseHelper.releaseMagick(context);

        if (hitBlockRemove(p_230299_1_) && !this.world.isRemote) {
            this.remove();
        }
    }

    @Override
    public void remove() {
        if(victim == null)
            victim = this;
        releaseMagick();
        if (!this.world.isRemote) {
            this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.5F, 1.0F + this.rand.nextFloat());
        }
        if (this.world.isRemote()) {
            for (int c = 0; c < 10; ++c) {
                LitParticle par = new LitParticle(this.world, spellContext().element.getRenderer().getParticleTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + this.getPosX()
                        , MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + this.getPosY() + this.getHeight() * 0.5
                        , MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + this.getPosZ())
                        , 0.125f, 0.125f, MagickCore.rand.nextFloat(), 80, spellContext().element.getRenderer());
                par.setGlow();
                par.setShakeLimit(15.0f);
                par.addMotion(MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10);
                MagickCore.addMagickParticle(par);
            }
            for (int i = 0; i < 5; ++i) {
                LitParticle litPar = new LitParticle(this.world, spellContext().element.getRenderer().getMistTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + this.getPosX()
                        , MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + this.getPosY() + this.getHeight() * 0.5
                        , MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + this.getPosZ())
                        , this.getWidth() * 0.5f, this.getWidth() * 0.5f, 0.5f * MagickCore.rand.nextFloat(), spellContext().element.getRenderer().getParticleRenderTick(), spellContext().element.getRenderer());
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
