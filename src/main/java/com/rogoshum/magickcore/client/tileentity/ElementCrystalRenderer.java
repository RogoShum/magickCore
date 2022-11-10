package com.rogoshum.magickcore.client.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.tileentity.ElementCrystalTileEntity;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import net.minecraft.block.CropsBlock;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class ElementCrystalRenderer extends TileEntityRenderer<ElementCrystalTileEntity> {
    private Color color = Color.ORIGIN_COLOR;

    public ElementCrystalRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(ElementCrystalTileEntity tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        int age = tile.getWorld().getBlockState(tile.getPos()).get(CropsBlock.AGE);
        ResourceLocation crystal = new ResourceLocation(MagickCore.MOD_ID + ":textures/blocks/element_crystal_stage" + Integer.toString(age) + ".png");
        RenderType TYPE = RenderHelper.getTexedOrbGlow(crystal);
        MagickElement element = MagickRegistry.getElement(tile.eType);
        if(element != null)
            color = element.getRenderer().getColor();

        matrixStackIn.push();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        RenderHelper.RenderContext renderContext = new RenderHelper.RenderContext(1.0f, color, RenderHelper.renderLight);
        matrixStackIn.translate(0, -0.1, 0);
        matrixStackIn.scale(0.5f, 0.5f, 0.5f);

        matrixStackIn.push();
        matrixStackIn.translate(0.0, 0.0, 0.5);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, TYPE), renderContext);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.translate(0.0, 0.0, -0.5);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, TYPE), renderContext);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, -0.5);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, TYPE), renderContext);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, 0.5);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, TYPE), renderContext);
        matrixStackIn.pop();
        matrixStackIn.pop();
    }
}
