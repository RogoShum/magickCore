package com.rogoshum.magickcore.client.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.common.block.ItemExtractorBlock;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.tileentity.ItemExtractorTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class ItemExtractorRenderer implements BlockEntityRenderer<ItemExtractorTileEntity> {
    protected static final ResourceLocation wool = new ResourceLocation("textures/block/glass.png");

    public ItemExtractorRenderer(BlockEntityRendererProvider.Context p_173554_) {
    }

    @Override
    public void render(ItemExtractorTileEntity tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        if(tile.getLevel() != null) {
            matrixStackIn.pushPose();
            BlockState state = tile.getLevel().getBlockState(tile.getBlockPos());
            Optional<Direction> value = state.getOptionalValue(ItemExtractorBlock.FACING);
            if(value.isPresent()) {
                matrixStackIn.mulPose(value.get().getOpposite().getRotation());
            }
            matrixStackIn.translate(0, -0.75, 0);
            matrixStackIn.scale(4, 4, 4);
            Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(ModItems.ITEM_EXTRACTOR.get()), ItemTransforms.TransformType.GROUND, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 0);
            matrixStackIn.popPose();
        }
        matrixStackIn.scale(0.99f, 0.99f, 0.99f);
        //RenderHelper.renderCube(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RenderHelper.getTexedEntity(wool)), new RenderHelper.RenderContext(1.0f, Color.ORIGIN_COLOR, RenderHelper.renderLight));
        //RenderHelper.renderCube(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), RENDER_TYPE_1), new RenderHelper.RenderContext(1.0f, Color.ORIGIN_COLOR, RenderHelper.renderLight));
        matrixStackIn.popPose();
    }
}
