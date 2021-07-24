package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.ManaOrbEntity;
import com.rogoshum.magickcore.entity.ManaSphereEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;

public class ManaSphereRenderer extends EasyRenderer<ManaSphereEntity>{

    @Override
    public void render(ManaSphereEntity entityIn, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        Matrix4f positionMatrix = matrixStackIn.getLast().getMatrix();
        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            EasyRenderer.renderRift(matrixStackIn, bufferIn.getBuffer(RenderHelper.ORB), entityIn, 3.0f, entityIn.getElement().getRenderer().getColor()
                    , 1.0f, partialTicks, entityIn.world);

            float scale = entityIn.getWidth() * 1.6f;
            if(entityIn.ticksExisted < 9)
                scale *= 1 - 1f / ((float)entityIn.ticksExisted + 1f);

            if(entityIn.getTickTime() - entityIn.ticksExisted <= 9)
                scale *= 1 - 1f / (float)(entityIn.getTickTime() - entityIn.ticksExisted);

            int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);
            matrixStackIn.scale(scale, scale, scale);
            VectorHitReaction[] test = {};
            entityIn.getElement().getRenderer().renderSphere(positionMatrix, bufferIn, RenderHelper.getTexedSphereGlow(blank), 6, 0.4f, entityIn.getHitReactions(), 2.10f, packedLightIn);
            //matrixStackIn.scale(0.48f, 0.48f, 0.48f);
            //entityIn.getElement().getRenderer().renderOrb(matrixStackIn, bufferIn, 0.5f, Integer.toString(entityIn.getEntityId()), 0.1f);
            matrixStackIn.scale(0.30f, 0.30f, 0.30f);
            entityIn.getElement().getRenderer().renderSphere(positionMatrix, bufferIn, RenderHelper.getTexedSphereGlow(blank), 4, 0.9f, entityIn.getHitReactions(), 6.0f, packedLightIn);
        }
    }
}
