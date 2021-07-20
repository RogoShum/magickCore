package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.ManaOrbEntity;
import com.rogoshum.magickcore.entity.ManaRiftEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class ManaRiftRenderer extends EasyRenderer<ManaRiftEntity>{

    @Override
    public void render(ManaRiftEntity entityIn, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        matrixStackIn.translate(0, -entityIn.getHeight() / 2 + 0.005, 0);
        matrixStackIn.scale(1.002f, 1.002f, 1.002f);
        Matrix4f positionMatrix = matrixStackIn.getLast().getMatrix();
        int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);

        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            EasyRenderer.renderRift(matrixStackIn, bufferIn.getBuffer(RenderHelper.ORB), entityIn, 3.0f, entityIn.getElement().getRenderer().getColor()
                    , 5.0f, partialTicks, entityIn.world, entityIn.getUniqueID().toString(), 0.0f);
            matrixStackIn.scale(0.99f, 0.99f, 0.99f);
            EasyRenderer.renderRift(matrixStackIn, bufferIn.getBuffer(RenderHelper.LIGHTING), entityIn, 3.0f, entityIn.getElement().getRenderer().getColor()
                    , 5.0f, partialTicks, entityIn.world, entityIn.getUniqueID().toString(), 0.0f);


            matrixStackIn.push();
            matrixStackIn.scale(entityIn.getWidth() / 1.35f, 0, entityIn.getWidth() / 1.35f);
            Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
            IVertexBuilder buffer = bufferIn.getBuffer(RenderHelper.getTexedOrbGlow(entityIn.getElement().getRenderer().getOrbTexture()));
            float[] color = entityIn.getElement().getRenderer().getColor();
            float alpha = 0.15f;
            buffer.pos(matrix4f, -1.0f, 0.0f, -1.0f).color(color[0], color[1], color[2], alpha).tex(1.0f, 1.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(-1.0f, 0.0f, -1.0f).endVertex();
            buffer.pos(matrix4f, -1.0f, 0.0f, 1.0f).color(color[0], color[1], color[2], alpha).tex(1.0f, 0.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(-1.0f, 0.0f, 1.0f).endVertex();
            buffer.pos(matrix4f, 1.0f, 0.0f, 1.0f).color(color[0], color[1], color[2], alpha).tex(0.0f, 0.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(1.0f, 0.0f, 1.0f).endVertex();
            buffer.pos(matrix4f, 1.0f, 0.0f, -1.0f).color(color[0], color[1], color[2], alpha).tex(0.0f, 1.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(1.0f, 0.0f, -1.0f).endVertex();
            matrixStackIn.pop();
        }
    }
}
