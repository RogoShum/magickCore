package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class BloodBubbleEntity extends ManaProjectileEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/blood_bubble.png");
    private static final DataParameter<Float> HEALTH = EntityDataManager.createKey(BloodBubbleEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> BACK = EntityDataManager.createKey(BloodBubbleEntity.class, DataSerializers.BOOLEAN);
    public BloodBubbleEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
        this.dataManager.register(HEALTH, this.getType().getWidth());
        this.dataManager.register(BACK, false);
    }

    @Override
    public void tick() {
        super.tick();
    }

    public void setHealth(float health) {
        this.getDataManager().set(HEALTH, health);
    }

    public float getHealth() {
        return this.getDataManager().get(HEALTH);
    }

    public void setBack(boolean back) {
        this.getDataManager().set(BACK, back);
    }

    public boolean getBack() {
        return this.getDataManager().get(BACK);
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        boolean isLiving = p_213868_1_.getEntity() instanceof LivingEntity;
        boolean suitable = suitableEntity(p_213868_1_.getEntity());
        if(isLiving) {
            LivingEntity living = (LivingEntity) p_213868_1_.getEntity();
            if(!suitable) {
                living.heal(getHealth());
            } else {
                living.setHealth(Math.max(living.getHealth() * 0.5f, 0.001f));
                this.setHealth(getHealth() * 0.5f);
                setBack(true);
            }
        }

        super.onEntityHit(p_213868_1_);
    }

    protected void backToOwner() {
        MagickContext context = MagickContext.create(this.world)
                .replenishChild(DirectionContext.create(this.getPositionVec().subtract(getOwner().getPositionVec())))
                .replenishChild(SpawnContext.create(getType()))
                .force(getHealth())
                .applyType(ApplyType.SPAWN_ENTITY)
                .<MagickContext>tick(spellContext().tick)
                .caster(getOwner()).projectile(this).victim(getOwner())
                .<MagickContext>replenishChild(TraceContext.create(getOwner()))
                .noCost();
        MagickReleaseHelper.releaseMagick(context);
    }

    @Override
    public void remove() {
        victim = this;
        releaseMagick();
        if(getBack())
            backToOwner();
        if (!this.world.isRemote) {
            this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.5F, 1.0F + this.rand.nextFloat());
        }
        super.remove();
    }

    @Override
    public boolean hitBlockRemove(BlockRayTraceResult blockRayTraceResult) {
        return false;
    }

    @Override
    public void reSize() {
        float height = getType().getHeight() + getHealth() * 0.1f;
        if(getHeight() != height)
            this.setHeight(height);
        float width = getType().getWidth() + getHealth() * 0.1f;
        if(getWidth() != width)
            this.setWidth(width);
    }

    @Override
    public void beforeJoinWorld(MagickContext context) {
        this.setHealth(context.force);
    }

    @Override
    public float getSourceLight() {
        return 3;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public void renderFrame(float partialTicks) {
        LitParticle par = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleTexture()
                , new Vector3d(this.lastTickPosX + (this.getPosX() - this.lastTickPosX) * partialTicks
                , this.lastTickPosY + (this.getPosY() - this.lastTickPosY) * partialTicks + this.getHeight() / 2
                , this.lastTickPosZ + (this.getPosZ() - this.lastTickPosZ) * partialTicks)
                , 0.1f * this.getWidth(), 0.1f * this.getWidth(), 1.0f, 20, MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.setGlow();
        par.setParticleGravity(0);
        par.setColor(Color.RED_COLOR);
        par.setLimitScale();
        MagickCore.addMagickParticle(par);
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.DEFAULT;
    }
}
