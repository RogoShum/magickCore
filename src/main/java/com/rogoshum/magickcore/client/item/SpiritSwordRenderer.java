package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
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

public class SpiritSwordRenderer extends BlockEntityWithoutLevelRenderer {
    protected static final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    protected static final ResourceLocation QUARTZ = new ResourceLocation( "minecraft:textures/block/quartz_block_top.png");
    private static final RenderType RENDER_TYPE_0 = RenderType.entityTranslucent(new ResourceLocation( "minecraft:textures/block/oak_log.png"));
    private static final RenderType RENDER_TYPE_1 = RenderType.entityTranslucent(new ResourceLocation( "minecraft:textures/block/quartz_block_top.png"));

    public SpiritSwordRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType p_239207_2_, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay) {
        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.scale(0.65f, 0.65f, 0.65f);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(135));
        matrixStack.pushPose();
        matrixStack.translate(0, 0.7, 0);
        matrixStack.scale(0.08f, 0.6f, 0.08f);
        RenderHelper.renderCubeCache(BufferContext.create(matrixStack, Tesselator.getInstance().getBuilder(), RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.9f, Color.ORIGIN_COLOR, combinedLight));
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.translate(0, 0.3, 0);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(45));
        matrixStack.mulPose(Vector3f.ZN.rotationDegrees(135));
        matrixStack.scale(0.25f, 0.25f, 0.25f);
        RenderHelper.renderCubeCache(BufferContext.create(matrixStack, Tesselator.getInstance().getBuilder(), RenderHelper.getTexturedQuadsGlow(QUARTZ))
                , new RenderHelper.RenderContext(1.0f, Color.ORIGIN_COLOR, combinedLight));
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.translate(0.3, 0.3, 0);
        matrixStack.scale(0.3f, 0.15f, 0.1f);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(45));
        matrixStack.mulPose(Vector3f.ZN.rotationDegrees(135));
        RenderHelper.renderCubeCache(BufferContext.create(matrixStack, Tesselator.getInstance().getBuilder(), RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.9f, Color.ORIGIN_COLOR, combinedLight));
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.translate(-0.3, 0.3, 0);
        matrixStack.scale(0.3f, 0.15f, 0.1f);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(45));
        matrixStack.mulPose(Vector3f.ZN.rotationDegrees(135));
        RenderHelper.renderCubeCache(BufferContext.create(matrixStack, Tesselator.getInstance().getBuilder(), RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.9f, Color.ORIGIN_COLOR, combinedLight));
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.translate(0, -0.2, 0);
        matrixStack.scale(0.15f, 1.0f, 0.1f);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(45));
        matrixStack.mulPose(Vector3f.ZN.rotationDegrees(135));
        RenderHelper.renderCubeCache(BufferContext.create(matrixStack, Tesselator.getInstance().getBuilder(), RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.9f, Color.ORIGIN_COLOR, combinedLight));
        matrixStack.popPose();

        ItemManaData data = ExtraDataUtil.itemManaData(stack, 2);
        if(data.contextCore().haveMagickContext()) {
            matrixStack.pushPose();
            matrixStack.translate(0, 0.3, 0);
            matrixStack.scale(0.35f, 0.35f, 0.35f);
            ItemStack core = new ItemStack(ModItems.MAGICK_CORE.get());
            ExtraDataUtil.itemManaData(core).spellContext().copy(data.spellContext());
            BakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getModel(core, null, null, 0);
            Minecraft.getInstance().getItemRenderer().render(core, ItemTransforms.TransformType.GUI, false, matrixStack, bufferIn, combinedLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
            matrixStack.popPose();
        }
        matrixStack.popPose();
    }
}
