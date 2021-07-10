package com.rogoshum.magickcore.client.entity.easyrender.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.client.LayerRenderHelper;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;

public class ManaFreezeRenderer extends EasyLayerRender<LivingEntity> {
    public static LayerRenderHelper helper = new LayerRenderHelper(Minecraft.getInstance().getRenderManager(), null);

    @Override
    public void render(LivingEntity entity, LivingRenderer renderer, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, float partialTicks) {
        int packedLightIn = RenderHelper.renderLight;

        helper.setEntityModel(renderer.getEntityModel());
        helper.setAlpha(1f);
        helper.setColor(RenderHelper.ORIGIN);
        helper.render(entity, renderer, RenderHelper.blankTex, entity.rotationYaw, partialTicks, matrixStackIn, bufferIn, Minecraft.getInstance().getRenderManager().getPackedLight(entity, partialTicks));

        matrixStackIn.translate(0, -0.01f, 0);
        matrixStackIn.scale(1.01f, 1.02f, 1.01f);

        helper.setColor(MagickCore.proxy.getElementRender(LibElements.STASIS).getColor());
        helper.setAlpha(0.5f);
        helper.render(entity, renderer, RenderHelper.RES_ITEM_GLINT, entity.rotationYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    public float[] getColorBlender(int time, float[] preColor, float[] blend)
    {
        float[] newColor = new float[3];
        float scale = 0f;
        if(time > 0)
            scale = (float)time / (float)time + 1.0f;
        newColor[0] = scale * preColor[0];
        newColor[1] = scale * preColor[1];
        newColor[2] = scale * preColor[2];

        newColor[0] = newColor[0] + (1.0f-scale) * blend[0];
        newColor[1] = newColor[1] + (1.0f-scale) * blend[1];
        newColor[2] = newColor[2] + (1.0f-scale) * blend[2];

        return newColor;
    }
}
