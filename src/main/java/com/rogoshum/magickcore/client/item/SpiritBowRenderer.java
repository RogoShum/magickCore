package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;

public class SpiritBowRenderer extends BlockEntityWithoutLevelRenderer {
    private static final RenderType RENDER_TYPE_0 = RenderType.entityTranslucent(new ResourceLocation( "minecraft:textures/block/oak_log.png"));
    private static final RenderType RENDER_TYPE_1 = RenderType.entityTranslucent(new ResourceLocation( "minecraft:textures/block/quartz_block_top.png"));

    public SpiritBowRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType p_239207_2_, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay) {
        matrixStack.pushPose();
        matrixStack.translate(0.4, 0.6, 0.5);
        matrixStack.scale(0.65f, 0.65f, 0.65f);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(135));

        matrixStack.pushPose();
        matrixStack.translate(-0.15, 0.9, 0);
        VertexConsumer builder = bufferIn.getBuffer(RenderType.lines());
        builder.vertex(matrixStack.last().pose(), 0, 0, 0).color(1, 1, 1, 0.5f).normal(0, 0, 0).endVertex();
        matrixStack.translate(0, -1.8, 0);
        builder.vertex(matrixStack.last().pose(), 0, 0, 0).color(1, 1, 1, 0.5f).normal(0, 0, 0).endVertex();
        matrixStack.popPose();

        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        if(data.contextCore().haveMagickContext()) {
            matrixStack.pushPose();
            matrixStack.translate(0.15, 0, 0);
            matrixStack.scale(0.3f, 0.3f, 0.3f);
            ItemStack core = new ItemStack(ModItems.MAGICK_CORE.get());
            ExtraDataUtil.itemManaData(core).spellContext().copy(data.spellContext());
            BakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getModel(core, null, null, 0);
            Minecraft.getInstance().getItemRenderer().render(core, ItemTransforms.TransformType.GUI, false, matrixStack, bufferIn, combinedLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
            matrixStack.popPose();
        }
        matrixStack.pushPose();
        matrixStack.translate(0, -0.6, 0);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-25));
        matrixStack.scale(0.1f, 0.6f, 0.1f);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(45));
        matrixStack.mulPose(Vector3f.ZN.rotationDegrees(135));
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStack, Tesselator.getInstance().getBuilder(), RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.9f, Color.ORIGIN_COLOR,  combinedLight));
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.translate(0, 0.6, 0);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(25));
        matrixStack.scale(0.1f, 0.6f, 0.1f);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(45));
        matrixStack.mulPose(Vector3f.ZN.rotationDegrees(135));
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStack, Tesselator.getInstance().getBuilder(), RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.9f, Color.ORIGIN_COLOR, combinedLight));
        matrixStack.popPose();

        matrixStack.popPose();
    }
}
