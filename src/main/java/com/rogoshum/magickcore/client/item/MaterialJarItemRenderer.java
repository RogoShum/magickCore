package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderHelper;
import com.rogoshum.magickcore.magick.Color;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3f;

public class MaterialJarItemRenderer extends ItemStackTileEntityRenderer {

    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.push();
        matrixStackIn.translate(0.5, 0.1901, 0.5);

        matrixStackIn.push();
        matrixStackIn.scale(0.3f, 0.38f, 0.3f);
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), RenderHelper.getTexedEntity(RenderHelper.blankTex))
                , new RenderHelper.RenderContext(0.2f, Color.ORIGIN_COLOR, combinedLight));
        matrixStackIn.scale(0.9f, 0.9f, 0.9f);
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), RenderHelper.getTexedEntity(RenderHelper.blankTex))
                , new RenderHelper.RenderContext(0.05f, Color.ORIGIN_COLOR, RenderHelper.halfLight));
        matrixStackIn.pop();

        if(!stack.hasTag()) {
            matrixStackIn.pop();
            return;
        }
        CompoundNBT blockTag = NBTTagHelper.getBlockTag(stack.getTag());
        if(!blockTag.contains("stack")) {
            matrixStackIn.pop();
            return;
        }
        ItemStack stack1 = ItemStack.read(blockTag.getCompound("stack"));
        if(!stack1.isEmpty()) {
            matrixStackIn.push();
            matrixStackIn.translate(0, 0.15, 0);
            matrixStackIn.scale(0.01f, 0.01f, .01f);
            matrixStackIn.rotate(Vector3f.ZN.rotationDegrees(180));
            String count = String.valueOf(blockTag.getInt("count"));
            Minecraft.getInstance().fontRenderer.drawString(matrixStackIn, count, -count.length()*3, 2, 0);
            matrixStackIn.pop();
            matrixStackIn.translate(0, -0.12f, 0);
            matrixStackIn.scale(0.5f, 0.5f,0.5f);
            IBakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack1, null, null);
            IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
            Minecraft.getInstance().getItemRenderer().renderItem(stack1, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, renderTypeBuffer, combinedLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
            renderTypeBuffer.finish();
        }

        matrixStackIn.pop();
    }
}
