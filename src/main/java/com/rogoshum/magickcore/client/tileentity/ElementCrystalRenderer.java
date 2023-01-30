package com.rogoshum.magickcore.client.tileentity;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.block.ElementCrystalBlock;
import com.rogoshum.magickcore.common.tileentity.ElementCrystalTileEntity;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ElementCrystalRenderer extends BlockEntityRenderer<ElementCrystalTileEntity> {
    private Color color = Color.ORIGIN_COLOR;

    public ElementCrystalRenderer(BlockEntityRenderDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(ElementCrystalTileEntity tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState state = tile.getLevel().getBlockState(tile.getBlockPos());
        if(!(state.getBlock() instanceof ElementCrystalBlock)) return;
        int age = state.getValue(CropBlock.AGE);
        ResourceLocation crystal = new ResourceLocation(MagickCore.MOD_ID + ":textures/blocks/element_crystal_stage" + Integer.toString(age) + ".png");
        RenderType TYPE = RenderHelper.getTexedOrbGlow(crystal);
        MagickElement element = MagickRegistry.getElement(tile.eType);
        if(element != null)
            color = element.getRenderer().getColor();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        RenderHelper.RenderContext renderContext = new RenderHelper.RenderContext(1.0f, color, RenderHelper.renderLight);
        matrixStackIn.translate(0, -0.1, 0);
        matrixStackIn.scale(0.5f, 0.5f, 0.5f);

        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0, 0.0, 0.5);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, TYPE), renderContext);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0, 0.0, -0.5);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, TYPE), renderContext);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, -0.5);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, TYPE), renderContext);
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, 0.5);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, TYPE), renderContext);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }
}
