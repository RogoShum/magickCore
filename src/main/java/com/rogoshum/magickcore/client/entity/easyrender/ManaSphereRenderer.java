package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.entity.pointed.ManaSphereEntity;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibShaders;
import com.rogoshum.magickcore.magick.context.MagickContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.vector.Vector3f;

public class ManaSphereRenderer extends EasyRenderer<ManaSphereEntity>{

    @Override
    public void render(ManaSphereEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        if(entityIn.spellContext().element != null && entityIn.spellContext().element.getRenderer() != null) {
            //EasyRenderer.renderRift(matrixStackIn, bufferIn, RenderHelper.ORB, entityIn, 3.0f, entityIn.getElement().getRenderer().getColor()
                    //, 1.0f, partialTicks, entityIn.world);
            float scale = entityIn.getWidth() * 1.6f;
            if(entityIn.ticksExisted < 9)
                scale *= 1 - 1f / ((float)entityIn.ticksExisted + 1f);

            if(entityIn.spellContext().tick - entityIn.ticksExisted <= 9)
                scale *= 1 - 1f / (float)(entityIn.spellContext().tick - entityIn.ticksExisted);
            if(entityIn.spellContext().tick <= entityIn.ticksExisted)
                scale = 0;

            int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);
            matrixStackIn.scale(scale, scale, scale);
            entityIn.spellContext().element.getRenderer().renderSphere(
                    BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedSphereGlow(RenderHelper.blankTex, 1.2f, 0f))
                    , 8, 0.6f, entityIn.getHitReactions(), 2.10f, packedLightIn);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
            scale = 0.4f;
            matrixStackIn.scale(scale, scale, scale);
            ModElements.ORIGIN.getRenderer().renderSphere(
                    BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedSphereGlow(RenderHelper.blankTex, 1.2f, 0f))
                    , 8, 0.6f, entityIn.getHitReactions(), 2.10f, packedLightIn);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
        }
    }
}
