package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class SpiritBowRenderer extends ItemStackTileEntityRenderer {
    private static final RenderType RENDER_TYPE_0 = RenderType.entityTranslucent(new ResourceLocation( "minecraft:textures/block/oak_log.png"));
    private static final RenderType RENDER_TYPE_1 = RenderType.entityTranslucent(new ResourceLocation( "minecraft:textures/block/quartz_block_top.png"));

    @Override
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLight, int combinedOverlay) {
        matrixStack.pushPose();
        matrixStack.translate(0.4, 0.6, 0.5);
        matrixStack.scale(0.65f, 0.65f, 0.65f);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(135));

        matrixStack.pushPose();
        matrixStack.translate(-0.15, 0.9, 0);
        IVertexBuilder builder = bufferIn.getBuffer(RenderType.lines());
        builder.vertex(matrixStack.last().pose(), 0, 0, 0).color(1, 1, 1, 0.5f).endVertex();
        matrixStack.translate(0, -1.8, 0);
        builder.vertex(matrixStack.last().pose(), 0, 0, 0).color(1, 1, 1, 0.5f).endVertex();
        matrixStack.popPose();

        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        if(data.contextCore().haveMagickContext()) {
            matrixStack.pushPose();
            matrixStack.translate(0.15, 0, 0);
            matrixStack.scale(0.3f, 0.3f, 0.3f);
            ItemStack core = new ItemStack(ModItems.MAGICK_CORE.get());
            ExtraDataUtil.itemManaData(core).spellContext().copy(data.spellContext());
            IBakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getModel(core, null, null);
            Minecraft.getInstance().getItemRenderer().render(core, ItemCameraTransforms.TransformType.GUI, false, matrixStack, bufferIn, combinedLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
            matrixStack.popPose();
        }
        matrixStack.pushPose();
        matrixStack.translate(0, -0.6, 0);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-25));
        matrixStack.scale(0.1f, 0.6f, 0.1f);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(45));
        matrixStack.mulPose(Vector3f.ZN.rotationDegrees(135));
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStack, Tessellator.getInstance().getBuilder(), RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.9f, Color.ORIGIN_COLOR,  combinedLight));
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.translate(0, 0.6, 0);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(25));
        matrixStack.scale(0.1f, 0.6f, 0.1f);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(45));
        matrixStack.mulPose(Vector3f.ZN.rotationDegrees(135));
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStack, Tessellator.getInstance().getBuilder(), RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.9f, Color.ORIGIN_COLOR, combinedLight));
        matrixStack.popPose();

        matrixStack.popPose();
    }
}
