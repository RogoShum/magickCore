package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibShaders;
import com.rogoshum.magickcore.api.magick.ManaFactor;
import com.rogoshum.magickcore.api.magick.context.child.TraceContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

public class ShadowEntity extends ManaProjectileEntity {
    private static final ManaFactor MANA_FACTOR = ManaFactor.create(0.5f, 1.0f, 1.0f);
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/shadow.png");
    public static final ResourceLocation MIST = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/mist.png");
    public ShadowEntity(EntityType<? extends ThrowableProjectile> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void makeSound() {
        if (this.tickCount == 1) {
            this.playSound(ModSounds.magical_focus_energy.get(), 1.5F, 1.0F + this.random.nextFloat());
        }
    }

    @Override
    public float getSourceLight() {
        return 0;
    }

    @Override
    protected void traceTarget() {
        if (!this.spellContext().containChild(LibContext.TRACE) || this.level.isClientSide) return;
        TraceContext traceContext = spellContext().getChild(LibContext.TRACE);
        Entity entity = traceContext.entity;
        if(entity == null && traceContext.uuid != MagickCore.emptyUUID) {
            entity = ((ServerLevel) this.level).getEntity(traceContext.uuid);
            traceContext.entity = entity;
        } else if(entity != null && entity.isAlive()) {
            Vec3 vec = entity.position().subtract(this.position()).scale(0.5);
            this.setPos(vec.x + this.position().x(), vec.y + this.position().y(), vec.z + this.position().z());
        }
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
    protected float getGravity() {
        return 0;
    }

    @Override
    protected void applyParticle() {
        float partial = Minecraft.getInstance().getFrameTime();
        double x = Mth.lerp(partial, this.xOld, this.getX());
        double y = Mth.lerp(partial, this.yOld, this.getY());
        double z = Mth.lerp(partial, this.zOld, this.getZ());
        float scale = Math.max(this.getBbWidth(), 0.5f) * 0.4f;
        for (int i = 0; i < 5; ++i) {
            LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleSprite()
                    , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + x
                    , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + y + this.getBbHeight() * 0.5
                    , MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5 + z)
                    , scale, scale, 0.35f, 20, MagickCore.proxy.getElementRender(spellContext().element().type()));
            par.setParticleGravity(0f);
            par.setShakeLimit(5f);
            par.setLimitScale();
            par.useShader(LibShaders.BITS);
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    public void removeEffect() {
        if (!this.level.isClientSide) {
            this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.5F, 1.0F + this.random.nextFloat());
        } else {
            float partial = Minecraft.getInstance().getFrameTime();
            double x = Mth.lerp(partial, this.xOld, this.getX());
            double y = Mth.lerp(partial, this.yOld, this.getY());
            double z = Mth.lerp(partial, this.zOld, this.getZ());
            float scale = Math.max(this.getBbWidth(), 0.5f) * 2;
            for (int i = 0; i < 20; ++i) {
                double motionX = MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5;
                double motionY = MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5;
                double motionZ = MagickCore.getNegativeToOne() * this.getBbWidth() * 0.5;
                LitParticle par = new LitParticle(this.level, ModElements.ORIGIN.getRenderer().getParticleSprite()
                        , new Vec3(motionX + x
                        , motionY + y + this.getBbHeight() * 0.5
                        , motionZ + z)
                        , scale, scale, 0.35f, 40, MagickCore.proxy.getElementRender(spellContext().element().type()));
                par.setParticleGravity(0f);
                par.setShakeLimit(5f);
                par.setLimitScale();
                par.addMotion(motionX * 0.2, motionY * 0.2, motionZ * 0.2);
                MagickCore.addMagickParticle(par);
            }
        }
    }
}
