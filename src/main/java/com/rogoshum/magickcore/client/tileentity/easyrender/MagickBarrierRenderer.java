package com.rogoshum.magickcore.client.tileentity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.block.tileentity.ElementCrystalTileEntity;
import com.rogoshum.magickcore.block.tileentity.MagickBarrierTileEntity;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.event.RenderEvent;
import com.rogoshum.magickcore.init.ModElements;
import net.minecraft.block.CropsBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class MagickBarrierRenderer extends EasyTileRenderer<MagickBarrierTileEntity>{
    @Override
    public void render(MagickBarrierTileEntity tileEntityIn, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        if(tileEntityIn == null || tileEntityIn.isRemoved()) return;
        float alphaScale = tileEntityIn.mana / tileEntityIn.requiredMana;
        if(Float.isNaN(alphaScale))
            alphaScale = 0;
        matrixStackIn.push();
        float[] color = tileEntityIn.getElementData().getElement().getRenderer().getColor();
        float alpha = 0.5f;
        float alphaAdd = 0.15f;
        float blockBB = 0.25f;
        int angle = 0;

        for(int i = 0; i < 3 ; ++i){
            if(i == 1 )
                matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90));
            else if(i == 2 )
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
            for(int c = 0; c < 4 ; ++c) {
                RenderType type = RenderHelper.getLineGlow(1.5 + alphaScale * 2);
                IVertexBuilder line = bufferIn.getBuffer(type);

                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(angle));
                line.pos(matrixStackIn.getLast().getMatrix(), blockBB, blockBB, blockBB).color(color[0], color[1], color[2], alphaAdd + alpha * alphaScale).
                        tex(0, 0).overlay(OverlayTexture.NO_OVERLAY).
                        lightmap(RenderHelper.renderLight).normal(0.5f, 0.5f, 0.5f).endVertex();

                line.pos(matrixStackIn.getLast().getMatrix(), blockBB, -blockBB, blockBB).color(color[0], color[1], color[2], alphaAdd + alpha * alphaScale).
                        tex(0, 0).overlay(OverlayTexture.NO_OVERLAY).
                        lightmap(RenderHelper.renderLight).normal(0.5f, 0.5f, 0.5f).endVertex();

                bufferIn.finish(type);
                angle += 90;
            }
        }
        matrixStackIn.pop();
        //MagickCore.LOGGER.debug("qwq");

        if(!tileEntityIn.isClosed()) return;

        RenderEvent.activeTileEntityRender(tileEntityIn);
        alpha = 0.4f;
        alphaAdd = 0.0f;
        matrixStackIn.push();
        IVertexBuilder buffer = bufferIn.getBuffer(RenderHelper.getTexedCylinderGlint(RenderHelper.blankTex, 1f, 0f));
        buffer.pos(matrixStackIn.getLast().getMatrix(), 0, 0, 0).color(color[0], color[1], color[2], alphaAdd + alpha * alphaScale).
                tex(0, 0).overlay(OverlayTexture.NO_OVERLAY).
                lightmap(RenderHelper.renderLight).normal(0.5f, 0.5f, 0.5f).endVertex();
        float offsetX1 = ((MagickBarrierTileEntity)tileEntityIn.getOutputFirst()).getPos().getX() - tileEntityIn.getPos().getX();
        float offsetY1 = ((MagickBarrierTileEntity)tileEntityIn.getOutputFirst()).getPos().getY() - tileEntityIn.getPos().getY();
        float offsetZ1 = ((MagickBarrierTileEntity)tileEntityIn.getOutputFirst()).getPos().getZ() - tileEntityIn.getPos().getZ();

        matrixStackIn.translate(offsetX1, offsetY1, offsetZ1);

        buffer.pos(matrixStackIn.getLast().getMatrix(), 0, 0, 0).color(color[0], color[1], color[2],
                alphaAdd + alpha * ((MagickBarrierTileEntity)tileEntityIn.getOutputFirst()).mana / ((MagickBarrierTileEntity)tileEntityIn.getOutputFirst()).requiredMana).
                tex(1, 0).overlay(OverlayTexture.NO_OVERLAY).
                lightmap(RenderHelper.renderLight).normal(0.5f, 0.5f, 0.5f).endVertex();

        float offsetX = ((MagickBarrierTileEntity)tileEntityIn.getOutputSecond()).getPos().getX() - ((MagickBarrierTileEntity)tileEntityIn.getOutputFirst()).getPos().getX();
        float offsetY = ((MagickBarrierTileEntity)tileEntityIn.getOutputSecond()).getPos().getY() - ((MagickBarrierTileEntity)tileEntityIn.getOutputFirst()).getPos().getY();
        float offsetZ = ((MagickBarrierTileEntity)tileEntityIn.getOutputSecond()).getPos().getZ() - ((MagickBarrierTileEntity)tileEntityIn.getOutputFirst()).getPos().getZ();

        matrixStackIn.translate(offsetX, offsetY, offsetZ);

        buffer.pos(matrixStackIn.getLast().getMatrix(), 0, 0, 0).color(color[0], color[1], color[2],
                alphaAdd + alpha * ((MagickBarrierTileEntity)tileEntityIn.getOutputSecond()).mana / ((MagickBarrierTileEntity)tileEntityIn.getOutputSecond()).requiredMana).
                tex(1, 1).overlay(OverlayTexture.NO_OVERLAY).
                lightmap(RenderHelper.renderLight).normal(0.5f, 0.5f, 0.5f).endVertex();
        matrixStackIn.pop();

        IVertexBuilder line = bufferIn.getBuffer(RenderHelper.getLineGlow(1.5 + alphaScale * 2));
        alpha = 1.0f;
        alphaAdd = 0.1f;
        line.pos(matrixStackIn.getLast().getMatrix(), 0, 0, 0).color(color[0], color[1], color[2], alphaAdd + alpha * alphaScale).
                tex(0, 0).overlay(OverlayTexture.NO_OVERLAY).
                lightmap(RenderHelper.renderLight).normal(0.5f, 0.5f, 0.5f).endVertex();

        matrixStackIn.translate(offsetX1, offsetY1, offsetZ1);

        line.pos(matrixStackIn.getLast().getMatrix(), 0, 0, 0).color(color[0], color[1], color[2],
                alphaAdd + alpha * ((MagickBarrierTileEntity)tileEntityIn.getOutputFirst()).mana / ((MagickBarrierTileEntity)tileEntityIn.getOutputFirst()).requiredMana).
                tex(1, 0).overlay(OverlayTexture.NO_OVERLAY).
                lightmap(RenderHelper.renderLight).normal(0.5f, 0.5f, 0.5f).endVertex();

        matrixStackIn.translate(offsetX, offsetY, offsetZ);

        line.pos(matrixStackIn.getLast().getMatrix(), 0, 0, 0).color(color[0], color[1], color[2],
                alphaAdd + alpha * ((MagickBarrierTileEntity)tileEntityIn.getOutputSecond()).mana / ((MagickBarrierTileEntity)tileEntityIn.getOutputSecond()).requiredMana).
                tex(1, 1).overlay(OverlayTexture.NO_OVERLAY).
                lightmap(RenderHelper.renderLight).normal(0.5f, 0.5f, 0.5f).endVertex();

    }
}
