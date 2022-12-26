package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.ManaArrowRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.ManaLaserRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class ManaArrowEntity extends ManaProjectileEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/arrow.png");
    private static final ManaFactor MANA_FACTOR = ManaFactor.create(0.5f, 1.0f, 1.0f);
    public ManaArrowEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
        double length = getMotion().length() * 3;
        if(length > maxMotion)
            maxMotion = length;
    }

    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaProjectileEntity>> getRenderer() {
        return () -> new ManaArrowRenderer(this);
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
    protected float getGravityVelocity() {
        float range = spellContext().range;
        if(range > 10)
            range = 10;

        return 0.01f - range * 0.001f;
    }

    @Override
    public void reSize() {
    }

    @Override
    protected void applyParticle() {
        LitParticle par = new LitParticle(this.world, this.spellContext().element.getRenderer().getParticleTexture()
                , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() + this.getPosX()
                , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosY() + this.getHeight() / 2
                , MagickCore.getNegativeToOne() * this.getWidth() + this.getPosZ())
                , 0.15f * getWidth(), 0.15f * getWidth(), 1.0f, 10, this.spellContext().element.getRenderer());
        par.setGlow();
        MagickCore.addMagickParticle(par);
    }

    public void addParticle(Vector3d pos, Vector3d direction, int count, float baseScale, float scale) {
        for (int i = 0; i < count; ++i) {
            LitParticle par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getLaserTexture()
                    , new Vector3d(pos.x - i * direction.x
                    , pos.y - i * direction.y + this.getHeight() / 2
                    , pos.z - i * direction.z)
                    , (float) (baseScale * Math.pow(scale, i)), (float) (baseScale * Math.pow(scale, i)), 1.0f, 1, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setGlow();
            par.setParticleGravity(0);
            par.setLimitScale();
            MagickCore.addMagickParticle(par);
        }
    }

    @Override
    public ManaFactor getManaFactor() {
        float range = spellContext().range;
        if(range > 10)
            range = 10;
        return ManaFactor.create(Math.min(1.5f, (float) (Math.pow(this.getMotion().length() * range, 1.8) * 0.2 + 0.05)), 1.0f, 1.0f);
    }
}
