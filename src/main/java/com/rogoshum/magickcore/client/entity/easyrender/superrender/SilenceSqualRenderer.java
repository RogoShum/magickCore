package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.BufferPackage;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.superentity.SilenceSquallEntity;
import com.rogoshum.magickcore.entity.superentity.ThornsCaressEntity;
import com.rogoshum.magickcore.lib.LibShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
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
    public void render(SilenceSquallEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            float alpha = Math.min(1.0f, (float)entityIn.ticksExisted / 100f);
            //EasyRenderer.renderRift(matrixStackIn, bufferIn.getBuffer(RenderHelper.ORB), entityIn, 11.0f, entityIn.getElement().getRenderer().getColor()
                    //, 0.7f, partialTicks, entityIn.world);
            matrixStackIn.scale(1.45f, 1.45f, 1.45f);
            matrixStackIn.push();
            matrixStackIn.scale(1.0f, 3.6f, 1.0f);
            RenderHelper.renderParticle(BufferPackage.create(matrixStackIn, bufferIn, RenderHelper.getTexedOrbGlow(entityIn.getElement().getRenderer().getCycleTexture())), alpha, entityIn.getElement().getRenderer().getColor());
            matrixStackIn.scale(0.9f, 0.9f, 0.9f);
            RenderHelper.renderParticle(BufferPackage.create(matrixStackIn, bufferIn, RenderHelper.getTexedOrbGlow(entityIn.getElement().getRenderer().getCycleTexture())), 0.5f * alpha, entityIn.getElement().getRenderer().getColor());
            matrixStackIn.pop();
            float c = entityIn.ticksExisted % 5;
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(360f * (c / 4)));

            RenderHelper.renderCylinder(BufferPackage.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                    entityIn.getElement().getRenderer().getWindTexture(0), 1f, 0f))
                    , 0.0f, 0.8f * alpha, entityIn.getElement().getRenderer().getColor()
                    , 8.0f, 2.35f, 6, entityIn.getHitReactions(), 0f);

            RenderHelper.renderCylinder(BufferPackage.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                    entityIn.getElement().getRenderer().getWindTexture(1), 1f, 0f))
                    , 0.0f,0.8f * alpha, entityIn.getElement().getRenderer().getColor()
                    , 3.85f, 4.25f, 5, entityIn.getHitReactions(), 0f);

            RenderHelper.renderCylinder(BufferPackage.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                    entityIn.getElement().getRenderer().getWindTexture(2), 1f, 0f))
                    , 0.0f,0.8f * alpha, entityIn.getElement().getRenderer().getColor()
                    , 6.0f, 6.35f, 4, entityIn.getHitReactions(), 0f);

            RenderHelper.renderCylinder(BufferPackage.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                    entityIn.getElement().getRenderer().getWindTexture(3), 1f, 0f))
                    , 0.0f,0.8f * alpha, entityIn.getElement().getRenderer().getColor()
                    , 2f, 10.25f, 3, entityIn.getHitReactions(), 0f);

            RenderHelper.renderCylinder(BufferPackage.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                    entityIn.getElement().getRenderer().getWindTexture(0), 1f, 0f)).useShader(LibShaders.slime)
                    , 0.0f, 0.8f * alpha, entityIn.getElement().getRenderer().getColor()
                    , 8.0f, 2.35f, 6, entityIn.getHitReactions(), 0f);

            RenderHelper.renderCylinder(BufferPackage.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                    entityIn.getElement().getRenderer().getWindTexture(1), 1f, 0f)).useShader(LibShaders.slime)
                    , 0.0f,0.8f * alpha, entityIn.getElement().getRenderer().getColor()
                    , 3.85f, 4.25f, 5, entityIn.getHitReactions(), 0f);

            RenderHelper.renderCylinder(BufferPackage.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                    entityIn.getElement().getRenderer().getWindTexture(2), 1f, 0f)).useShader(LibShaders.slime)
                    , 0.0f,0.8f * alpha, entityIn.getElement().getRenderer().getColor()
                    , 6.0f, 6.35f, 4, entityIn.getHitReactions(), 0f);

            RenderHelper.renderCylinder(BufferPackage.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                    entityIn.getElement().getRenderer().getWindTexture(3), 1f, 0f)).useShader(LibShaders.slime)
                    , 0.0f,0.8f * alpha, entityIn.getElement().getRenderer().getColor()
                    , 2f, 10.25f, 3, entityIn.getHitReactions(), 0f);

        }
    }
}
