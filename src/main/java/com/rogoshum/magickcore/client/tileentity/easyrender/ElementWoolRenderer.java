package com.rogoshum.magickcore.client.tileentity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.block.tileentity.ElementCrystalTileEntity;
import com.rogoshum.magickcore.block.tileentity.ElementWoolTileEntity;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.init.ModElements;
import net.minecraft.block.CropsBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class ElementWoolRenderer extends EasyTileRenderer<ElementWoolTileEntity>{
    protected final ResourceLocation wool = new ResourceLocation("textures/block/white_wool.png");

    @Override
    public void render(ElementWoolTileEntity tileEntityIn, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        float[] color = {1, 1, 1};

        matrixStackIn.scale(0.5f, 0.5f, 0.5f);
        IManaElement element = ModElements.getElement(tileEntityIn.eType);
        if(element != null)
            color = element.getRenderer().getColor();

        matrixStackIn.push();
        matrixStackIn.translate(0.0, 0.0, 1.0);
        RenderHelper.renderStaticParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrb(wool)), 1.0f, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.translate(0.0, 0.0, -1.0);
        RenderHelper.renderStaticParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrb(wool)), 1.0f, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, -1.0);
        RenderHelper.renderStaticParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrb(wool)), 1.0f, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, 1.0);
        RenderHelper.renderStaticParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrb(wool)), 1.0f, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, 1.0);
        RenderHelper.renderStaticParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrb(wool)), 1.0f, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, -1.0);
        RenderHelper.renderStaticParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrb(wool)), 1.0f, color);
        matrixStackIn.pop();
        ////////////////////////////////////////////////////////////////////////////////

        float alpha = 0.2f;
        matrixStackIn.push();
        matrixStackIn.translate(0.0, 0.0, 1.0);
        RenderHelper.renderStaticParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrbGlint(cylinder_rotate)), alpha, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.translate(0.0, 0.0, -1.0);
        RenderHelper.renderStaticParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrbGlint(cylinder_rotate)), alpha, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, -1.0);
        RenderHelper.renderStaticParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrbGlint(cylinder_rotate)), alpha, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, 1.0);
        RenderHelper.renderStaticParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrbGlint(cylinder_rotate)), alpha, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, 1.0);
        RenderHelper.renderStaticParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrbGlint(cylinder_rotate)), alpha, color);
        matrixStackIn.pop();

        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
        matrixStackIn.translate(0.0, 0.0, -1.0);
        RenderHelper.renderStaticParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrbGlint(cylinder_rotate)), alpha, color);
        matrixStackIn.pop();
    }
}
