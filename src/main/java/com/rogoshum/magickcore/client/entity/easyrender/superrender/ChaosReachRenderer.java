package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.ManaRiftEntity;
import com.rogoshum.magickcore.entity.superentity.ChaoReachEntity;
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

public class ChaosReachRenderer extends EasyRenderer<ChaoReachEntity> {

    @Override
    public void render(ChaoReachEntity entityIn, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        Matrix4f positionMatrix = matrixStackIn.getLast().getMatrix();
        int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);

        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            //EasyRenderer.renderRift(matrixStackIn, bufferIn.getBuffer(RenderHelper.ORB), entityIn, 5.0f, entityIn.getElement().getRenderer().getColor(), 1.0f, partialTicks, entityIn.world);
            if(entityIn.initial) {
                float scale = Math.min(1f, (float) (entityIn.ticksExisted - 30) / 5f);
                matrixStackIn.scale(1.45f * scale, 1.45f * scale, 1.45f * scale);
                matrixStackIn.push();
                float c = entityIn.ticksExisted % 11;
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(360f * (c / 10)));
                entityIn.getElement().getRenderer().renderSphere(positionMatrix, bufferIn, RenderHelper.getTexedSphereGlow(blank, 1f, 0f), 6, 0.5f, entityIn.getHitReactions(), 3.2f, packedLightIn);
                matrixStackIn.pop();
                matrixStackIn.push();
                matrixStackIn.scale(scale + 0.2f * MagickCore.rand.nextFloat(), scale + 0.2f * MagickCore.rand.nextFloat(), scale + 0.2f * MagickCore.rand.nextFloat());
                RenderHelper.renderParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrbGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/shield/element_shield_" + Integer.toString(entityIn.ticksExisted % 10) + ".png"))), 1.0f, entityIn.getElement().getRenderer().getColor());
                matrixStackIn.pop();

                matrixStackIn.scale(0.8f, 0.8f, 0.8f);
                entityIn.getElement().getRenderer().renderSphere(positionMatrix, bufferIn, RenderHelper.getTexedSphereGlow(blank, 1f, 0f), 4, 0.9f, entityIn.getHitReactions(), 5.2f, packedLightIn);
            }
        }
    }
}
