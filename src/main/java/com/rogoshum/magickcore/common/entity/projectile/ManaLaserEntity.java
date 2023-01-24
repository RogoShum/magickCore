package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.ManaLaserRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.ManaOrbRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.Supplier;

public class ManaLaserEntity extends ManaProjectileEntity {
    private static final ManaFactor MANA_FACTOR = ManaFactor.create(0.3f, 1.0f, 0.3f);
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/mana_laser.png");
    public ManaLaserEntity(EntityType<? extends ThrowableProjectile> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void makeSound() {
        if (this.tickCount == 1) {
            this.playSound(ModSounds.gatorix_spawn.get(), 0.25F, 1.0F + this.random.nextFloat());
        }
    }

    @Environment(EnvType.CLIENT)
    public Supplier<EasyRenderer<? extends ManaProjectileEntity>> getRenderer() {
        return () -> new ManaLaserRenderer(this);
    }

    @Override
    protected float getGravity() {
        return 0.0F;
    }

    @Override
    protected void applyParticle() {
        /*
        int count = (int) (20 * getWidth());
        double scaleX = (this.getPosX() - this.lastTickPosX)/count;
        double scaleY = (this.getPosY() - this.lastTickPosY)/count;
        double scaleZ = (this.getPosZ() - this.lastTickPosZ)/count;
        for (int i = 0; i < count; ++i) {
            LitParticle par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getLaserTexture()
                    , new Vec3(this.lastTickPosX + scaleX * i
                    , this.lastTickPosY + scaleY * i + this.getHeight() / 2
                    , this.lastTickPosZ + scaleZ * i)
                    , 0.3f * getWidth(), 0.3f * getWidth(), 1.0f, 2, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0);
            par.setNoScale();
            //par.addMotion(this.getMotion().x / 2, this.getMotion().y / 2, this.getMotion().z / 2);
            MagickCore.addMagickParticle(par);
        }

         */

        LitParticle par = new LitParticle(this.level, this.spellContext().element.getRenderer().getParticleTexture()
                , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() + this.getX()
                , MagickCore.getNegativeToOne() * this.getBbWidth() + this.getY() + this.getBbHeight() / 2
                , MagickCore.getNegativeToOne() * this.getBbWidth() + this.getZ())
                , 0.15f * getBbWidth(), 0.15f * getBbWidth(), 1.0f, 10, this.spellContext().element.getRenderer());
        par.setGlow();
        MagickCore.addMagickParticle(par);
    }

    @Override
    public boolean hitEntityRemove(EntityHitResult result) {
        return false;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        return MANA_FACTOR;
    }
}
