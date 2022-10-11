package com.rogoshum.magickcore.client.entity.easyrender.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.buff.ManaBuff;
import com.rogoshum.magickcore.client.LayerRenderHelper;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.lib.LibEntityData;
import com.rogoshum.magickcore.magick.Color;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;

public class ManaBuffRenderer extends EasyLayerRender<LivingEntity> {
    public static LayerRenderHelper helper = new LayerRenderHelper(Minecraft.getInstance().getRenderManager(), null);

    @Override
    public void render(LivingEntity entity, LivingRenderer renderer, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, float partialTicks) {
        ExtraDataHelper.entityData(entity).<EntityStateData>execute(LibEntityData.ENTITY_STATE, state -> {
            int packedLightIn = RenderHelper.renderLight;
            Color color = Color.create(0f, 0f, 0f);
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

                if (color.r() > 0.0f || color.g() > 0.0f || color.b() > 0.0f) {
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
        });
    }

    public Color getColorBlender(int time, Color preColor, Color blend)
    {
        float[] newColor = new float[3];
        float scale = 0f;
        if(time > 0)
            scale = (float)time / ((float)time + 1.0f);
        newColor[0] = scale * preColor.r();
        newColor[1] = scale * preColor.g();
        newColor[2] = scale * preColor.b();

        newColor[0] = newColor[0] + (1.0f-scale) * blend.r();
        newColor[1] = newColor[1] + (1.0f-scale) * blend.g();
        newColor[2] = newColor[2] + (1.0f-scale) * blend.b();

        return Color.create(newColor[0], newColor[1], newColor[2]);
    }
}
