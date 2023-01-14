package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.projectile.BubbleRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class BubbleEntity extends ManaProjectileEntity {
    public static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/bubble.png");
    public BubbleEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }
    private static final ManaFactor WATER_FACTOR = ManaFactor.create(2.0f, 1.0f, 1.0f);
    private static final ManaFactor AIR_FACTOR = ManaFactor.create(0.5f, 1.0f, 0.5f);

    @Override
    public void tick() {
        super.tick();
    }

    @OnlyIn(Dist.CLIENT)
    public Supplier<EasyRenderer<? extends ManaProjectileEntity>> getRenderer() {
        return () -> new BubbleRenderer(this);
    }

    @Override
    public boolean isInWater() {
        return !super.isInWater();
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
    public ManaFactor getManaFactor() {
        return isInWater() ? AIR_FACTOR : WATER_FACTOR;
    }

    @Override
    protected void applyParticle() {
        if(isInWater()) return;
        float partial = Minecraft.getInstance().getFrameTime();
        double x = MathHelper.lerp(partial, this.xOld, this.getX());
        double y = MathHelper.lerp(partial, this.yOld, this.getY());
        double z = MathHelper.lerp(partial, this.zOld, this.getZ());
        float scale = Math.max(this.getBbWidth(), 0.5f) * 0.4f;

        LitParticle par = new LitParticle(this.level, ICON
                , new Vector3d(MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + x
                , MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + y + this.getBbHeight() / 2
                , MagickCore.getNegativeToOne() * this.getBbWidth() / 2 + z)
                , scale, scale, 0.5f, 20, MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.setParticleGravity(0f);
        par.setGlow();
        par.setShakeLimit(5f);
        par.setNoScale();
        par.addMotion(getDeltaMovement().x * 0.2, getDeltaMovement().y * 0.2, getDeltaMovement().z * 0.2);
        MagickCore.addMagickParticle(par);
    }
}
