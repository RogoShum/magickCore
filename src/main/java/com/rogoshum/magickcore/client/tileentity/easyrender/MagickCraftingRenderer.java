package com.rogoshum.magickcore.client.tileentity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.block.tileentity.MagickCraftingTileEntity;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.init.ModElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class MagickCraftingRenderer extends EasyTileRenderer<MagickCraftingTileEntity>{
    @Override
    public void render(MagickCraftingTileEntity tileEntityIn, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        matrixStackIn.push();
        float length = 0.7f;
        double offset = 0.3;
        double offset1 = offset * 2;
        float degree = 37.5f;
        matrixStackIn.translate(offset, 0.0, offset);
        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(45f));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-degree));
        matrixStackIn.scale(0.1f, length, 0.1f);
        RenderHelper.renderSphere(matrixStackIn.getLast().getMatrix(), bufferIn, RenderHelper.getTexedSphereGlow(blank), 4, 0.5f, RenderHelper.ORIGIN, RenderHelper.renderLight);
        matrixStackIn.pop();
        matrixStackIn.translate(-offset1, 0.0, -offset1);

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(45f));
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(degree));
        matrixStackIn.scale(0.1f, length, 0.1f);
        RenderHelper.renderSphere(matrixStackIn.getLast().getMatrix(), bufferIn, RenderHelper.getTexedSphereGlow(blank), 4, 0.5f, RenderHelper.ORIGIN, RenderHelper.renderLight);
        matrixStackIn.pop();
        matrixStackIn.translate(0.0, 0.0, offset1);

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(45f));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(-degree));
        matrixStackIn.scale(0.1f, length, 0.1f);
        RenderHelper.renderSphere(matrixStackIn.getLast().getMatrix(), bufferIn, RenderHelper.getTexedSphereGlow(blank), 4, 0.5f, RenderHelper.ORIGIN, RenderHelper.renderLight);
        matrixStackIn.pop();
        matrixStackIn.translate(offset1, 0.0, -offset1);

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(45f));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(degree));
        matrixStackIn.scale(0.1f, length, 0.1f);
        RenderHelper.renderSphere(matrixStackIn.getLast().getMatrix(), bufferIn, RenderHelper.getTexedSphereGlow(blank), 4, 0.5f, RenderHelper.ORIGIN, RenderHelper.renderLight);
        matrixStackIn.pop();
        matrixStackIn.pop();
        float height = (float) (tileEntityIn.ticksExisted % 120) / 60f;
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(height * 360f));
        if(height > 1.0f) {
            height = 2.0f - height;
        }
        height = (float) Math.pow(height, 1.5f);

        matrixStackIn.translate(0, 0.2f + height * 0.2f, 0);
        matrixStackIn.scale(0.6f, 0.6f, 0.6f);
        if(tileEntityIn.getMainItem() != null) {
            IBakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(tileEntityIn.getMainItem(), tileEntityIn.getWorld(), (LivingEntity) null);
            Minecraft.getInstance().getItemRenderer().renderItem(tileEntityIn.getMainItem(), ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, ibakedmodel);
        }
    }
}
