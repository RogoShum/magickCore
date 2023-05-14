package com.rogoshum.magickcore.client.tileentity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.tileentity.ElementWoolTileEntity;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ElementWoolRenderer implements BlockEntityRenderer<ElementWoolTileEntity> {
    protected static final ResourceLocation wool = new ResourceLocation("textures/block/white_wool.png");

    private static final RenderType RENDER_TYPE_0 = RenderHelper.getTexturedQuadsSolid(wool);
    private static final RenderType RENDER_TYPE_1 = RenderHelper.getTexturedQuadsGlint(RenderHelper.SPHERE_ROTATE);

    public ElementWoolRenderer(BlockEntityRendererProvider.Context p_173554_) {
    }

    @Override
    public void render(ElementWoolTileEntity tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        float f = MagickCore.proxy.getRunTick();
        RenderSystem.setShaderLights(Vector3f.ZERO, Vector3f.ZERO);
        RenderHelper.renderCubeCache(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RENDER_TYPE_0), new RenderHelper.RenderContext(1.0f, tile.getColor(), RenderHelper.renderLight));
        RenderHelper.renderCubeCache(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RENDER_TYPE_1), new RenderHelper.RenderContext(1.0f, tile.getColor(), RenderHelper.renderLight));
        matrixStackIn.popPose();
    }
}
