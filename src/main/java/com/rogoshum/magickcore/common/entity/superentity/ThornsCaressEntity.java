package com.rogoshum.magickcore.common.entity.superentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ISuperEntity;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.laser.ThornsCaressLaserRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.superrender.ThornsCaressRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.api.magick.ManaFactor;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ThornsCaressEntity extends ManaPointEntity implements ISuperEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/thorns_caress.png");
    public ThornsCaressEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        MagickCore.proxy.addRenderer(() -> new ThornsCaressLaserRenderer(this));
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new ThornsCaressRenderer(this);
    }

    @Override
    public boolean releaseMagick() {
        if(this.tickCount % 2 ==0) {
            List<Entity> livings = this.findEntity((entity -> entity instanceof LivingEntity && !MagickReleaseHelper.sameLikeOwner(this.getCaster(), entity)));
            for (Entity entity : livings) {
                MagickContext context = new MagickContext(level).noCost().caster(this.getCaster()).projectile(this).victim(entity).tick(100).force(4f).applyType(ApplyType.DE_BUFF);
                MagickReleaseHelper.releaseMagick(context);
                context = new MagickContext(level).noCost().caster(this.getCaster()).projectile(this).victim(entity).tick(100).force(1).applyType(ApplyType.HIT_ENTITY);
                MagickReleaseHelper.releaseMagick(context);
            }
        }
        return true;
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.DEFAULT;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    protected void doClientTask() {
        super.doClientTask();
    }

    @Override
    protected void makeSound() {
        if(this.tickCount == 1) {
            this.playSound(ModSounds.wither_spawn.get(), 2.0F, 1.0F + this.random.nextFloat() / 3);
        }
        if(this.tickCount % 13 == 0) {
            this.playSound(ModSounds.wither_ambience.get(), 0.7F, 0.85F - this.random.nextFloat() * 0.5f);
        }
    }

    protected void applyParticle() {
        if(this.tickCount % 2 == 0){
            LitParticle par = new LitParticle(this.level, this.spellContext().element.getRenderer().getParticleTexture()
                    , new Vec3(this.getX()
                    , this.getY() + this.getBbHeight() / 2
                    , this.getZ())
                    , 0.45f, 0.45f, this.random.nextFloat(), 60, this.spellContext().element.getRenderer());
            par.setGlow();
            //par.setParticleGravity(0);
            par.addMotion(MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.05);
            MagickCore.addMagickParticle(par);
        }
        if(this.tickCount % 5 == 0){
            LitParticle litPar = new LitParticle(this.level, this.spellContext().element.getRenderer().getMistTexture()
                    , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + this.getX()
                    , MagickCore.getNegativeToOne() * this.getBbWidth() + this.getY() + this.getBbHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + this.getZ())
                    , this.random.nextFloat() * this.getBbWidth(), this.random.nextFloat() * this.getBbWidth(), 0.6f + 0.4f * this.random.nextFloat(), this.spellContext().element.getRenderer().getParticleRenderTick() / 4, this.spellContext().element.getRenderer());
            litPar.setGlow();
            litPar.setParticleGravity(0f);
            litPar.setShakeLimit(5.0f);
            litPar.addMotion(MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1);
            MagickCore.addMagickParticle(litPar);
        }
    }

    @Override
    public float getSourceLight() {
        return 15;
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.level.getEntities(this, this.getBoundingBox().inflate(12), predicate);
    }
}
