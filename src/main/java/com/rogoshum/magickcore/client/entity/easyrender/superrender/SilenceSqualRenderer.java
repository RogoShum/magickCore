package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.entity.superentity.SilenceSquallEntity;
import com.rogoshum.magickcore.lib.LibShaders;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.vector.Vector3f;

public class SilenceSqualRenderer extends EasyRenderer<SilenceSquallEntity> {

    @Override
    public void render(SilenceSquallEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        if(entityIn.spellContext().element != null && entityIn.spellContext().element.getRenderer() != null) {
            ElementRenderer renderer = entityIn.spellContext().element.getRenderer();
            float alpha = Math.min(1.0f, (float)entityIn.ticksExisted / 100f);
            //EasyRenderer.renderRift(matrixStackIn, bufferIn.getBuffer(RenderHelper.ORB), entityIn, 11.0f, renderer.getColor()
                    //, 0.7f, partialTicks, entityIn.world);
            matrixStackIn.scale(1.45f, 1.45f, 1.45f);
            matrixStackIn.push();
            matrixStackIn.scale(1.0f, 3.6f, 1.0f);
            RenderHelper.renderParticle(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedOrbGlow(renderer.getCycleTexture())), alpha, renderer.getColor());
            matrixStackIn.scale(0.9f, 0.9f, 0.9f);
            RenderHelper.renderParticle(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedOrbGlow(renderer.getCycleTexture())), 0.5f * alpha, renderer.getColor());
            matrixStackIn.pop();
            float c = entityIn.ticksExisted % 5;
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(360f * (c / 4)));

            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                    renderer.getWindTexture(0), 1f, 0f)).useShader(LibShaders.slime)
                    , 0.0f, 0.8f * alpha, renderer.getColor()
                    , 8.0f, 2.35f, 6, entityIn.getHitReactions(), 0f);

            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                    renderer.getWindTexture(1), 1f, 0f)).useShader(LibShaders.slime)
                    , 0.0f,0.8f * alpha, renderer.getColor()
                    , 3.85f, 4.25f, 5, entityIn.getHitReactions(), 0f);

            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                    renderer.getWindTexture(2), 1f, 0f)).useShader(LibShaders.slime)
                    , 0.0f,0.8f * alpha, renderer.getColor()
                    , 6.0f, 6.35f, 4, entityIn.getHitReactions(), 0f);

            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                    renderer.getWindTexture(3), 1f, 0f)).useShader(LibShaders.slime)
                    , 0.0f,0.8f * alpha, renderer.getColor()
                    , 2f, 10.25f, 3, entityIn.getHitReactions(), 0f);

            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                    renderer.getWindTexture(0), 1f, 0f)).useShader(LibShaders.slime)
                    , 0.0f, 0.8f * alpha, renderer.getColor()
                    , 8.0f, 2.35f, 6, entityIn.getHitReactions(), 0f);

            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                    renderer.getWindTexture(1), 1f, 0f)).useShader(LibShaders.slime)
                    , 0.0f,0.8f * alpha, renderer.getColor()
                    , 3.85f, 4.25f, 5, entityIn.getHitReactions(), 0f);

            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                    renderer.getWindTexture(2), 1f, 0f)).useShader(LibShaders.slime)
                    , 0.0f,0.8f * alpha, renderer.getColor()
                    , 6.0f, 6.35f, 4, entityIn.getHitReactions(), 0f);

            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                    renderer.getWindTexture(3), 1f, 0f)).useShader(LibShaders.slime)
                    , 0.0f,0.8f * alpha, renderer.getColor()
                    , 2f, 10.25f, 3, entityIn.getHitReactions(), 0f);

        }
    }
}
