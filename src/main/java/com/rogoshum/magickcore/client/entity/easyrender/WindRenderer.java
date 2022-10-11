package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.projectile.LampEntity;
import com.rogoshum.magickcore.entity.projectile.WindEntity;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibShaders;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class WindRenderer extends EasyRenderer<WindEntity>{

    @Override
    public void render(WindEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        float alpha = 1.0f;
        float c = entityIn.ticksExisted % 30;
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(360f * (c / 29)));
        matrixStackIn.translate(0, -0.2, 0);
        matrixStackIn.push();
        matrixStackIn.translate(0, 0.2, 0);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                        wind, entityIn.getHeight(), 0f)).useShader(LibShaders.slime)
                , 0.2f * alpha, alpha, ModElements.ORIGIN.color()
                , entityIn.getWidth() * 0.4f, entityIn.getHeight(), 16, null, 0f);
        matrixStackIn.pop();
        matrixStackIn.translate(0, 0.2, 0);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                        wind, entityIn.getHeight(), 0f)).useShader(LibShaders.slime)
                , 0.1f * alpha, alpha, entityIn.spellContext().element.color()
                , entityIn.getWidth() * 0.5f, 0.2f + entityIn.getHeight(), 16, null, 0f);
        float height = entityIn.getHeight() - 0.2f;

        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                        wind, entityIn.getHeight(), 0f)).useShader(LibShaders.opacity)
                , 0.4f, alpha, ModElements.ORIGIN.color()
                , entityIn.getWidth() * 0.6f, height, 16, null, 0f);
        matrixStackIn.translate(0, 0.2, 0);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                        wind, entityIn.getHeight(), 0f)).useShader(LibShaders.opacity)
                , 0.0f, alpha, entityIn.spellContext().element.color()
                , entityIn.getWidth() * 0.7f, height, 16, null, 0f);

        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                        wind, entityIn.getHeight(), 0f)).useShader(LibShaders.slime)
                , 0.1f, alpha, entityIn.spellContext().element.color()
                , entityIn.getWidth() * 2f, height, 16, null, 0f);
    }
}
