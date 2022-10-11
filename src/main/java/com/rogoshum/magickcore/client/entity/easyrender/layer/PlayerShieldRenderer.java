package com.rogoshum.magickcore.client.entity.easyrender.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.LayerRenderHelper;
import com.rogoshum.magickcore.lib.LibEntityData;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class PlayerShieldRenderer extends EasyLayerRender<ClientPlayerEntity> {
    public static LayerRenderHelper helper = new LayerRenderHelper(Minecraft.getInstance().getRenderManager(), null);

    @Override
    public void render(ClientPlayerEntity entityIn, LivingRenderer renderer, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, float partialTicks) {
        ExtraDataHelper.entityData(entityIn).<EntityStateData>execute(LibEntityData.ENTITY_STATE, state -> {
            int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);

            if(state != null) {
                float value = state.getElementShieldMana();
                if (value > 0.0f) {
                    float alpha = value / entityIn.getMaxHealth();

                    if (value > entityIn.getMaxHealth())
                        alpha = 1.0f;

                    matrixStackIn.translate(0, entityIn.getHeight() / 2, 0);
                    matrixStackIn.scale(entityIn.getWidth() * 3f, entityIn.getHeight() * 1.5f, entityIn.getWidth() * 3f);
                    //matrixStackIn.scale(0.97f, 0.97f, 0.97f);
                    VectorHitReaction[] test = {};
                    //state.getElement().getRenderer().renderSphere(positionMatrix, (IRenderTypeBuffer.Impl) bufferIn, RenderHelper.getTexedSphereGlow(blank, 1f, 0f), 16, 0.1f * alpha, test, false, Integer.toString(entityIn.getEntityId()), packedLightIn);
                    RenderType type = RenderHelper.getTexedSphereGlow(sphere_rotate, 1f, 0f);
                    state.getElement().getRenderer().renderSphere(
                            BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), type), 16, alpha, test, false, Integer.toString(entityIn.getEntityId()), packedLightIn);
                    matrixStackIn.scale(0.56f, 0.56f, 0.56f);
                    RenderHelper.renderParticle(
                            BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), RenderHelper.getTexedOrbGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/shield/element_shield_" + Integer.toString(entityIn.ticksExisted % 10) + ".png")))
                            , alpha, state.getElement().getRenderer().getColor());
                }
            }
        });
    }
}
