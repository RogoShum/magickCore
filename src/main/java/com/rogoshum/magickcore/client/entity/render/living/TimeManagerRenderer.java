package com.rogoshum.magickcore.client.entity.render.living;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.entity.living.TimeManagerEntity;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;

public class TimeManagerRenderer extends EntityRenderer<TimeManagerEntity> {
    private EntityModel<TimeManagerEntity> flyingSwordModel;

    public TimeManagerRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getTextureLocation(TimeManagerEntity entity) {
        return new ResourceLocation(MagickCore.MOD_ID, "textures/entity/flying_sword.png");
    }

    @Override
    public void render(TimeManagerEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pushPose();
        Matrix4f positionMatrix = matrixStackIn.last().pose();
        matrixStackIn.translate(0, 0.5, 0);
        matrixStackIn.scale(entityIn.getBbWidth(), entityIn.getBbHeight() / 2, entityIn.getBbWidth());
        //matrixStackIn.rotate(this.renderManager.getCameraOrientation());
        //RenderHelper.renderSphere(positionMatrix, bufferIn.getBuffer(RenderHelper.Orb), 1, 16, 1);
        //Minecraft.getInstance().getRenderManager().textureManager.bindTexture(new ResourceLocation(MagickCore.MOD_ID, "textures/blank.png"));
        //RenderHelper.renderSphere(positionMatrix,  bufferIn.getBuffer(RenderHelper.Orb), 64, 1, packedLightIn, false, Integer.toString(entityIn.getEntityId()));
        matrixStackIn.popPose();
    }
}
