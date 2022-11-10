package com.rogoshum.magickcore.client.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.tileentity.MagickCraftingTileEntity;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class MagickCraftingRenderer extends TileEntityRenderer<MagickCraftingTileEntity> {
    protected static final ResourceLocation cylinder_bloom = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_bloom.png");

    public MagickCraftingRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(MagickCraftingTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        float alpha = Math.min(tileEntityIn.ticksExisted / 30f, 1.0f);
        matrixStackIn.push();
        matrixStackIn.translate(0.5f, 0.001f, 0.5f);
        RenderType type = RenderHelper.getTexedCylinderGlint(RenderHelper.ripple_5, 0.5f, 0);
        matrixStackIn.rotate(Vector3f.ZN.rotationDegrees(180));
        RenderHelper.CylinderContext context = new RenderHelper.CylinderContext(0.9f, 1.2f, 2f, 2.4f, 16
                , 0, 0.2f * alpha, 0.4f, Color.BLUE_COLOR);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), type), context);
        matrixStackIn.rotate(Vector3f.YN.rotationDegrees(90));
        type = RenderHelper.getTexedCylinderGlint(RenderHelper.ripple_4, 10, 0);
        context = new RenderHelper.CylinderContext(1.4f, 1.2f, 3f, 1.6f, 16
                , 0.5f * alpha, alpha, 1f, Color.BLUE_COLOR);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), type), context);
        matrixStackIn.rotate(Vector3f.XN.rotationDegrees(90));
        matrixStackIn.scale(1.3f, 1.3f, 1f);
        type = RenderHelper.getTexedOrbGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/shield/element_shield_" + (tileEntityIn.ticksExisted % 10) + ".png"));
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), type), new RenderHelper.RenderContext(0.3f * alpha, Color.BLUE_COLOR, RenderHelper.renderLight));
        matrixStackIn.scale(1.17f, 1.17f, 1f);
        type = RenderHelper.getTexedOrbGlow(ModElements.ORIGIN.getRenderer().getOrbTexture());
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), type), new RenderHelper.RenderContext(0.2f * alpha, Color.BLUE_COLOR, RenderHelper.renderLight));
        matrixStackIn.pop();
    }
}
