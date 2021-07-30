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
        //helper.setAlpha(1f);
        helper.setColor(RenderHelper.ORIGIN);
        helper.render(entity, renderer, renderer.getEntityTexture(entity), entity.rotationYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

        matrixStackIn.translate(0, -0.05f * entity.getHeight(), 0);
        matrixStackIn.scale(1.1f, 1.1f, 1.1f);

        helper.setAlpha(1f);
        helper.setColor(MagickCore.proxy.getElementRender(LibElements.STASIS).getColor());
        helper.render(entity, renderer, RenderHelper.RES_ITEM_GLINT, entity.rotationYaw, partialTicks, matrixStackIn, bufferIn, RenderHelper.renderLight);
    }
}
