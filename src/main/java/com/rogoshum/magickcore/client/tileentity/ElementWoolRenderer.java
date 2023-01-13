package com.rogoshum.magickcore.client.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.common.tileentity.ElementWoolTileEntity;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.BufferContext;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;

public class ElementWoolRenderer extends TileEntityRenderer<ElementWoolTileEntity> {
    protected static final ResourceLocation wool = new ResourceLocation("textures/block/white_wool.png");

    private static final RenderType RENDER_TYPE_0 = RenderHelper.getTexedOrbSolid(wool);
    private static final RenderType RENDER_TYPE_1 = RenderHelper.getTexedOrbGlint(RenderHelper.SPHERE_ROTATE, 0.1f, 0f);

    public ElementWoolRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(ElementWoolTileEntity tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuilder(), RENDER_TYPE_0), new RenderHelper.RenderContext(1.0f, tile.getColor(), RenderHelper.renderLight));
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuilder(), RENDER_TYPE_1), new RenderHelper.RenderContext(1.0f, tile.getColor(), RenderHelper.renderLight));
        matrixStackIn.popPose();
    }
}
