package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.BufferPackage;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.ManaOrbEntity;
import com.rogoshum.magickcore.entity.ManaRiftEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class ManaRiftRenderer extends EasyRenderer<ManaRiftEntity>{

    @Override
    public void render(ManaRiftEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {

        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            EasyRenderer.renderRift(matrixStackIn, bufferIn, RenderHelper.ORB, entityIn, 4.0f, entityIn.getElement().getRenderer().getColor()
                    , 1.0f, partialTicks, entityIn.world);
            matrixStackIn.translate(0, -entityIn.getHeight() / 2 + 0.005, 0);
            matrixStackIn.push();
            matrixStackIn.scale(entityIn.getWidth() / 1.35f, 0, entityIn.getWidth() / 1.35f);
            Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
            BufferPackage pack = BufferPackage.create(matrixStackIn, bufferIn, RenderHelper.getTexedOrbGlow(entityIn.getElement().getRenderer().getOrbTexture()));
            RenderHelper.begin(pack);
            float[] color = entityIn.getElement().getRenderer().getColor();
            float alpha = 0.35f;
            bufferIn.pos(matrix4f, -1.0f, 0.0f, -1.0f).color(color[0], color[1], color[2], alpha).tex(1.0f, 1.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(-1.0f, 0.0f, -1.0f).endVertex();
            bufferIn.pos(matrix4f, -1.0f, 0.0f, 1.0f).color(color[0], color[1], color[2], alpha).tex(1.0f, 0.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(-1.0f, 0.0f, 1.0f).endVertex();
            bufferIn.pos(matrix4f, 1.0f, 0.0f, 1.0f).color(color[0], color[1], color[2], alpha).tex(0.0f, 0.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(1.0f, 0.0f, 1.0f).endVertex();
            bufferIn.pos(matrix4f, 1.0f, 0.0f, -1.0f).color(color[0], color[1], color[2], alpha).tex(0.0f, 1.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(1.0f, 0.0f, -1.0f).endVertex();
            RenderHelper.finish(pack);
            matrixStackIn.pop();
        }
    }
}
