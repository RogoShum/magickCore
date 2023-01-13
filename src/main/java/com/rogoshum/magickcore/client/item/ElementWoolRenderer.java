package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class ElementWoolRenderer extends ItemStackTileEntityRenderer {
    protected final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    protected static final ResourceLocation cylinder_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_rotate.png");
    protected static final ResourceLocation wool = new ResourceLocation("textures/block/white_wool.png");
    private static final RenderType RENDER_TYPE_0 = RenderHelper.getTexedOrbSolid(wool);
    private static final RenderType RENDER_TYPE_1 = RenderHelper.getTexedOrbGlint(RenderHelper.SPHERE_ROTATE, 0.1f, 0f);

    @Override
    public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStackIn, IRenderTypeBuffer typeBufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5F, 0.5F, 0.5F);
        Color color = Color.ORIGIN_COLOR;

        if(stack.hasTag()) {
            CompoundNBT blockTag = stack.getTag();

            String type = blockTag.getString("ELEMENT") == "" ? LibElements.ORIGIN : blockTag.getString("ELEMENT");
            color = MagickCore.proxy.getElementRender(type).getColor();
        }
        BufferBuilder bufferIn = Tessellator.getInstance().getBuilder();
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, bufferIn, RENDER_TYPE_0), new RenderHelper.RenderContext(1.0f, color, RenderHelper.renderLight));
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, bufferIn, RENDER_TYPE_1), new RenderHelper.RenderContext(1.0f, color, RenderHelper.renderLight));

        matrixStackIn.popPose();
    }

    public static CompoundNBT getBlockTag(CompoundNBT tag)
    {
        CompoundNBT blockTag = tag.getCompound("BlockEntityTag");
        return blockTag;
    }
}
