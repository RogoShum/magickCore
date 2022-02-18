package com.rogoshum.magickcore.client.entity.easyrender.bloom;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.BufferPackage;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.entity.ManaSphereEntity;
import com.rogoshum.magickcore.entity.superentity.DawnWardEntity;
import com.rogoshum.magickcore.lib.LibShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class DawnWardBloomRenderer extends EasyRenderer<DawnWardEntity> {
    @Override
    public void render(DawnWardEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        int packedLightIn = RenderHelper.renderLight;
        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            if(entityIn.initial) {
                float scale = Math.min(1f, (float) (entityIn.ticksExisted - 25) / 5f) * entityIn.getWidth();
                scale *= 1.05;
                /*matrixStackIn.scale(scale, scale, scale);
                entityIn.getElement().getRenderer().renderSphere(BufferPackage.create(matrixStackIn, bufferIn,
                        RenderHelper.getTexedSphereGlow(RenderHelper.ripple_5, 3f, 0f)).useShader(LibShaders.slime),
                        16, 1.0f, entityIn.getHitReactions(), 0.3f, packedLightIn);*/
            }
        }
    }
}
