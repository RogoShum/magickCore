package com.rogoshum.magickcore.client.tileentity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.block.tileentity.MagickContainerTileEntity;
import com.rogoshum.magickcore.client.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.magick.Color;
import com.rogoshum.magickcore.magick.MagickElement;
import com.rogoshum.magickcore.registry.MagickRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;

public class MagickContainerRenderer extends EasyTileRenderer<MagickContainerTileEntity>{
    @Override
    public void render(MagickContainerTileEntity tileEntityIn, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        Color color = Color.ORIGIN_COLOR;
        matrixStackIn.push();
        matrixStackIn.push();
        //matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
        MagickElement element = MagickRegistry.getElement(tileEntityIn.eType);
        if(element != null)
            color = element.getRenderer().getColor();
        matrixStackIn.push();
        float scale = (float) tileEntityIn.manaCapacity().getMana() / (float)tileEntityIn.manaCapacity().getMaxMana();
        matrixStackIn.scale(scale, scale, scale);
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        RenderHelper.renderSphere(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedSphereGlow(blank, 0.32f, 0f)), 4, 0.3f, color, RenderHelper.renderLight);
        matrixStackIn.pop();

        RenderHelper.renderSphere(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedSphereGlow(cylinder_rotate, 0.32f, 0f)), 4, 1.0f, Color.ORIGIN_COLOR, RenderHelper.renderLight);
        matrixStackIn.pop();
        matrixStackIn.scale(0.6f, 0.6f, 0.6f);
        //RenderHelper.renderParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrbGlow(orbTex)), 0.5f, color);
        if(tileEntityIn.getMaterialItem() != null) {
            matrixStackIn.scale(0.8f, 0.8f, 0.8f);
            IBakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(tileEntityIn.getMaterialItem(), tileEntityIn.getWorld(), (LivingEntity) null);
            Minecraft.getInstance().getItemRenderer().renderItem(tileEntityIn.getMaterialItem(), ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, ibakedmodel);
        }
        matrixStackIn.pop();
    }
}
