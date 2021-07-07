package com.rogoshum.magickcore.entity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.baseEntity.ManaProjectileEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ManaLaserEntity extends ManaProjectileEntity {
    public ManaLaserEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }

    @Override
    protected void applyParticle() {
        if(this.world.isRemote() && this.getElement() != null)
        {
            LitParticle par = new LitParticle(this.world, this.getElement().getRenderer().getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() + this.getPosX()
                    , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosY() + this.getHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                    , 0.1f, 0.1f, 1.0f, 10, this.getElement().getRenderer());
            par.setGlow();
            MagickCore.addMagickParticle(par);
        }
    }
}
