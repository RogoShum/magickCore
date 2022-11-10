package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ShadowEntity extends ManaProjectileEntity {
    private static final ManaFactor MANA_FACTOR = ManaFactor.create(0.5f, 1.0f, 1.0f);
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/shadow.png");
    public static final ResourceLocation MIST = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/mist.png");
    public ShadowEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public float getSourceLight() {
        return 0;
    }

    @Override
    public ResourceLocation getEntityIcon() {
        return ICON;
    }

    @Override
    public ManaFactor getManaFactor() {
        return MANA_FACTOR;
    }

    @Override
    protected float getGravityVelocity() {
        return 0;
    }

    @Override
    protected void applyParticle() {
        float partial = Minecraft.getInstance().getRenderPartialTicks();
        double x = MathHelper.lerp(partial, this.lastTickPosX, this.getPosX());
        double y = MathHelper.lerp(partial, this.lastTickPosY, this.getPosY());
        double z = MathHelper.lerp(partial, this.lastTickPosZ, this.getPosZ());
        float scale = Math.max(this.getWidth(), 0.5f) * 0.4f;
        for (int i = 0; i < 5; ++i) {
            LitParticle par = new LitParticle(this.world, ModElements.ORIGIN.getRenderer().getParticleSprite()
                    , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + x
                    , MagickCore.getNegativeToOne() * this.getWidth() / 2 + y + this.getHeight() / 2
                    , MagickCore.getNegativeToOne() * this.getWidth() / 2 + z)
                    , scale, scale, 0.2f, 20, MagickCore.proxy.getElementRender(spellContext().element.type()));
            par.setParticleGravity(0f);
            par.setShakeLimit(15f);
            par.setLimitScale();
            MagickCore.addMagickParticle(par);
        }
    }
}
