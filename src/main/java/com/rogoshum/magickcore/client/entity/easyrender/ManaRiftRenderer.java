package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.entity.pointed.ManaRiftEntity;
import com.rogoshum.magickcore.magick.Color;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.vector.Matrix4f;

public class ManaRiftRenderer extends EasyRenderer<ManaRiftEntity>{

    @Override
    public void render(ManaRiftEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {

        if(entityIn.spellContext().element != null && entityIn.spellContext().element.getRenderer() != null) {
            EasyRenderer.renderRift(matrixStackIn, bufferIn, RenderHelper.ORB, entityIn, 4.0f, entityIn.spellContext().element.getRenderer().getColor()
                    , 1.0f, partialTicks, entityIn.world);
            matrixStackIn.translate(0, -entityIn.getHeight() / 2 + 0.005, 0);
            matrixStackIn.push();
            matrixStackIn.scale(entityIn.getWidth() / 1.35f, 0, entityIn.getWidth() / 1.35f);
            Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
            BufferContext pack = BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedOrbGlow(entityIn.spellContext().element.getRenderer().getOrbTexture()));
            RenderHelper.begin(pack);
            Color color = entityIn.spellContext().element.getRenderer().getColor();
            float alpha = 0.35f;
            bufferIn.pos(matrix4f, -1.0f, 0.0f, -1.0f).color(color.r(), color.g(), color.b(), alpha).tex(1.0f, 1.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(-1.0f, 0.0f, -1.0f).endVertex();
            bufferIn.pos(matrix4f, -1.0f, 0.0f, 1.0f).color(color.r(), color.g(), color.b(), alpha).tex(1.0f, 0.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(-1.0f, 0.0f, 1.0f).endVertex();
            bufferIn.pos(matrix4f, 1.0f, 0.0f, 1.0f).color(color.r(), color.g(), color.b(), alpha).tex(0.0f, 0.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(1.0f, 0.0f, 1.0f).endVertex();
            bufferIn.pos(matrix4f, 1.0f, 0.0f, -1.0f).color(color.r(), color.g(), color.b(), alpha).tex(0.0f, 1.0f)
                    .overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(1.0f, 0.0f, -1.0f).endVertex();
            RenderHelper.finish(pack);
            matrixStackIn.pop();
        }
    }
}
