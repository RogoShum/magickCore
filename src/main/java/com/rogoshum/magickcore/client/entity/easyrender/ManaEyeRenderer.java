package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.ManaEyeEntity;
import com.rogoshum.magickcore.entity.ManaOrbEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class ManaEyeRenderer extends EasyRenderer<ManaEyeEntity>{

    @Override
    public void render(ManaEyeEntity entityIn, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);
        Matrix4f positionMatrix = matrixStackIn.getLast().getMatrix();
        matrixStackIn.scale(entityIn.getWidth() / 2, entityIn.getHeight() * 1.2f, entityIn.getWidth() / 2);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            VectorHitReaction[] test = {};
            entityIn.getElement().getRenderer().renderSphere(positionMatrix, bufferIn, RenderHelper.getTexedSphereGlow(blank, 1f, 0f), 4, 0.6f, test, 0.0f, packedLightIn);
        }
    }
}
