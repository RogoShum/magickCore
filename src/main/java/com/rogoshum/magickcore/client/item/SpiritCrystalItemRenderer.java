package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderHelper;
import com.rogoshum.magickcore.magick.Color;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class SpiritCrystalItemRenderer extends ItemStackTileEntityRenderer {

    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.push();
        matrixStackIn.translate(0.5, -0.15, 0.5);
        matrixStackIn.scale(2.5f, 2.5f, 2.5f);
        matrixStackIn.push();
        matrixStackIn.scale(0.2f, 0.07f, 0.2f);
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), RenderHelper.getTexedEntityGlow(RenderHelper.blankTex))
                , new RenderHelper.RenderContext(0.5f, Color.ORIGIN_COLOR, combinedLight));
        matrixStackIn.pop();
        matrixStackIn.translate(0.0, 0.15, 0.0);
        matrixStackIn.push();
        matrixStackIn.translate(0.05, 0.0, 0.0);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-20));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(-20));
        matrixStackIn.push();
        matrixStackIn.scale(0.1f, 0.25f, 0.1f);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(45));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(45));
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), RenderHelper.getTexedEntityGlow(RenderHelper.blankTex))
                , new RenderHelper.RenderContext(0.5f, Color.ORIGIN_COLOR, combinedLight));
        matrixStackIn.pop();
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.translate(-0.05, 0.00, -0.05);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-20));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(20));
        matrixStackIn.push();
        matrixStackIn.scale(0.1f, 0.3f, 0.1f);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(45));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(-45));
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), RenderHelper.getTexedEntityGlow(RenderHelper.blankTex))
                , new RenderHelper.RenderContext(0.5f, Color.ORIGIN_COLOR, combinedLight));
        matrixStackIn.pop();
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.translate(-0.05, 0.00, 0.05);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(20));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(20));
        matrixStackIn.push();
        matrixStackIn.scale(0.1f, 0.2f, 0.1f);
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-45));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(-45));
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), RenderHelper.getTexedEntityGlow(RenderHelper.blankTex))
                , new RenderHelper.RenderContext(0.5f, Color.ORIGIN_COLOR, combinedLight));
        matrixStackIn.pop();
        matrixStackIn.pop();

        matrixStackIn.pop();
    }
}
