package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.pointed.GravityLiftEntity;
import com.rogoshum.magickcore.entity.projectile.LampEntity;
import com.rogoshum.magickcore.lib.LibShaders;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class GravityLiftRenderer extends EasyRenderer<GravityLiftEntity>{

    @Override
    public void render(GravityLiftEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        float alpha = 0.5f - (float)entityIn.ticksExisted % 100 / 100f;
        alpha *= alpha * 4;
        if(alpha < 0.8f)
            alpha = 0.8f;

        float c = entityIn.ticksExisted % 20;
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(360f * (c / 19)));
        RenderHelper.CylinderContext context =
                new RenderHelper.CylinderContext(0.4f, 0.1f, 1.5f
                        , 0.2f + entityIn.getHeight(), 16
                        , 0.5f * alpha, alpha, 0.3f);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                        wind, entityIn.getHeight(), 0f)).useShader(LibShaders.slime)
                , entityIn.spellContext().element.color()
                , context, entityIn.getHitReactions(), 0f);

        context =
                new RenderHelper.CylinderContext(0.6f, 0.2f, 1.5f
                        , 0.2f + entityIn.getHeight(), 16
                        , 0.2f * alpha, alpha * 0.8f, 0.3f);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                        wind, entityIn.getHeight(), 0f)).useShader(LibShaders.slime)
                , entityIn.spellContext().element.color()
                , context, entityIn.getHitReactions(), 0f);

        matrixStackIn.translate(0, entityIn.getHeight() * 0.5, 0);
        alpha = 1.0f;
        context = new RenderHelper.CylinderContext(2f, 2f, 2f
                , 0.5f, 16
                , 0.35f * alpha, alpha, 0.8f);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                        wind, 0.5f, 0f)).useShader(LibShaders.opacity)
                , entityIn.spellContext().element.color()
                , context, entityIn.getHitReactions(), 0f);

        for (int i = 0; i <= entityIn.getHeight() + 1; i+=2) {
            float radius = 0.9f;
            if(i % 4 == 0)
                radius = 1.05f;
            context = new RenderHelper.CylinderContext(radius, radius, 2f
                    , 1f, 16
                    , 0.35f * alpha, alpha, 0.7f);
            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                            wind, 0.5f, 0f)).useShader(LibShaders.opacity)
                    , entityIn.spellContext().element.color()
                    , context, entityIn.getHitReactions(), 0f);
            matrixStackIn.translate(0, -2, 0);
        }
    }
}
