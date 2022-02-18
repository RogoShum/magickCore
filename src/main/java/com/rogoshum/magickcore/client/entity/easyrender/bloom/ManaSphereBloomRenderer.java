package com.rogoshum.magickcore.client.entity.easyrender.bloom;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.BufferPackage;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.entity.ManaSphereEntity;
import com.rogoshum.magickcore.lib.LibShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.util.math.vector.Vector3f;

public class ManaSphereBloomRenderer extends EasyRenderer<ManaSphereEntity> {
    @Override
    public void render(ManaSphereEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            float scale = entityIn.getWidth() * 1.6f;
            if(entityIn.ticksExisted < 9)
                scale *= 1 - 1f / ((float)entityIn.ticksExisted + 1f);

            if(entityIn.getTickTime() - entityIn.ticksExisted <= 9)
                scale *= 1 - 1f / (float)(entityIn.getTickTime() - entityIn.ticksExisted);
            int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);
            scale *= 1.2;
            matrixStackIn.scale(scale, scale, scale);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
            VectorHitReaction[] test = {};
            entityIn.getElement().getRenderer().renderSphere(
                    BufferPackage.create(matrixStackIn, bufferIn, RenderHelper.getTexedSphereGlow(blank, 1f, 0f)).useShader(LibShaders.slime)
                    , 6, 0.4f, entityIn.getHitReactions(), 2.10f, packedLightIn);
        }
    }
}
