package com.rogoshum.magickcore.client.entity.easyrender.bloom;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.entity.superentity.DawnWardEntity;
import net.minecraft.client.renderer.BufferBuilder;

public class DawnWardBloomRenderer extends EasyRenderer<DawnWardEntity> {
    @Override
    public void render(DawnWardEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        /*
        int packedLightIn = RenderHelper.renderLight;
        if(entityIn.manaData().getElement() != null && entityIn.manaData().getElement().getRenderer() != null) {
            if(entityIn.initial) {
                float scale = Math.min(1f, (float) (entityIn.ticksExisted - 25) / 5f) * entityIn.getWidth();
                scale *= 1.05;
                matrixStackIn.scale(scale, scale, scale);
                entityIn.manaData().getRenderer().renderSphere(BufferPackage.create(matrixStackIn, bufferIn,
                        RenderHelper.getTexedSphereGlow(RenderHelper.ripple_5, 3f, 0f)).useShader(LibShaders.slime),
                        16, 1.0f, entityIn.getHitReactions(), 0.3f, packedLightIn);
            }
        }

         */
    }
}
