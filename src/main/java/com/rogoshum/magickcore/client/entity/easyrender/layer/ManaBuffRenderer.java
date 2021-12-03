package com.rogoshum.magickcore.client.entity.easyrender.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.buff.ManaBuff;
import com.rogoshum.magickcore.capability.IElementAnimalState;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.client.LayerRenderHelper;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;
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
            helper.setEntityModel(renderer.getEntityModel());

            matrixStackIn.translate(0, -0.005f * entity.getHeight(), 0);
            matrixStackIn.scale(1.01f, 1.01f, 1.01f);

            for(ManaBuff buff : state.getBuffList().values())
            {
                if(!buff.isBeneficial()) {
                    color = getColorBlender(time++, color, MagickCore.proxy.getElementRender(buff.getElement()).getColor());
                }
            }

            if (color[0] > 0.0f || color[1] > 0.0f || color[2] > 0.0f) {
                helper.setColor(color);
                helper.setAlpha(0.5f);
                helper.render(entity, renderer, RenderHelper.RES_ITEM_GLINT, entity.rotationYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            }

            for(ManaBuff buff : state.getBuffList().values())
            {
                if(buff.isBeneficial()) {
                    matrixStackIn.translate(0, -0.005f * entity.getHeight(), 0);
                    matrixStackIn.scale(1.01f, 1.01f, 1.01f);
                    //matrixStackIn.translate(0, -0.02f, 0);
                    helper.setColor(getColorBlender(time++, color, MagickCore.proxy.getElementRender(buff.getElement()).getColor()));
                    helper.setAlpha(1.0f);
                    //helper.render(entity, renderer, RenderHelper.ripple_4, entity.rotationYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
                    helper.render(entity, renderer, RenderHelper.ripple_2, entity.rotationYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
                }
            }
        }
    }

    public float[] getColorBlender(int time, float[] preColor, float[] blend)
    {
        float[] newColor = new float[3];
        float scale = 0f;
        if(time > 0)
            scale = (float)time / ((float)time + 1.0f);
        newColor[0] = scale * preColor[0];
        newColor[1] = scale * preColor[1];
        newColor[2] = scale * preColor[2];

        newColor[0] = newColor[0] + (1.0f-scale) * blend[0];
        newColor[1] = newColor[1] + (1.0f-scale) * blend[1];
        newColor[2] = newColor[2] + (1.0f-scale) * blend[2];

        return newColor;
    }
}
