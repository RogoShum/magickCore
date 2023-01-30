package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class MaterialJarItemRenderer extends EasyItemRenderer {

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.1901, 0.5);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));
        matrixStackIn.pushPose();
        matrixStackIn.scale(0.3f, 0.42f, 0.3f);
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RenderHelper.getTexedOrb(RenderHelper.blankTex))
                , new RenderHelper.RenderContext(0.2f, Color.ORIGIN_COLOR, combinedLight));
        matrixStackIn.scale(0.9f, 0.9f, 0.9f);
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RenderHelper.getTexedOrb(RenderHelper.blankTex))
                , new RenderHelper.RenderContext(0.05f, Color.ORIGIN_COLOR, combinedLight));
        matrixStackIn.popPose();

        if(!stack.hasTag()) {
            matrixStackIn.popPose();
            return;
        }
        CompoundTag blockTag = NBTTagHelper.getBlockTag(stack.getTag());
        if(!blockTag.contains("stack")) {
            matrixStackIn.popPose();
            return;
        }
        ItemStack stack1 = ItemStack.of(blockTag.getCompound("stack"));
        if(!stack1.isEmpty()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0, 0.15, 0);
            matrixStackIn.scale(0.01f, 0.01f, .01f);
            matrixStackIn.mulPose(Vector3f.ZN.rotationDegrees(180));
            String count = String.valueOf(blockTag.getInt("count"));
            Minecraft.getInstance().font.draw(matrixStackIn, count, -count.length()*3, 2, 0);
            matrixStackIn.popPose();
            matrixStackIn.translate(0, -0.12f, 0);
            matrixStackIn.scale(0.5f, 0.5f, .5f);
            BakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getModel(stack1, null, null);
            MultiBufferSource.BufferSource renderTypeBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            Minecraft.getInstance().getItemRenderer().render(stack1, transformType, false, matrixStackIn, renderTypeBuffer, combinedLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
            renderTypeBuffer.endBatch();
        }

        matrixStackIn.popPose();
    }
}
