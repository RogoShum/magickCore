package com.rogoshum.magickcore.client.entity.easyrender.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.LayerRenderHelper;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;

public class ManaTakenRenderer extends EasyLayerRender<LivingEntity> {
    public static LayerRenderHelper helper = new LayerRenderHelper(Minecraft.getInstance().getRenderManager(), null);

    @Override
    public void render(LivingEntity entity, LivingRenderer renderer, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, float partialTicks) {
        helper.setEntityModel(renderer.getEntityModel());
        //helper.setAlpha(1f);
        helper.setColor(Color.ORIGIN_COLOR);
        helper.render(entity, renderer, renderer.getEntityTexture(entity), entity.rotationYaw, partialTicks, matrixStackIn, bufferIn, 0);

        helper.setAlpha(.5f);
        helper.setColor(MagickCore.proxy.getElementRender(LibElements.TAKEN).getColor());
        helper.render(entity, renderer, RenderHelper.TAKEN_LAYER, entity.rotationYaw, partialTicks, matrixStackIn, bufferIn, RenderHelper.renderLight);
    }
}
