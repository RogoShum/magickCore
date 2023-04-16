package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.ManaOrbRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.ManaStarRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class ManaOrbEntity extends ManaProjectileEntity {
    private static final ManaFactor MANA_FACTOR = ManaFactor.create(1.0f, 1.0f, 1.0f);
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/mana_orb.png");
    public ManaOrbEntity(EntityType<? extends ThrowableProjectile> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void makeSound() {
        if (this.tickCount == 1) {
            this.playSound(ModSounds.gatorix_spawn.get(), 0.25F, 2.0F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaProjectileEntity>> getRenderer() {
        return () -> new ManaOrbRenderer(this);
    }

    @Override
    public float getSourceLight() {
        return 6;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public void renderFrame(float partialTicks) {
        LitParticle par = new LitParticle(this.level, MagickCore.proxy.getElementRender(spellContext().element.type()).getTrailTexture()
                , new Vec3(this.xOld + (this.getX() - this.xOld) * partialTicks
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
        return ManaFactor.DEFAULT;
    }
}
