package com.rogoshum.magickcore.common.entity.pointed;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.GravityLiftRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.superrender.DawnWardRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class GravityLiftEntity extends ManaPointEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/gravity_lift.png");
    public final Predicate<Entity> inCylinder = (entity -> {
        Vec3 pos = entity.position().add(0, entity.getBbHeight() * 0.5, 0);
        return this.distanceToSqr(pos) <= liftHeight() * liftHeight() && rightDirection(pos);
    });
    public GravityLiftEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected float getEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return -0.5f;
    }

    @Override
    protected boolean fixedPosition() {
        return false;
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new GravityLiftRenderer(this);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount == 1) {
            this.playSound(ModSounds.soft_buildup_high.get(), 0.5F, 1.0F + this.random.nextFloat());
        }
        this.playSound(SoundEvents.BEE_POLLINATE, 0.3F, 2.0f);
        this.playSound(SoundEvents.BEE_POLLINATE, 1.2F, 0.0f);
    }

    @Override
    public Vec3 getPostDirection(Entity entity) {
        if(spellContext().containChild(LibContext.DIRECTION))
            return spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction;
        return super.getPostDirection(entity);
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.level.getEntities(this, this.getBoundingBox().inflate(liftHeight()),
                predicate != null ? predicate.and(inCylinder)
                        : inCylinder);
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.POINT_DEFAULT;
    }

    @Override
    protected void applyParticle() {
        if(!spellContext().containChild(LibContext.DIRECTION)) return;
        Vec3 dir = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
        double x = position().x;
        double y = position().y;
        double z = position().z;
        for (int i = 0; i < 5; ++i) {
            LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleSprite()
                    , new Vec3(MagickCore.getNegativeToOne() * 0.5f * this.getBbWidth() + x
                    , MagickCore.getNegativeToOne() * 0.5f * this.getBbWidth() + y + 1f
                    , MagickCore.getNegativeToOne() * 0.5f * this.getBbWidth() + z)
                    , getBbWidth() * 0.5f, getBbWidth() * 0.5f, 0.2f, (int) (spellContext().range * 3), MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.addMotion(dir.x, dir.y, dir.z);
            par.setLimitScale();
            par.setShakeLimit(15);
            par.setCanCollide(false);
            MagickCore.addMagickParticle(par);
        }
        LitParticle par;
        for (int i = 0; i < 4; ++i) {
            par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleSprite()
                    , new Vec3(MagickCore.getNegativeToOne() * 0.25f * this.getBbWidth() + x
                    , MagickCore.getNegativeToOne() * 0.25f * this.getBbWidth() + y + 1f
                    , MagickCore.getNegativeToOne() * 0.25f * this.getBbWidth() + z)
                    , getBbWidth() * 0.35f, getBbWidth() * 0.35f, 0.3f, (int) (spellContext().range), MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.addMotion(dir.x, dir.y, dir.z);
            par.setLimitScale();
            par.setCanCollide(false);
            par.setShakeLimit(15);
            par.setGlow();
            MagickCore.addMagickParticle(par);
        }

        for (int i = 0; i < 6; ++i) {
            par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleSprite()
                    , new Vec3(MagickCore.getNegativeToOne() * 3f * this.getBbWidth() + x
                    , MagickCore.getNegativeToOne() * 3f + y + 1f
                    , MagickCore.getNegativeToOne() * 3f * this.getBbWidth() + z)
                    , getBbWidth() * 0.35f, getBbWidth() * 0.35f, 0.2f, 7, MagickCore.proxy.getElementRender(spellContext().element.type()));
            Vec3 direction = this.position().add(0, 1, 0).subtract(par.positionVec()).scale(0.2);
            par.addMotion(direction.x, direction.y, direction.z);
            par.setNoScale();
            par.setShakeLimit(15);
            par.setGlow();
            par.setCanCollide(false);
            MagickCore.addMagickParticle(par);
        }

        for (int i = 0; i < 2; ++i) {
            par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleSprite()
                    , new Vec3(+ x
                    , y + 1f
                    , z)
                    , 0.15f, 0.15f, 1.0f, 5, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.addMotion(MagickCore.getNegativeToOne() * 0.15, MagickCore.getNegativeToOne() * 0.15, MagickCore.getNegativeToOne() * 0.15);
            par.setLimitScale();
            par.setGlow();
            par.setCanCollide(true);
            par.setShakeLimit(15);
            par.setTraceTarget(this);
            MagickCore.addMagickParticle(par);
        }

        par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleSprite()
                , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5f + x
                , y + 1f
                , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5f + z)
                , 0.05f, 0.05f, 1.0f, (int) (spellContext().range * 4), MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.addMotion(dir.x, dir.y, dir.z);
        par.setLimitScale();
        par.setGlow();
        par.setShakeLimit(15);
        par.setCanCollide(false);
        MagickCore.addMagickParticle(par);
    }

    @Override
    public void beforeJoinWorld(MagickContext context) {
        if(!context.containChild(LibContext.DIRECTION) || context.<DirectionContext>getChild(LibContext.DIRECTION).direction.equals(Vec3.ZERO))
            spellContext().addChild(DirectionContext.create(new Vec3(0, 1, 0)));
    }

    public float liftHeight() {
        return getBbHeight() + spellContext().range * 2;
    }

    public boolean rightDirection(Vec3 vec) {
        Vec3 direction = null;
        if(spellContext().containChild(LibContext.DIRECTION)) {
            direction = spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize();
        } else if (getOwner() != null) {
            direction = getOwner().getLookAngle().normalize();
        }
        if(direction == null || direction.equals(Vec3.ZERO)) return false;
        boolean forward = (this.position().subtract(vec).normalize().dot(direction)) <= 0;
        if(!forward) return false;

        return this.position().subtract(vec).cross(direction).length() < 1.5f;
    }

    @Override
    public float eyeHeight() {
        return 2;
    }

    @Override
    public float getSourceLight() {
        return 20;
    }
}
