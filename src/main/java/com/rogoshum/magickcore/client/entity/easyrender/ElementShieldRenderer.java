package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.baseEntity.ManaProjectileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

public class ElementShieldRenderer extends EasyRenderer<Entity>{

    @Override
    public void preRender(Entity entity, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        render(entity, matrixStackIn, bufferIn, partialTicks);
    }

    @Override
    public void render(Entity entity, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        Entity player = Minecraft.getInstance().player;

        IEntityState state = entity.getCapability(MagickCore.entityState, null).orElse(null);
        if(state != null)
        {
            float value = state.getElementShieldMana();
            if(value > 0.0f) {
                float alpha = value / 100.0f;

                if(value > 100.0f)
                    alpha = 1.0f;

                float[] color = state.getElement().getRenderer().getColor();
                matrixStackIn.push();
                double x = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * (double) partialTicks;
                double y = entity.lastTickPosY + (entity.getPosY() - entity.lastTickPosY) * (double) partialTicks;
                double z = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * (double) partialTicks;

                Vector3d offset = player.getEyePosition(partialTicks).subtract(new Vector3d(x, y, z)).normalize().mul(entity.getWidth() * 1.3d, entity.getHeight() / 2, entity.getWidth() * 1.3d);

                if(entity == player)
                    offset = offset.add(0, -1, 0);

                /*if (entity.ticksExisted % 15 == 0 && !Minecraft.getInstance().isGamePaused()) {
                    LitParticle par = new LitParticle(entity.world, new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/ripple/ripple_" + Integer.toString(MagickCore.rand.nextInt(5)) + ".png")
                            , new Vector3d(MagickCore.getNegativeToOne() * entity.getWidth() * 1.5f + entity.getPosX() + offset.x
                            , MagickCore.getNegativeToOne() * entity.getHeight() * 1.2f + entity.getPosY() + entity.getHeight() / 2 + offset.y
                            , MagickCore.getNegativeToOne() * entity.getWidth() * 1.5f + entity.getPosZ() + offset.z)
                            , entity.getWidth() * 1.0f, entity.getWidth() * 1.0f, alpha, 40, state.getElement().getRenderer());
                    par.setGlow();
                    par.setParticleGravity(0);
                    par.setShakeLimit(15f);
                    MagickCore.addMagickParticle(par);
                }*/
                if(entity != player) {
                    Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
                    double camX = cam.x, camY = cam.y, camZ = cam.z;
                    matrixStackIn.translate(x - camX + offset.x, y - camY + entity.getHeight() * 1.1f / 2 + offset.y, z - camZ + offset.z);
                    matrixStackIn.scale(entity.getWidth() * 1.7f, entity.getHeight() * 0.9f, entity.getWidth() * 1.7f);
                    RenderHelper.renderParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrbGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/shield/element_shield_" + Integer.toString(entity.ticksExisted % 10) + ".png"))), 0.9f * alpha, color);
                    //if(entity.ticksExisted % 2 == 0)
                    //RenderHelper.renderParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrbGlow(new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/ripple/ripple_" + Integer.toString((entity.ticksExisted % 10) / 2) + ".png"))), 1.0f, RenderHelper.SOLAR);
                    matrixStackIn.scale(0.97f, 0.97f, 0.97f);
                    RenderHelper.renderParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrbGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/sphere_bloom.png"))), 0.5f * alpha, color);
                }
                matrixStackIn.pop();
                bufferIn.finish();
            }
        }
    }
}
