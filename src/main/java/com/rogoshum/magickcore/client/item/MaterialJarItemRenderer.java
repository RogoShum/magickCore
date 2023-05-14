package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MaterialJarItemRenderer extends BlockEntityWithoutLevelRenderer {

    public MaterialJarItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.1901, 0.5);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));

        if(stack.hasTag()) {
            CompoundTag blockTag = NBTTagHelper.getBlockTag(stack.getTag());
            if(blockTag.contains("stack")) {
                ItemStack stack1 = ItemStack.of(blockTag.getCompound("stack"));
                if(!stack1.isEmpty()) {
                    matrixStackIn.pushPose();
                    matrixStackIn.pushPose();
                    if(transformType == ItemTransforms.TransformType.GUI)
                        matrixStackIn.translate(0, 0.15, 0);
                    else
                        matrixStackIn.translate(0, 0.4, 0);
                    if(transformType == ItemTransforms.TransformType.GUI)
                        matrixStackIn.scale(0.01f, 0.01f, .01f);
                    else
                        matrixStackIn.scale(0.02f, 0.02f, .02f);
                    matrixStackIn.mulPose(Vector3f.ZN.rotationDegrees(180));
                    String count = String.valueOf(blockTag.getInt("count"));
                    Minecraft.getInstance().font.draw(matrixStackIn, count, -count.length()*3, 2, 0);
                    matrixStackIn.popPose();
                    matrixStackIn.translate(0, -0.12f, 0);
                    if(transformType == ItemTransforms.TransformType.GUI)
                        matrixStackIn.scale(0.5f, 0.5f, .5f);
                    BakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getModel(stack1, null, null, 0);
                    MultiBufferSource.BufferSource renderTypeBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                    Minecraft.getInstance().getItemRenderer().render(stack1, ItemTransforms.TransformType.GROUND, false, matrixStackIn, renderTypeBuffer, combinedLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
                    renderTypeBuffer.endBatch();
                    matrixStackIn.popPose();
                }
            }
        }

        matrixStackIn.pushPose();
        if(transformType == ItemTransforms.TransformType.GUI || transformType == ItemTransforms.TransformType.GROUND)
            matrixStackIn.scale(0.3f, 0.42f, 0.3f);
        else {
            matrixStackIn.translate(0.0, 0.25, 0.0);
            matrixStackIn.scale(1.0f, 1.4f, 1.0f);
        }
        RenderHelper.renderCubeCache(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RenderHelper.getTexturedShaderItemTranslucent(RenderHelper.BLANK_TEX))
                , new RenderHelper.RenderContext(0.2f, Color.ORIGIN_COLOR, combinedLight));
        matrixStackIn.scale(0.9f, 0.9f, 0.9f);
        RenderHelper.renderCubeCache(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RenderHelper.getTexturedShaderItemTranslucent(RenderHelper.BLANK_TEX))
                , new RenderHelper.RenderContext(0.05f, Color.ORIGIN_COLOR, combinedLight));
        matrixStackIn.popPose();


        matrixStackIn.popPose();
    }
}
