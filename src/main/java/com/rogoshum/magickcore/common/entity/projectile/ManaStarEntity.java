package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.PhantomRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.ManaStarRenderer;
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

public class ManaStarEntity extends ManaProjectileEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/mana_star.png");
    public ManaStarEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaProjectileEntity>> getRenderer() {
        return () -> new ManaStarRenderer(this);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public void renderFrame(float partialTicks) {
        LitParticle par = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element.type()).getTrailTexture()
                , new Vector3d(this.xOld + (this.getX() - this.xOld) * partialTicks
                , this.yOld + (this.getY() - this.yOld) * partialTicks + this.getBbHeight() / 2
                , this.zOld + (this.getZ() - this.zOld) * partialTicks)
                , 0.2f * getBbWidth(), 0.2f * getBbWidth(), 1.0f, 10, MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.setGlow();
        par.setParticleGravity(0);
        par.setLimitScale();
        MagickCore.addMagickParticle(par);
    }

    @Override
    public ManaFactor getManaFactor() {
        if(level.getDayTime() > 13800 && level.getDayTime() <= 22200)
            return ManaFactor.create(0.5f + (level.getDayTime() / 8400f), 1.0f, 1.0f);
        return ManaFactor.create(0.5f, 1.0f, 1.0f);
    }
}
