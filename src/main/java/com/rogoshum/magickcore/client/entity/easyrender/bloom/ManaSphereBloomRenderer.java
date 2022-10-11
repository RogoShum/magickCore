package com.rogoshum.magickcore.client.entity.easyrender.bloom;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.entity.pointed.ManaSphereEntity;
import net.minecraft.client.renderer.*;

public class ManaSphereBloomRenderer extends EasyRenderer<ManaSphereEntity> {
    @Override
    public void render(ManaSphereEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        /*
        if(entityIn.spellContext().element != null && entityIn.spellContext().element.getRenderer() != null) {
            float scale = entityIn.getWidth() * 1.6f;
            if(entityIn.ticksExisted < 9)
                scale *= 1 - 1f / ((float)entityIn.ticksExisted + 1f);

            if(entityIn.spellContext().tick - entityIn.ticksExisted <= 9)
                scale *= 1 - 1f / (float)(entityIn.spellContext().tick - entityIn.ticksExisted);
            int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);
            //scale *= 1.2;
            matrixStackIn.scale(scale, scale, scale);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
            VectorHitReaction[] test = {};
            entityIn.spellContext().element.getRenderer().renderSphere(
                    BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedSphereGlow(blank, 1f, 0f)).useShader(LibShaders.slime)
                    , 6, 0.7f, entityIn.getHitReactions(), 2.10f, packedLightIn);
        }

         */
    }
}
