package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.entity.superentity.DawnWardEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;

public class DawnWardRenderer extends EasyRenderer<DawnWardEntity> {
    private static ResourceLocation blankTex = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");

    @Override
    public void render(DawnWardEntity entityIn, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        //int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);
        int packedLightIn = RenderHelper.renderLight;
        Matrix4f positionMatrix = matrixStackIn.getLast().getMatrix();
        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            //EasyRenderer.renderRift(matrixStackIn, bufferIn.getBuffer(RenderHelper.ORB), entityIn, 6.0f, entityIn.getElement().getRenderer().getColor()
                    //, 2.0f, partialTicks, entityIn.world);
            if(entityIn.initial) {
                float scale = Math.min(1f, (float) (entityIn.ticksExisted - 25) / 5f) * entityIn.getWidth();
                matrixStackIn.scale(scale, scale, scale);
                VectorHitReaction[] test = {};
                entityIn.getElement().getRenderer().renderSphere(positionMatrix, bufferIn, RenderHelper.getTexedSphereGlow(RenderHelper.ripple_5, 3f, 0f), 16, 1.0f, entityIn.getHitReactions(), 0.3f, packedLightIn);
                matrixStackIn.scale(0.99f, 0.99f, 0.99f);
                entityIn.getElement().getRenderer().renderSphere(positionMatrix, bufferIn, RenderHelper.getTexedSphereGlow(blank, 1f, 0f), 16, 0.3f, entityIn.getHitReactions(), 0.5f, packedLightIn);
            }
        }
    }
}
