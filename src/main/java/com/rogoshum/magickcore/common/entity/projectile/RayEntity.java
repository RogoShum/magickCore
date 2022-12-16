package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.RayRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.RedStoneRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class RayEntity extends ManaProjectileEntity {
    private static final ManaFactor MANA_FACTOR = ManaFactor.create(0.7f, 1.0f, 1.0f);
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/ray.png");
    public RayEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void makeSound() {
        if (this.ticksExisted == 1) {
            this.playSound(ModSounds.glitter_another.get(), 0.25F, 1.0F - this.rand.nextFloat());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaProjectileEntity>> getRenderer() {
        return () -> new RayRenderer(this);
    }

    @Override
    public float getSourceLight() {
        return 3;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    protected void applyParticle() {
        for (int i = 0; i < 1; ++i) {
            LitParticle litPar = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosX()
                    , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosY() + this.getHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getWidth() / 2 + this.getPosZ())
                    , (MagickCore.getRandFloat() * this.getWidth()), (MagickCore.getRandFloat() * this.getWidth()), 1.0f, 20, spellContext().element.getRenderer());
            litPar.setGlow();
            litPar.setShakeLimit(15.0f);
            litPar.setLimitScale();
            MagickCore.addMagickParticle(litPar);
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0;
    }

    @Override
    public ManaFactor getManaFactor() {
        return MANA_FACTOR;
    }
}
