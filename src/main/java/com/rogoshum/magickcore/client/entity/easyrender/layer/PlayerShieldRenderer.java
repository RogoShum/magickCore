package com.rogoshum.magickcore.client.entity.easyrender.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.LayerRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;

public class PlayerShieldRenderer extends EasyLayerRender<LivingEntity> {
    public static LayerRenderHelper helper = new LayerRenderHelper(Minecraft.getInstance().getRenderManager(), null);

    @Override
    public void render(LivingEntity entityIn, LivingRenderer renderer, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, float partialTicks) {
        IEntityState state = entityIn.getCapability(MagickCore.entityState, null).orElse(null);
        int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);

        if(state != null) {
            float value = state.getElementShieldMana();
            if (value > 0.0f) {
                float alpha = value / 100.0f;

                if (value > 100.0f)
                    alpha = 1.0f;

                Matrix4f positionMatrix = matrixStackIn.getLast().getMatrix();
                matrixStackIn.translate(0, entityIn.getHeight() / 2, 0);
                matrixStackIn.scale(entityIn.getWidth() * 3f, entityIn.getHeight() * 1.5f, entityIn.getWidth() * 3f);
                //matrixStackIn.scale(0.97f, 0.97f, 0.97f);
                VectorHitReaction[] test = {};
                state.getElement().getRenderer().renderSphere(positionMatrix, (IRenderTypeBuffer.Impl) bufferIn, RenderHelper.getTexedSphereGlow(blank), 16, 0.1f * alpha, test, false, Integer.toString(entityIn.getEntityId()), packedLightIn);
                state.getElement().getRenderer().renderSphere(positionMatrix, (IRenderTypeBuffer.Impl) bufferIn, RenderHelper.getTexedSphereGlow(sphere_rotate), 16, 0.5f * alpha, test, false, Integer.toString(entityIn.getEntityId()), packedLightIn);
                matrixStackIn.scale(0.56f, 0.56f, 0.56f);
                RenderHelper.renderParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrbGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/shield/element_shield_" + Integer.toString(entityIn.ticksExisted % 10) + ".png"))), alpha, state.getElement().getRenderer().getColor());
            }
        }
    }
}
