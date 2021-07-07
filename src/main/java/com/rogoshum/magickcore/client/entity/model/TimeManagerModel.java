package com.rogoshum.magickcore.client.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.entity.living.TimeManagerEntity;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class TimeManagerModel extends EntityModel<TimeManagerEntity> {
    private final ModelRenderer body;

    public TimeManagerModel() {
        textureWidth = 128;
        textureHeight = 128;
 
        body = new ModelRenderer(this);
        this.body.addBox(-4.0F, 16.0F, -4.0F, 8.0F, 8.0F, 8.0F);
    }

    @Override
    public void setRotationAngles(TimeManagerEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        body.rotateAngleX = limbSwing;
        body.rotateAngleY = netHeadYaw;
        body.rotateAngleZ = headPitch;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        body.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

}
