package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.ManaArrowRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.ManaLaserRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class ManaArrowEntity extends ManaProjectileEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/arrow.png");
    private static final ManaFactor MANA_FACTOR = ManaFactor.create(0.5f, 1.0f, 1.0f);
    public ManaArrowEntity(EntityType<? extends ThrowableProjectile> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
        double length = getDeltaMovement().length() * 3;
        if(length > maxMotion)
            maxMotion = length;
    }

    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaProjectileEntity>> getRenderer() {
        return () -> new ManaArrowRenderer(this);
    }

    @Override
    public float getSourceLight() {
        return 7;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    protected float getGravity() {
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
        LitParticle par = new LitParticle(this.level, this.spellContext().element.getRenderer().getParticleTexture()
                , new Vec3(MagickCore.getNegativeToOne() * this.getBbWidth() + this.getX()
                , MagickCore.getNegativeToOne() * this.getBbWidth() + this.getY() + this.getBbHeight() / 2
                , MagickCore.getNegativeToOne() * this.getBbWidth() + this.getZ())
                , 0.15f * getBbWidth(), 0.15f * getBbWidth(), 1.0f, 10, this.spellContext().element.getRenderer());
        par.setGlow();
        MagickCore.addMagickParticle(par);
    }

    public void addParticle(Vec3 pos, Vec3 direction, int count, float baseScale, float scale) {
        for (int i = 0; i < count; ++i) {
            LitParticle par = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element.type()).getLaserTexture()
                    , new Vec3(pos.x - i * direction.x
                    , pos.y - i * direction.y + this.getBbHeight() / 2
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
        return ManaFactor.create(Math.min(1.5f, (float) (Math.pow(this.getDeltaMovement().length() * range, 1.8) * 0.2 + 0.05)), 1.0f, 1.0f);
    }
}
