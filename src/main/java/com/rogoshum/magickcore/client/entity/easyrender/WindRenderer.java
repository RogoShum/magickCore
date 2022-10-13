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
        RenderHelper.CylinderContext context =
                new RenderHelper.CylinderContext(entityIn.getWidth() * 0.4f, entityIn.getWidth() * 0.4f, 1
                        , entityIn.getHeight() / 2, 16
                        , 0.1f * alpha, alpha, 0.3f);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                        wind, entityIn.getHeight(), 0f)).useShader(LibShaders.slime)
                , ModElements.ORIGIN.color()
                , context, null, 0f);
        matrixStackIn.pop();
        matrixStackIn.translate(0, 0.2, 0);
        context = new RenderHelper.CylinderContext(entityIn.getWidth() * 0.5f, entityIn.getWidth() * 0.5f, 1
                        , 0.2f + entityIn.getHeight(), 16
                        , 0.1f * alpha, alpha, 0.3f);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                        wind, entityIn.getHeight(), 0f)).useShader(LibShaders.slime)
                , entityIn.spellContext().element.color()
                , context, null, 0f);
        float height = entityIn.getHeight() - 0.2f;

        context = new RenderHelper.CylinderContext(entityIn.getWidth() * 0.6f, entityIn.getWidth() * 0.6f, 1
                , height, 16
                , 0.0f, alpha, 0.3f);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                        wind, entityIn.getHeight(), 0f)).useShader(LibShaders.opacity)
                , ModElements.ORIGIN.color()
                , context, null, 0f);
        matrixStackIn.translate(0, 0.2, 0);
        context = new RenderHelper.CylinderContext(entityIn.getWidth() * 0.7f, entityIn.getWidth() * 0.7f, 1
                , height, 16
                , 0.0f, alpha, 0.3f);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                        wind, entityIn.getHeight(), 0f)).useShader(LibShaders.opacity)
                , entityIn.spellContext().element.color()
                , context, null, 0f);
        context = new RenderHelper.CylinderContext(entityIn.getWidth() * 2f, entityIn.getWidth() * 2f, 1
                , height / 2, 16
                , 0, 0.8f, 0.3f);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                        wind, entityIn.getHeight(), 0f)).useShader(LibShaders.slime)
                , entityIn.spellContext().element.color()
                , context, null, 0f);
    }
}
