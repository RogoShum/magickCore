package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.BloodBubbleRenderer;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class BloodBubbleEntity extends ManaProjectileEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/blood_bubble.png");
    private static final DataParameter<Float> HEALTH = EntityDataManager.defineId(BloodBubbleEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> BACK = EntityDataManager.defineId(BloodBubbleEntity.class, DataSerializers.BOOLEAN);
    public BloodBubbleEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
        this.entityData.define(HEALTH, this.getType().getWidth());
        this.entityData.define(BACK, false);
    }

    @Override
    public void tick() {
        super.tick();
    }

    public void setHealth(float health) {
        this.getEntityData().set(HEALTH, health);
    }

    public float getHealth() {
        return this.getEntityData().get(HEALTH);
    }

    public void setBack(boolean back) {
        this.getEntityData().set(BACK, back);
    }

    public boolean getBack() {
        return this.getEntityData().get(BACK);
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult p_213868_1_) {
        boolean isLiving = p_213868_1_.getEntity() instanceof LivingEntity;
        boolean suitable = suitableEntity(p_213868_1_.getEntity());
        if(isLiving) {
            LivingEntity living = (LivingEntity) p_213868_1_.getEntity();
            if(!suitable) {
                living.heal(getHealth());
            } else {
                this.setHealth(Math.min(living.getHealth() * 0.5f, getHealth() * 0.5f));
                living.setHealth(Math.max(living.getHealth() * 0.5f, 0.001f));
                setBack(true);
            }
        }

        super.onHitEntity(p_213868_1_);
    }

    protected void backToOwner() {
        MagickContext context = MagickContext.create(this.level)
                .replenishChild(DirectionContext.create(this.position().subtract(getOwner().position())))
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
        if (!this.level.isClientSide) {
            this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.5F, 1.0F + this.random.nextFloat());
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
        if(getBbHeight() != height)
            this.setHeight(height);
        float width = getType().getWidth() + getHealth() * 0.1f;
        if(getBbWidth() != width)
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
        LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleTexture()
                , new Vector3d(this.xOld + (this.getX() - this.xOld) * partialTicks
                , this.yOld + (this.getY() - this.yOld) * partialTicks + this.getBbHeight() / 2
                , this.zOld + (this.getZ() - this.zOld) * partialTicks)
                , 0.1f * this.getBbWidth(), 0.1f * this.getBbWidth(), 1.0f, 20, MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.setGlow();
        par.setParticleGravity(0);
        par.setColor(Color.RED_COLOR);
        par.setLimitScale();
        MagickCore.addMagickParticle(par);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaProjectileEntity>> getRenderer() {
        return () -> new BloodBubbleRenderer(this);
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.DEFAULT;
    }
}
