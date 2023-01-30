package com.rogoshum.magickcore.client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rogoshum.magickcore.common.entity.living.TimeManagerEntity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

public class TimeManagerModel extends EntityModel<TimeManagerEntity> {
    private final ModelPart body;

    public TimeManagerModel() {
        texWidth = 128;
        texHeight = 128;
 
        body = new ModelPart(this);
        this.body.addBox(-4.0F, 16.0F, -4.0F, 8.0F, 8.0F, 8.0F);
    }

    @Override
    public void setupAnim(TimeManagerEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        body.xRot = limbSwing;
        body.yRot = netHeadYaw;
        body.zRot = headPitch;
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        body.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

}
