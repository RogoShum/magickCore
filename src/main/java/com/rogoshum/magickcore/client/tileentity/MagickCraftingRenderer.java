package com.rogoshum.magickcore.client.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.tileentity.MagickCraftingTileEntity;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;
import net.minecraft.core.Vec3i;

import java.util.OptionalDouble;

import com.mojang.blaze3d.vertex.Tesselator;

public class MagickCraftingRenderer implements BlockEntityRenderer<MagickCraftingTileEntity> {
    private static final RenderType TYPE = RenderType.create(MagickCore.MOD_ID + "_lines", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES, 256
            ,false, false, RenderType.CompositeState.builder().setLayeringState(RenderHelper.VIEW_OFFSET_Z_LAYERING).setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeLinesShader)).setTransparencyState(new RenderStateShard.TransparencyStateShard("magick_translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthMask(false);
    }, () -> {
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    })).setTexturingState(RenderHelper.lineState(OptionalDouble.of(5))).createCompositeState(false));

    public MagickCraftingRenderer(BlockEntityRendererProvider.Context p_173554_) {
    }

    @Override
    public void render(MagickCraftingTileEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

    }
}
