package com.rogoshum.magickcore.common.entity.superentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ISuperEntity;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.superrender.SilenceSqualRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.api.magick.ManaFactor;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SilenceSquallEntity extends ManaEntity implements ISuperEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/silence_squall.png");
    public SilenceSquallEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new SilenceSqualRenderer(this);
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.DEFAULT;
    }
    @Override
    protected void makeSound() {
        if(this.tickCount == 1)
        {
            this.playSound(ModSounds.squal_spawn.get(), 1.0F, 1.0F + this.random.nextFloat() / 3);
        }

        if(this.tickCount % 8 == 0)
        {
            this.playSound(ModSounds.wind.get(), 0.5F, 1.0F - this.random.nextFloat());
        }
        if(this.tickCount % 100 == 0)
        {
            this.playSound(ModSounds.wind_fx.get(), 0.5F, 1.0F - this.random.nextFloat());
        }
        if(this.tickCount % 12 == 0)
        {
            this.playSound(ModSounds.squal_ambience.get(), 1.0F, 1.3F - this.random.nextFloat() / 3);
        }
    }

    protected void applyParticle() {
        for(int i = 0; i < 3; ++i) {
            LitParticle par = new LitParticle(this.level, this.spellContext().element().getRenderer().getParticleTexture()
                    , new Vec3(MagickCore.getNegativeToOne() * 18 + this.getX()
                    , MagickCore.getNegativeToOne() * 18 + this.getY() + this.getBbHeight() / 2
                    , MagickCore.getNegativeToOne() * 18 + this.getZ())
                    , MagickCore.getNegativeToOne() / 2, MagickCore.getNegativeToOne() / 2, 0.6f, 50, this.spellContext().element().getRenderer());
            par.setGlow();
            par.setParticleGravity(0);
            par.setTraceTarget(this);
            par.addMotion(MagickCore.getNegativeToOne() * 0.005, MagickCore.getNegativeToOne() * 0.005, MagickCore.getNegativeToOne() * 0.005);
            MagickCore.addMagickParticle(par);
        }

        for(int i = 0; i < 3; ++i) {
            LitParticle par = new LitParticle(this.level, this.spellContext().element().getRenderer().getTrailTexture()
                    , new Vec3(MagickCore.getNegativeToOne() * 18 + this.getX()
                    , MagickCore.getNegativeToOne() * 18 + this.getY() + this.getBbHeight() / 2
                    , MagickCore.getNegativeToOne() * 18 + this.getZ())
                    , MagickCore.getNegativeToOne() / 4, MagickCore.getNegativeToOne() / 8, 1.0f, 100, this.spellContext().element().getRenderer());
            par.setGlow();
            par.setParticleGravity(0);
            par.setTraceTarget(this);
            par.addMotion(MagickCore.getNegativeToOne() * 0.005, MagickCore.getNegativeToOne() * 0.005, MagickCore.getNegativeToOne() * 0.005);
            MagickCore.addMagickParticle(par);
        }

        for(int i = 0; i < 1; ++i) {
            LitParticle litPar = new LitParticle(this.level, this.spellContext().element().getRenderer().getMistTexture()
                    , new Vec3(MagickCore.getNegativeToOne() * 8 + this.getX()
                    , MagickCore.getNegativeToOne() * 6 + this.getY() + this.getBbHeight() / 2
                    , MagickCore.getNegativeToOne() * 8 + this.getZ())
                    , this.random.nextFloat() * this.getBbWidth() * 1.5f, this.random.nextFloat() * this.getBbWidth() * 1.5f, 0.2f, this.spellContext().element().getRenderer().getParticleRenderTick(), this.spellContext().element().getRenderer());
            litPar.setGlow();
            litPar.setParticleGravity(0f);
            litPar.setShakeLimit(5.0f);
            litPar.addMotion(MagickCore.getNegativeToOne() * 0.15, MagickCore.getNegativeToOne() * 0.15, MagickCore.getNegativeToOne() * 0.15);
            MagickCore.addMagickParticle(litPar);
        }
    }

    @Override
    public float getSourceLight() {
        return 15;
    }

    @Override
    public boolean releaseMagick() {
        Entity cloest = null;

        List<Entity> entities = findEntity();
        for (Entity entity : entities) {
            if(entity == null)
                continue;
            if(entity instanceof LivingEntity && !MagickReleaseHelper.sameLikeOwner(this.getCaster(), entity)) {
                if(cloest == null || this.distanceTo(entity) < this.distanceTo(cloest))
                    cloest = entity;
                if(this.distanceTo(entity) <= 9.5) {
                    MagickContext context = new MagickContext(this.level).noCost().caster(this.getCaster()).projectile(this).victim(entity).tick(200).force(4f).applyType(ApplyType.HIT_ENTITY);
                    MagickReleaseHelper.releaseMagick(context);
                }
                if(this.tickCount == 1 || this.distanceTo(entity) <= 3) {
                    MagickContext context = new MagickContext(this.level).noCost().caster(this.getCaster()).projectile(this).victim(entity).tick(200).force(10f).applyType(ApplyType.DE_BUFF);
                    MagickReleaseHelper.releaseMagick(context);
                    if(this.tickCount % 20 == 0) {
                        context = new MagickContext(this.level).noCost().caster(this.getCaster()).projectile(this).victim(entity).tick(20).force(1f).applyType(ApplyType.ATTACK);
                        MagickReleaseHelper.releaseMagick(context);
                        entity.invulnerableTime = 0;
                    }
                }
            }
        }

        if(cloest != null && cloest.isAlive()) {
            Vec3 vec = cloest.position().add(0, 2, 0).subtract(this.position());
            this.setDeltaMovement(vec.normalize().scale(0.1));
        }
        else if(this.getCaster() != null)
        {
            Vec3 vec = this.getCaster().position().add(0, 2, 0).subtract(this.position());
            this.setDeltaMovement(vec.normalize().scale(0.1));
        }
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        this.setPos(this.getX() + this.getDeltaMovement().x, this.getY() + this.getDeltaMovement().y, this.getZ() + this.getDeltaMovement().z);
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        return true;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.level.getEntities(this, this.getBoundingBox().inflate(16), predicate);
    }
}
