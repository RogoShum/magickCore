package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.Color;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemExtractorRenderer extends BlockEntityWithoutLevelRenderer {
    protected final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    protected static final ResourceLocation cylinder_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_rotate.png");
    protected static final ResourceLocation wool = new ResourceLocation("textures/block/glass.png");
    private static final RenderType RENDER_TYPE_0 = RenderHelper.getTexedOrb(wool);
    private static final RenderType RENDER_TYPE_1 = RenderHelper.getTexedOrbGlint(RenderHelper.SPHERE_ROTATE, 0.1f, 0f);

    public ItemExtractorRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource typeBufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5F, 0.5F, 0.5F);
        BufferBuilder bufferIn = Tesselator.getInstance().getBuilder();
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, bufferIn, RENDER_TYPE_0), new RenderHelper.RenderContext(1.0f, Color.ORIGIN_COLOR, RenderHelper.renderLight));
        //RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, bufferIn, RENDER_TYPE_1), new RenderHelper.RenderContext(1.0f, color, RenderHelper.renderLight));

        matrixStackIn.popPose();
    }
}
