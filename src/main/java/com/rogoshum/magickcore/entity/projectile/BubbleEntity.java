package com.rogoshum.magickcore.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.entity.base.ManaProjectileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class BubbleEntity extends ManaProjectileEntity {
    public static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/bubble.png");
    public BubbleEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
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
    protected void applyParticle() {
        if(isInWater()) return;
        float partial = Minecraft.getInstance().getRenderPartialTicks();
        double x = MathHelper.lerp(partial, this.lastTickPosX, this.getPosX());
        double y = MathHelper.lerp(partial, this.lastTickPosY, this.getPosY());
        double z = MathHelper.lerp(partial, this.lastTickPosZ, this.getPosZ());
        float scale = Math.max(this.getWidth(), 0.5f) * 0.4f;

        LitParticle par = new LitParticle(this.world, ICON
                , new Vector3d(MagickCore.getNegativeToOne() * this.getWidth() / 2 + x
                , MagickCore.getNegativeToOne() * this.getWidth() / 2 + y + this.getHeight() / 2
                , MagickCore.getNegativeToOne() * this.getWidth() / 2 + z)
                , scale, scale, 0.5f, 20, MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.setParticleGravity(0f);
        par.setGlow();
        par.setShakeLimit(5f);
        par.setNoScale();
        par.addMotion(getMotion().x * 0.2, getMotion().y * 0.2, getMotion().z * 0.2);
        MagickCore.addMagickParticle(par);
    }
}
