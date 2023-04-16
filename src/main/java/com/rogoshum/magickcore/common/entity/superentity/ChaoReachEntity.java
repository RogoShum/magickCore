package com.rogoshum.magickcore.common.entity.superentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.laser.ChaosReachLaserRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.superrender.ChaosReachRenderer;
import com.rogoshum.magickcore.api.enums.ManaLimit;
import com.rogoshum.magickcore.api.entity.ISuperEntity;
import com.rogoshum.magickcore.client.vertex.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaEntity;
import com.rogoshum.magickcore.common.entity.base.ManaPointEntity;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ChaoReachEntity extends ManaPointEntity implements ISuperEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/chaos_reach.png");
    public ChaoReachEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.level.getEntities(this, this.getBoundingBox().inflate(32), predicate);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        MagickCore.proxy.addRenderer(() -> new ChaosReachLaserRenderer(this));
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new ChaosReachRenderer(this);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.tickCount <= 30)
            return;
        initial = true;
    }

    @Override
    protected void doClientTask() {
        Vec3 rand = new Vec3(MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne());
        this.hitReactions.put(this.random.nextInt(200) - this.random.nextInt(2000), new VectorHitReaction(rand, 0.2F, 0.005F));
        super.doClientTask();
    }

    @Override
    public boolean releaseMagick() {
        if(!initial) return false;
        List<Entity> livings = findEntity(entity -> entity instanceof LivingEntity && !MagickReleaseHelper.sameLikeOwner(this.getCaster(), entity) && MagickReleaseHelper.canEntityTraceAnother(this, entity));
        boolean makeSound = false;
        for (Entity entity : livings) {
            makeSound = true;
            MagickContext context = new MagickContext(level).noCost().caster(this.getCaster()).projectile(this).victim(entity).tick(50).force(5).applyType(ApplyType.DE_BUFF);
            MagickReleaseHelper.releaseMagick(context);
            context = new MagickContext(level).noCost().caster(this.getCaster()).projectile(this).victim(entity).tick(10).force(2.5f).applyType(ApplyType.ATTACK);
            MagickReleaseHelper.releaseMagick(context);
        }

        if(makeSound && this.tickCount % 2 == 0)
            this.playSound(ModSounds.chaos_attak.get(), 2.0F, 1.0F - this.random.nextFloat() / 5);
        return true;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.DEFAULT;
    }

    @Override
    protected void makeSound() {
        if(this.tickCount == 1)
        {
            this.playSound(ModSounds.chaos_spawn.get(), 2.0F, 1.0F - this.random.nextFloat());
        }

        if(this.tickCount > 20 && this.tickCount % 5 == 0)
        {
            this.playSound(ModSounds.chaos_ambience.get(), 1.0F, 1.0F + this.random.nextFloat());
        }
    }

    protected void applyParticle()
    {
        for(int i = 0; i < 1; ++i) {
            LitParticle par = new LitParticle(this.level, this.spellContext().element.getRenderer().getParticleTexture()
                    , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + this.getX()
                    , MagickCore.getNegativeToOne() * this.getBbWidth() + this.getY() + this.getBbHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getBbWidth() + this.getZ())
                    , 0.15f, 0.15f, this.random.nextFloat(), 60, this.spellContext().element.getRenderer());
            par.setGlow();
            par.setParticleGravity(0);
            par.addMotion(MagickCore.getNegativeToOne() * 0.2, MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.2);
            MagickCore.addMagickParticle(par);
        }
        for(int i = 0; i < 1; ++i) {
            LitParticle litPar = new LitParticle(this.level, this.spellContext().element.getRenderer().getMistTexture()
                    , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + this.getX()
                    , MagickCore.getNegativeToOne() * this.getBbWidth() + this.getY() + this.getBbHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getBbWidth() + this.getZ())
                    , this.random.nextFloat() * this.getBbWidth() * this.getBbWidth(), this.random.nextFloat() * this.getBbWidth() * this.getBbWidth(), 0.8f + 0.2f * this.random.nextFloat(), this.spellContext().element.getRenderer().getParticleRenderTick() / 2, this.spellContext().element.getRenderer());
            litPar.setGlow();
            litPar.setParticleGravity(0f);
            litPar.setShakeLimit(35.0f);
            litPar.addMotion(MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1, MagickCore.getNegativeToOne() * 0.1);
            MagickCore.addMagickParticle(litPar);
        }

        float scale = Math.max(this.getBbWidth(), 0.5f) * 0.4f;
        for (int i = 0; i < 3; ++i) {
            LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleTexture()
                    , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + this.getX()
                    , MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + this.getY() + this.getBbHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + this.getZ())
                    , scale, scale, 0.5f, 15, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0f);
            par.setLimitScale();
            par.setShakeLimit(15f);
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    public float getSourceLight() {
        return 15;
    }
}
