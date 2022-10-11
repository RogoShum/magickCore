package com.rogoshum.magickcore.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class ManaElementOrbEntity extends ManaProjectileEntity {
    public ManaElementOrbEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void makeSound() {
        if(!this.world.isRemote && this.ticksExisted == 1)
        {
            this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.0F, 1.0F - this.rand.nextFloat());
        }
    }

    @Override
    public void releaseMagick() {
        List<Entity> list = findEntity(entity -> entity instanceof LivingEntity && entity.isAlive());
        if(!list.isEmpty())
        {
            Entity entity = list.get(0);
            EntityStateData state = ExtraDataHelper.entityStateData(entity);
            state.setManaValue(state.getManaValue() + 100);
            if(!(entity instanceof PlayerEntity) && state.getElement().type().equals(LibElements.ORIGIN))
                state.setElement(this.spellContext().element);
            this.remove();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ResourceLocation getEntityIcon() {
        return spellContext().element.getRenderer().getParticleTexture();
    }

    @Override
    protected void applyParticle() {
        if(this.world.isRemote() && this.spellContext().element != null)
        {
            LitParticle par = new LitParticle(this.world, this.spellContext().element.getRenderer().getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                    , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosY() + this.getHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosZ())
                    , this.rand.nextFloat() * 0.03f, this.rand.nextFloat() * 0.03f, 1.0f, 40, this.spellContext().element.getRenderer());
            par.setGlow();
            par.setLimitScale();
            MagickCore.addMagickParticle(par);


                LitParticle litPar = new LitParticle(this.world, this.spellContext().element.getRenderer().getMistTexture()
                        , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                        , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosY() + this.getHeight() / 2
                        , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosZ())
                        , this.rand.nextFloat() * 0.2f, this.rand.nextFloat() * 0.2f, 0.8f, this.spellContext().element.getRenderer().getParticleRenderTick() / 2, this.spellContext().element.getRenderer());
                litPar.setGlow();
                litPar.setParticleGravity(0f);
                litPar.setShakeLimit(15.0f);
                //litPar.setLimitScale();
                MagickCore.addMagickParticle(litPar);
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }
}
