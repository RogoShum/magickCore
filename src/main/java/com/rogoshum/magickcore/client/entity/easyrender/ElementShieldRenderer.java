package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.lib.LibEntityData;
import com.rogoshum.magickcore.magick.Color;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

public class ElementShieldRenderer extends EasyRenderer<Entity>{

    @Override
    public void preRender(Entity entity, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        render(entity, matrixStackIn, bufferIn, partialTicks);
    }

    @Override
    public void render(Entity entity, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        Entity player = Minecraft.getInstance().player;

        ExtraDataHelper.entityData(entity).<EntityStateData>execute(LibEntityData.ENTITY_STATE, state -> {
            float value = state.getElementShieldMana();
            if(value > 0.0f) {
                float alpha = value / ((LivingEntity)entity).getMaxHealth();

                if(value > ((LivingEntity)entity).getMaxHealth())
                    alpha = 1.0f;

                Color color = state.getElement().getRenderer().getColor();
                matrixStackIn.push();
                double x = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * (double) partialTicks;
                double y = entity.lastTickPosY + (entity.getPosY() - entity.lastTickPosY) * (double) partialTicks;
                double z = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * (double) partialTicks;

                Vector3d offset = player.getEyePosition(partialTicks).subtract(new Vector3d(x, y, z)).normalize().mul(entity.getWidth() * 1.3d, entity.getHeight() / 2, entity.getWidth() * 1.3d);

                if(entity == player)
                    offset = offset.add(0, -1, 0);

                if (entity.ticksExisted % 5 == 0 && partialTicks < 0.1f && !Minecraft.getInstance().isGamePaused()) {
                    LitParticle par = new LitParticle(entity.world, new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/ripple/ripple_" + Integer.toString(MagickCore.rand.nextInt(5)) + ".png")
                            , new Vector3d(MagickCore.getNegativeToOne() * entity.getWidth() * 1.5f + entity.getPosX() + offset.x
                            , MagickCore.getNegativeToOne() * entity.getHeight() * 1.2f + entity.getPosY() + entity.getHeight() / 2 + offset.y
                            , MagickCore.getNegativeToOne() * entity.getWidth() * 1.5f + entity.getPosZ() + offset.z)
                            , entity.getWidth(), entity.getWidth(), alpha, 60, state.getElement().getRenderer());
                    par.setGlow();
                    par.setParticleGravity(0);
                    par.setShakeLimit(5f);
                    par.setLimitScale();
                    MagickCore.addMagickParticle(par);
                }
                if(entity != player) {
                    Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
                    double camX = cam.x, camY = cam.y, camZ = cam.z;
                    matrixStackIn.translate(x - camX + offset.x, y - camY + entity.getHeight() * 1.1f / 2 + offset.y, z - camZ + offset.z);
                    matrixStackIn.scale(entity.getWidth() * 1.7f, entity.getHeight() * 0.9f, entity.getWidth() * 1.7f);
                    RenderHelper.renderParticle(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedOrbGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/shield/element_shield_" + Integer.toString(entity.ticksExisted % 10) + ".png"))), 0.9f * alpha, color);
                    //if(entity.ticksExisted % 2 == 0)
                    //RenderHelper.renderParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrbGlow(new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/ripple/ripple_" + Integer.toString((entity.ticksExisted % 10) / 2) + ".png"))), 1.0f, RenderHelper.SOLAR);
                    matrixStackIn.scale(0.97f, 0.97f, 0.97f);
                    RenderHelper.renderParticle(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedOrbGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/sphere_bloom.png"))), 0.5f * alpha, color);
                }
                matrixStackIn.pop();
            }
        });
    }
}
