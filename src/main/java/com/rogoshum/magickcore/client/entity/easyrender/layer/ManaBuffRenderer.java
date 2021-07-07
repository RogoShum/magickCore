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
import net.minecraft.util.ResourceLocation;

public class ManaBuffRenderer extends EasyLayerRender<LivingEntity> {
    public static LayerRenderHelper helper = new LayerRenderHelper(Minecraft.getInstance().getRenderManager(), null);

    @Override
    public void render(LivingEntity entity, LivingRenderer renderer, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, float partialTicks) {
        IEntityState state = entity.getCapability(MagickCore.entityState, null).orElse(null);
        int packedLightIn = RenderHelper.renderLight;
        float[] defaultColor = {0f, 0f, 0f};
        float[] color = defaultColor;
        int time = 0;

        if(state != null && !state.getBuffList().isEmpty())
        {
            matrixStackIn.translate(0, -0.01f, 0);
            matrixStackIn.scale(1.01f, 1.02f, 1.01f);

            if(state.getBuffList().containsKey(LibBuff.PARALYSIS))
                color = getColorBlender(time++, color, MagickCore.proxy.getElementRender(LibElements.ARC).getColor());

            if(state.getBuffList().containsKey(LibBuff.WITHER))
                color = getColorBlender(time++, color, MagickCore.proxy.getElementRender(LibElements.WITHER).getColor());

            if(state.getBuffList().containsKey(LibBuff.FREEZE))
                color = getColorBlender(time++, color, MagickCore.proxy.getElementRender(LibElements.STASIS).getColor());

            if(state.getBuffList().containsKey(LibBuff.SLOW))
                color = getColorBlender(time++, color, MagickCore.proxy.getElementRender(LibElements.STASIS).getColor());

            if(state.getBuffList().containsKey(LibBuff.CRIPPLE))
                color = getColorBlender(time++, color, MagickCore.proxy.getElementRender(LibElements.WITHER).getColor());

            if(state.getBuffList().containsKey(LibBuff.FRAGILE) || state.getBuffList().containsKey(LibBuff.WEAKEN))
                color = getColorBlender(time++, color, MagickCore.proxy.getElementRender(LibElements.VOID).getColor());

            if(state.getBuffList().containsKey(LibBuff.TAKEN))
                color = getColorBlender(time++, color, MagickCore.proxy.getElementRender(LibElements.TAKEN).getColor());

            helper.setEntityModel(renderer.getEntityModel());
            //helper.preRender(entity, matrixStackIn, partialTicks);
            if (color[0] > 0.0f || color[1] > 0.0f || color[2] > 0.0f) {
                helper.setColor(color);
                helper.setAlpha(0.5f);
                helper.render(entity, renderer, RenderHelper.RES_ITEM_GLINT, entity.rotationYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            }
            if(state.getBuffList().containsKey(LibBuff.STASIS)) {
                color = MagickCore.proxy.getElementRender(LibElements.STASIS).getColor();
                matrixStackIn.translate(0, -0.005f, 0);
                matrixStackIn.scale(1.001f, 1.01f, 1.001f);
                //matrixStackIn.translate(0, -0.02f, 0);
                helper.setColor(color);
                helper.setAlpha(0.6f);
                helper.render(entity, renderer, RenderHelper.RES_ITEM_GLINT, entity.rotationYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            }

            if(state.getBuffList().containsKey(LibBuff.LIGHT)) {
                color = MagickCore.proxy.getElementRender(LibElements.VOID).getColor();
                matrixStackIn.translate(0, -0.005f, 0);
                matrixStackIn.scale(1.001f, 1.01f, 1.001f);
                //matrixStackIn.translate(0, -0.01f, 0);
                helper.setColor(color);
                helper.setAlpha(0.6f);
                helper.render(entity, renderer, RenderHelper.RES_ITEM_GLINT, entity.rotationYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            }

            if(state.getBuffList().containsKey(LibBuff.RADIANCE_WELL)) {
                matrixStackIn.translate(0, -0.005f, 0);
                matrixStackIn.scale(1.001f, 1.01f, 1.001f);
                //matrixStackIn.translate(0, -0.01f, 0);
                float[] gold = {1f, 0.6f, 0};
                helper.setColor(gold);
                helper.setAlpha(0.5f);
                helper.render(entity, renderer, RenderHelper.RES_ITEM_GLINT, entity.rotationYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            }

            if(state.getBuffList().containsKey(LibBuff.HYPERMUTEKI)) {
                matrixStackIn.translate(0, -0.005f, 0);
                matrixStackIn.scale(1.001f, 1.01f, 1.001f);
                //matrixStackIn.translate(0, -0.01f, 0);
                float[] gold = {1f, 0.8f, 0};
                helper.setColor(gold);
                helper.setAlpha(0.7f);
                helper.render(entity, renderer, RenderHelper.RES_ITEM_GLINT, entity.rotationYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            }


        }
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
