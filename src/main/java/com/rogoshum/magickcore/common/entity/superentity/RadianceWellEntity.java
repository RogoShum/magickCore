package com.rogoshum.magickcore.common.entity.superentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ISuperEntity;
import com.rogoshum.magickcore.client.entity.easyrender.ManaSphereRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.laser.RadianceWellLaserRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.superrender.RadianceWellRenderer;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class RadianceWellEntity extends ManaPointEntity implements ISuperEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/radiance_well.png");
    public RadianceWellEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.tickCount <= 10)
            return;
        initial = true;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        MagickCore.proxy.addRenderer(() -> new RadianceWellLaserRenderer(this));
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public Supplier<EasyRenderer<? extends ManaEntity>> getRenderer() {
        return () -> new RadianceWellRenderer(this);
    }

    @Nonnull
    @Override
    public List<Entity> findEntity(@Nullable Predicate<Entity> predicate) {
        return this.level.getEntities(this, this.getBoundingBox().inflate(0, getBbHeight(), 0), predicate);
    }

    @Override
    public boolean releaseMagick() {
        if(!initial) return false;
        List<Entity> livings = findEntity((living) -> living instanceof LivingEntity && MagickReleaseHelper.sameLikeOwner(this.getOwner(), living));
        livings.forEach(living -> {
            MagickContext context = new MagickContext(level).noCost().caster(this.getOwner()).projectile(this).victim(living).tick(20).force(3).applyType(ApplyType.BUFF);
            MagickReleaseHelper.releaseMagick(context);
        });
        return true;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    protected void doClientTask() {
        super.doClientTask();
        if(this.tickCount % 2 == 0) {
            Vec3 rand = new Vec3(MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne(), MagickCore.getNegativeToOne());
            this.hitReactions.put(this.random.nextInt(200) - this.random.nextInt(2000), new VectorHitReaction(rand, 0.06F, 0.01F));
        }
    }

    @Override
    public ManaFactor getManaFactor() {
        return ManaFactor.DEFAULT;
    }

    @Override
    protected void makeSound() {
        if(this.tickCount == 1)
        {
            this.playSound(ModSounds.wall_spawn.get(), 2.0F, 0.7F + this.random.nextFloat());
        }

        if(this.tickCount % 10 == 0)
        {
            this.playSound(ModSounds.wall_ambience.get(), 1.0F, 0.8F - this.random.nextFloat() / 4);
        }

        if(this.tickCount == this.spellContext().tick - 5)
            this.playSound(ModSounds.wall_dissipate.get(), 2.0F, 1.0F + this.random.nextFloat());
    }

    @Override
    protected void applyParticle()
    {
        if(this.tickCount % 5 ==0) {
            LitParticle cc = new LitParticle(this.level, this.spellContext().element.getRenderer().getRingTexture()
                    , new Vec3(this.getX()
                    , this.getY() + this.getBbHeight()
                    , this.getZ())
                    , 0.7f, 0.7f, 0.4f, 60, this.spellContext().element.getRenderer());
            cc.setGlow();
            cc.setParticleGravity(0);
            MagickCore.addMagickParticle(cc);
        }
        for(int i = 0; i < 5; ++i) {
            LitParticle par = new LitParticle(this.level, this.spellContext().element.getRenderer().getParticleTexture()
                    , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + this.getX()
                    , this.getY() + this.getBbHeight() / 5
                    , MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + this.getZ())
                    , 0.1f, 0.1f, this.random.nextFloat(), 60, this.spellContext().element.getRenderer());
            par.setGlow();
            par.setParticleGravity(-0.15f);
            par.addMotion(MagickCore.getNegativeToOne() * 0.01, MagickCore.getNegativeToOne() * 0.05, MagickCore.getNegativeToOne() * 0.01);
            MagickCore.addMagickParticle(par);
        }

        float scale = Math.max(this.getBbWidth(), 0.5f) * 0.4f;
        for (int i = 0; i < 3; ++i) {
            LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleTexture()
                    , new Vec3(this.getX()
                    , this.getY() + this.getBbHeight()
                    , this.getZ())
                    , 0.2f, 0.2f, 1.0f, 18, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0.5f);
            par.setLimitScale();
            par.setShakeLimit(15f);
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    public float getSourceLight() {
        return 20;
    }
}
