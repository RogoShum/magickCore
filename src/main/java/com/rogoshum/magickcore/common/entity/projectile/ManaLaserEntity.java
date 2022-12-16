package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.ManaLaserRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.ManaOrbRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class ManaLaserEntity extends ManaProjectileEntity {
    private static final ManaFactor MANA_FACTOR = ManaFactor.create(0.3f, 1.0f, 0.3f);
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/mana_laser.png");
    public ManaLaserEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void makeSound() {
        if (this.ticksExisted == 1) {
            this.playSound(ModSounds.gatorix_spawn.get(), 0.25F, 1.0F + this.rand.nextFloat());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaProjectileEntity>> getRenderer() {
        return () -> new ManaLaserRenderer(this);
    }

    @Override
    protected float getGravityVelocity() {
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
                    , new Vector3d(this.lastTickPosX + scaleX * i
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

        LitParticle par = new LitParticle(this.world, this.spellContext().element.getRenderer().getParticleTexture()
                , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() + this.getPosX()
                , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosY() + this.getHeight() / 2
                , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                , 0.15f * getWidth(), 0.15f * getWidth(), 1.0f, 10, this.spellContext().element.getRenderer());
        par.setGlow();
        MagickCore.addMagickParticle(par);
    }

    @Override
    public boolean hitEntityRemove(EntityRayTraceResult result) {
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
