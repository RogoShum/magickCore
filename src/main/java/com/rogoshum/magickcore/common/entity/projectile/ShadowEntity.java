package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ShadowEntity extends ManaProjectileEntity {
    private static final ManaFactor MANA_FACTOR = ManaFactor.create(0.5f, 1.0f, 1.0f);
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/shadow.png");
    public static final ResourceLocation MIST = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/mist.png");
    public ShadowEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public float getSourceLight() {
        return 0;
    }

    @Override
    protected void traceTarget() {
        if (!this.spellContext().containChild(LibContext.TRACE) || this.world.isRemote) return;
        TraceContext traceContext = spellContext().getChild(LibContext.TRACE);
        Entity entity = traceContext.entity;
        if(entity == null && traceContext.uuid != MagickCore.emptyUUID) {
            entity = ((ServerWorld) this.world).getEntityByUuid(traceContext.uuid);
            traceContext.entity = entity;
        } else if(entity != null) {
            Vector3d vec = entity.getPositionVec().subtract(this.getPositionVec()).scale(0.5);
            this.setPosition(vec.x + this.getPositionVec().getX(), vec.y + this.getPositionVec().getY(), vec.z + this.getPositionVec().getZ());
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
    protected float getGravityVelocity() {
        return 0;
    }

    @Override
    protected void applyParticle() {
        float partial = Minecraft.getInstance().getRenderPartialTicks();
        double x = MathHelper.lerp(partial, this.lastTickPosX, this.getPosX());
        double y = MathHelper.lerp(partial, this.lastTickPosY, this.getPosY());
        double z = MathHelper.lerp(partial, this.lastTickPosZ, this.getPosZ());
        float scale = Math.max(this.getWidth(), 0.5f) * 0.4f;
        for (int i = 0; i < 5; ++i) {
            LitParticle par = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleSprite()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + x
                    , MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + y + this.getHeight() * 0.5
                    , MagickCore.getNegativeToOne() * this.getWidth() * 0.5 + z)
                    , scale, scale, 0.35f, 20, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setParticleGravity(0f);
            par.setShakeLimit(15f);
            par.setLimitScale();
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    public void removeEffect() {
        if (!this.world.isRemote) {
            this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.5F, 1.0F + this.rand.nextFloat());
        } else {
            float partial = Minecraft.getInstance().getRenderPartialTicks();
            double x = MathHelper.lerp(partial, this.lastTickPosX, this.getPosX());
            double y = MathHelper.lerp(partial, this.lastTickPosY, this.getPosY());
            double z = MathHelper.lerp(partial, this.lastTickPosZ, this.getPosZ());
            float scale = Math.max(this.getWidth(), 0.5f) * 2;
            for (int i = 0; i < 20; ++i) {
                double motionX = MagickCore.getNegativeToOne() * this.getWidth() * 0.5;
                double motionY = MagickCore.getNegativeToOne() * this.getWidth() * 0.5;
                double motionZ = MagickCore.getNegativeToOne() * this.getWidth() * 0.5;
                LitParticle par = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleSprite()
                        , new Vector3d(motionX + x
                        , motionY + y + this.getHeight() * 0.5
                        , motionZ + z)
                        , scale, scale, 0.35f, 40, MagickCore.proxy.getElementRender(spellContext().element.type()));
                par.setParticleGravity(0f);
                par.setShakeLimit(15f);
                par.setLimitScale();
                par.addMotion(motionX * 0.2, motionY * 0.2, motionZ * 0.2);
                MagickCore.addMagickParticle(par);
            }
        }
    }
}
