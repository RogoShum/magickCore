package com.rogoshum.magickcore.common.entity.projectile;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.ManaStarRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.base.ManaProjectileEntity;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ManaStarEntity extends ManaProjectileEntity {
    private static final ResourceLocation ICON = new ResourceLocation(MagickCore.MOD_ID +":textures/entity/mana_star.png");
    public ManaStarEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        MagickCore.proxy.addRenderer(() -> new ManaStarRenderer(this));
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
        LitParticle par = new LitParticle(this.world, MagickCore.proxy.getElementRender(spellContext().element.type()).getTrailTexture()
                , new Vector3d(this.lastTickPosX + (this.getPosX() - this.lastTickPosX) * partialTicks
                , this.lastTickPosY + (this.getPosY() - this.lastTickPosY) * partialTicks + this.getHeight() / 2
                , this.lastTickPosZ + (this.getPosZ() - this.lastTickPosZ) * partialTicks)
                , 0.3f * getWidth(), 0.3f * getWidth(), 1.0f, 10, MagickCore.proxy.getElementRender(spellContext().element.type()));
        par.setGlow();
        par.setParticleGravity(0);
        par.setLimitScale();
        MagickCore.addMagickParticle(par);
    }

    @Override
    public ManaFactor getManaFactor() {
        if(world.getDayTime() > 13800 && world.getDayTime() <= 22200)
            return ManaFactor.create(0.5f + (world.getDayTime() / 8400f), 1.0f, 1.0f);
        return ManaFactor.create(0.5f, 1.0f, 1.0f);
    }
}
