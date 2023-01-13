package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.item.tool.SpiritCrystalStaffItem;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class StaffRenderer extends ItemStackTileEntityRenderer {
    protected static final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    private static final ResourceLocation wind = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/wind.png");
    protected static final ResourceLocation QUARTZ = new ResourceLocation( "minecraft:textures/block/quartz_block_top.png");
    private static final RenderType RENDER_TYPE_0 = RenderType.entityTranslucent(new ResourceLocation( "minecraft:textures/block/oak_log.png"));
    private static final RenderType RENDER_TYPE_1 = RenderType.entityTranslucent(new ResourceLocation( "minecraft:textures/block/quartz_block_top.png"));

    @Override
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLight, int combinedOverlay) {
        matrixStack.pushPose();
        matrixStack.translate(0.55, 0.55, 0.5);
        matrixStack.scale(0.65f, 0.65f, 0.65f);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(135));
        matrixStack.pushPose();
        matrixStack.translate(0, 0.5, 0);
        matrixStack.scale(0.1f, 1.2f, 0.1f);
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStack, Tessellator.getInstance().getBuilder(), RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.9f, Color.ORIGIN_COLOR, combinedLight));
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.translate(0, -0.1, 0);
        matrixStack.scale(0.2f, 0.1f, 0.2f);
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStack, Tessellator.getInstance().getBuilder(), RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.9f, Color.ORIGIN_COLOR, combinedLight));
        matrixStack.popPose();

        matrixStack.translate(0, -0.3, 0);

        float c = MagickCore.proxy.getRunTick() % 100;
        float angle = 360f * (c / 99);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(angle));

        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        if(data.contextCore().haveMagickContext()) {
            matrixStack.pushPose();
            matrixStack.scale(0.4f, 0.4f, 0.4f);
            ItemStack core = new ItemStack(ModItems.MAGICK_CORE.get());
            ExtraDataUtil.itemManaData(core).spellContext().copy(data.spellContext());
            IBakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getModel(core, null, null);
            Minecraft.getInstance().getItemRenderer().render(core, ItemCameraTransforms.TransformType.GUI, false, matrixStack, bufferIn, combinedLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
            matrixStack.popPose();
        }

        if(stack.getItem() instanceof SpiritCrystalStaffItem) {
            for (int i = 0; i < RenderHelper.vertex_list.length; ++i) {
                float[] vertex = RenderHelper.vertex_list[i];
                matrixStack.pushPose();
                matrixStack.translate(vertex[0] * 0.3, vertex[1] * 0.3, vertex[2] * 0.3);
                matrixStack.scale(0.1f, 0.14f, 0.1f);
                matrixStack.mulPose(Vector3f.YN.rotationDegrees(angle));
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(45));
                matrixStack.mulPose(Vector3f.ZN.rotationDegrees(135));
                RenderHelper.renderCubeDynamic(BufferContext.create(matrixStack, Tessellator.getInstance().getBuilder(), RENDER_TYPE_1)
                        , new RenderHelper.RenderContext(0.9f, Color.ORIGIN_COLOR, combinedLight));
                matrixStack.popPose();
            }
        }
        matrixStack.popPose();
    }
}
