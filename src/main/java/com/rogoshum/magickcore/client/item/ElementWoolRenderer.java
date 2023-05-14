package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.Color;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class ElementWoolRenderer extends BlockEntityWithoutLevelRenderer {
    protected final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    protected static final ResourceLocation cylinder_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_rotate.png");
    protected static final ResourceLocation wool = new ResourceLocation("textures/block/white_wool.png");
    private static final RenderType RENDER_TYPE_0 = RenderHelper.getTexturedQuadsSolid(wool);
    private static final RenderType RENDER_TYPE_1 = RenderHelper.getTexturedItemGlint(RenderHelper.SPHERE_ROTATE, 1.0f, 0f);

    public ElementWoolRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource typeBufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5F, 0.5F, 0.5F);
        Color color = Color.ORIGIN_COLOR;

        if(stack.hasTag()) {
            CompoundTag blockTag = stack.getTag();

            String type = blockTag.getString("ELEMENT") == "" ? LibElements.ORIGIN : blockTag.getString("ELEMENT");
            color = MagickCore.proxy.getElementRender(type).getPrimaryColor();
        }
        BufferBuilder bufferIn = Tesselator.getInstance().getBuilder();
        RenderHelper.renderCubeCache(BufferContext.create(matrixStackIn, bufferIn, RENDER_TYPE_0), new RenderHelper.RenderContext(1.0f, color, RenderHelper.renderLight));
        RenderHelper.renderCubeCache(BufferContext.create(matrixStackIn, bufferIn, RENDER_TYPE_1), new RenderHelper.RenderContext(1.0f, color, RenderHelper.renderLight));

        matrixStackIn.popPose();
    }

    public static CompoundTag getBlockTag(CompoundTag tag)
    {
        CompoundTag blockTag = tag.getCompound("BlockEntityTag");
        return blockTag;
    }
}
