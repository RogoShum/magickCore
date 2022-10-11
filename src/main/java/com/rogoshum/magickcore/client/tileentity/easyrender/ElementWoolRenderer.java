package com.rogoshum.magickcore.client.tileentity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.block.tileentity.ElementWoolTileEntity;
import com.rogoshum.magickcore.client.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.magick.Color;
import com.rogoshum.magickcore.magick.MagickElement;
import com.rogoshum.magickcore.registry.MagickRegistry;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class ElementWoolRenderer extends EasyTileRenderer<ElementWoolTileEntity>{
    protected final ResourceLocation wool = new ResourceLocation("textures/block/white_wool.png");

    @Override
    public void render(ElementWoolTileEntity tileEntityIn, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        Color color = Color.ORIGIN_COLOR;

        matrixStackIn.scale(0.5f, 0.5f, 0.5f);
        MagickElement element = MagickRegistry.getElement(tileEntityIn.eType);
        if(element != null)
            color = element.getRenderer().getColor();
        float alpha = 1.0f;
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        matrixStackIn.push();
        matrixStackIn.translate(0.0, 0.0, 1.0);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180));
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedOrbSolid(wool)), alpha, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.translate(0.0, 0.0, -1.0);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedOrbSolid(wool)), alpha, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, -1.0);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedOrbSolid(wool)), alpha, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.translate(1.0, 0.0, 0.0);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(270));
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedOrbSolid(wool)), alpha, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(270));
        matrixStackIn.translate(0.0, 0.0, -1.0);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedOrbSolid(wool)), alpha, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, -1.0);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedOrbSolid(wool)), alpha, color);
        matrixStackIn.pop();
        ////////////////////////////////////////////////////////////////////////////////

        alpha = 1.0f;
        matrixStackIn.push();
        matrixStackIn.translate(0.0, 0.0, 1.0);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180));
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedOrbGlint(cylinder_rotate, 1f, 0f)), alpha, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.translate(0.0, 0.0, -1.0);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedOrbGlint(cylinder_rotate, 1f, 0f)), alpha, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, -1.0);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedOrbGlint(cylinder_rotate, 1f, 0f)), alpha, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.translate(1.0, 0.0, 0.0);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(270));
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedOrbGlint(cylinder_rotate, 1f, 0f)), alpha, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(270));
        matrixStackIn.translate(0.0, 0.0, -1.0);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedOrbGlint(cylinder_rotate, 1f, 0f)), alpha, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, -1.0);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, buffer, RenderHelper.getTexedOrbGlint(cylinder_rotate, 1f, 0f)), alpha, color);
        matrixStackIn.pop();
    }
}
