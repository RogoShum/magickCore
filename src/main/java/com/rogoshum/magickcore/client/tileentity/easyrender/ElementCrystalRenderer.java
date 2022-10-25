package com.rogoshum.magickcore.client.tileentity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.block.tileentity.ElementCrystalTileEntity;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderHelper;
import com.rogoshum.magickcore.magick.Color;
import com.rogoshum.magickcore.magick.MagickElement;
import com.rogoshum.magickcore.registry.MagickRegistry;
import net.minecraft.block.CropsBlock;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class ElementCrystalRenderer extends EasyTileRenderer<ElementCrystalTileEntity>{
    @Override
    public void render(ElementCrystalTileEntity tileEntityIn, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        Color color = Color.ORIGIN_COLOR;
        int age = tileEntityIn.getWorld().getBlockState(tileEntityIn.getPos()).get(CropsBlock.AGE);
        ResourceLocation crystal = new ResourceLocation(MagickCore.MOD_ID + ":textures/blocks/element_crystal_stage" + Integer.toString(age) + ".png");
        matrixStackIn.translate(0, -0.1, 0);
        matrixStackIn.scale(0.5f, 0.5f, 0.5f);
        MagickElement element = MagickRegistry.getElement(tileEntityIn.eType);
        if(element != null)
            color = element.getRenderer().getColor();

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        RenderHelper.RenderContext renderContext = new RenderHelper.RenderContext(1.0f, color);

        matrixStackIn.push();
        matrixStackIn.translate(0.0, 0.0, 0.5);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedOrbGlow(crystal)), renderContext);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.translate(0.0, 0.0, -0.5);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedOrbGlow(crystal)), renderContext);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, -0.5);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedOrbGlow(crystal)), renderContext);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, 0.5);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedOrbGlow(crystal)), renderContext);
        matrixStackIn.pop();
    }
}
