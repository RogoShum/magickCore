package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.magick.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.util.ExtraDataUtil;
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

public class SpiritSwordRenderer extends ItemStackTileEntityRenderer {
    protected static final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    protected static final ResourceLocation QUARTZ = new ResourceLocation( "minecraft:textures/block/quartz_block_top.png");
    private static final RenderType RENDER_TYPE_0 = RenderType.getEntityTranslucent(new ResourceLocation( "minecraft:textures/block/oak_log.png"));
    private static final RenderType RENDER_TYPE_1 = RenderType.getEntityTranslucent(new ResourceLocation( "minecraft:textures/block/quartz_block_top.png"));

    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLight, int combinedOverlay) {
        matrixStack.push();
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.scale(0.65f, 0.65f, 0.65f);
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(135));
        matrixStack.push();
        matrixStack.translate(0, 0.7, 0);
        matrixStack.scale(0.08f, 0.6f, 0.08f);
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStack, Tessellator.getInstance().getBuffer(), RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.9f, Color.ORIGIN_COLOR, combinedLight));
        matrixStack.pop();

        matrixStack.push();
        matrixStack.translate(0, 0.3, 0);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(45));
        matrixStack.rotate(Vector3f.ZN.rotationDegrees(135));
        matrixStack.scale(0.25f, 0.25f, 0.25f);
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStack, Tessellator.getInstance().getBuffer(), RenderHelper.getTexedEntity(QUARTZ))
                , new RenderHelper.RenderContext(1.0f, Color.ORIGIN_COLOR, combinedLight));
        matrixStack.pop();

        matrixStack.push();
        matrixStack.translate(0.3, 0.3, 0);
        matrixStack.scale(0.3f, 0.15f, 0.1f);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(45));
        matrixStack.rotate(Vector3f.ZN.rotationDegrees(135));
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStack, Tessellator.getInstance().getBuffer(), RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.9f, Color.ORIGIN_COLOR, combinedLight));
        matrixStack.pop();

        matrixStack.push();
        matrixStack.translate(-0.3, 0.3, 0);
        matrixStack.scale(0.3f, 0.15f, 0.1f);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(45));
        matrixStack.rotate(Vector3f.ZN.rotationDegrees(135));
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStack, Tessellator.getInstance().getBuffer(), RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.9f, Color.ORIGIN_COLOR, combinedLight));
        matrixStack.pop();

        matrixStack.push();
        matrixStack.translate(0, -0.2, 0);
        matrixStack.scale(0.15f, 1.0f, 0.1f);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(45));
        matrixStack.rotate(Vector3f.ZN.rotationDegrees(135));
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStack, Tessellator.getInstance().getBuffer(), RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.9f, Color.ORIGIN_COLOR, combinedLight));
        matrixStack.pop();

        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        if(data.contextCore().haveMagickContext()) {
            matrixStack.push();
            matrixStack.translate(0, 0.3, 0);
            matrixStack.scale(0.35f, 0.35f, 0.35f);
            ItemStack core = new ItemStack(ModItems.MAGICK_CORE.get());
            ExtraDataUtil.itemManaData(core).spellContext().copy(data.spellContext());
            IBakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(core, null, null);
            Minecraft.getInstance().getItemRenderer().renderItem(core, ItemCameraTransforms.TransformType.GUI, false, matrixStack, bufferIn, combinedLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
            matrixStack.pop();
        }
        matrixStack.pop();
    }
}
