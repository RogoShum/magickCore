package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.superentity.ChaoReachEntity;
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

public class ThornsCaressRenderer extends EasyRenderer<ThornsCaressEntity> {

    @Override
    public void render(ThornsCaressEntity entityIn, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        matrixStackIn.translate(0, -entityIn.getHeight() / 2 + 0.005, 0);
        matrixStackIn.scale(1.002f, 1.002f, 1.002f);
        Matrix4f positionMatrix = matrixStackIn.getLast().getMatrix();
        int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);

        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            matrixStackIn.translate(0, entityIn.getHeight() / 2, 0);
            matrixStackIn.scale(1.45f, 1.45f, 1.45f);
            matrixStackIn.push();
            matrixStackIn.scale(0.25f, 0.25f, 0.25f);
            entityIn.getElement().getRenderer().renderOrb(matrixStackIn, bufferIn, 1.0f, Integer.toString(entityIn.getEntityId()), 0.1f);
            entityIn.getElement().getRenderer().renderOrb(matrixStackIn, bufferIn, 1.0f, Integer.toString(entityIn.getEntityId()), 2f);
            matrixStackIn.pop();
            float c = entityIn.ticksExisted % 11;
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(360f * (c / 10)));
            RenderHelper.renderCylinder(cylinder_rotate, matrixStackIn, bufferIn, 1.0f, entityIn.getElement().getRenderer().getColor()
                    , 1.0f, 0.35f, 32, true, entityIn.getUniqueID().toString(), 0.25f);

            entityIn.getElement().getRenderer().renderSphere(positionMatrix, bufferIn, RenderHelper.getTexedSphereGlow(blank), 6, 0.3f, entityIn.getHitReactions(), 2.10f, packedLightIn);
        }
    }
}
