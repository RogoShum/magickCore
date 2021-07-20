package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.superentity.SilenceSquallEntity;
import com.rogoshum.magickcore.entity.superentity.ThornsCaressEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.Iterator;

public class SilenceSqualRenderer extends EasyRenderer<SilenceSquallEntity> {

    @Override
    public void render(SilenceSquallEntity entityIn, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        matrixStackIn.translate(0, -entityIn.getHeight(), 0);
        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            float alpha = Math.min(1.0f, (float)entityIn.ticksExisted / 100f);
            matrixStackIn.translate(0, entityIn.getHeight(), 0);
            matrixStackIn.scale(1.45f, 1.45f, 1.45f);
            matrixStackIn.push();
            matrixStackIn.scale(1.0f, 3.6f, 1.0f);
            RenderHelper.renderParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrbGlow(entityIn.getElement().getRenderer().getCycleTexture())), alpha, entityIn.getElement().getRenderer().getColor());
            matrixStackIn.scale(0.9f, 0.9f, 0.9f);
            RenderHelper.renderParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrbGlow(entityIn.getElement().getRenderer().getCycleTexture())), 0.5f * alpha, entityIn.getElement().getRenderer().getColor());
            matrixStackIn.pop();
            float c = entityIn.ticksExisted % 5;
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(360f * (c / 4)));
            RenderHelper.renderCylinder(RenderHelper.getTexedCylinderGlint(entityIn.getElement().getRenderer().getWindTexture(0)), matrixStackIn, bufferIn, 0.02f * alpha, entityIn.getElement().getRenderer().getColor()
                    , 8.0f, 2.35f, 6, true, entityIn.getUniqueID().toString(), 0f);

            RenderHelper.renderCylinder(RenderHelper.getTexedCylinderGlint(entityIn.getElement().getRenderer().getWindTexture(1)), matrixStackIn, bufferIn, 0.02f * alpha, entityIn.getElement().getRenderer().getColor()
                    , 3.85f, 4.25f, 5, true, entityIn.getUniqueID().toString(), 0f);

            RenderHelper.renderCylinder(RenderHelper.getTexedCylinderGlint(entityIn.getElement().getRenderer().getWindTexture(2)), matrixStackIn, bufferIn, 0.02f * alpha, entityIn.getElement().getRenderer().getColor()
                    , 6.0f, 6.35f, 4, true, entityIn.getUniqueID().toString(), 0f);

            RenderHelper.renderCylinder(RenderHelper.getTexedCylinderGlint(entityIn.getElement().getRenderer().getWindTexture(3)), matrixStackIn, bufferIn, 0.02f * alpha, entityIn.getElement().getRenderer().getColor()
                    , 2f, 10.25f, 3, true, entityIn.getUniqueID().toString(), 0f);

        }
    }
}
