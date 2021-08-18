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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class MagickBarrierRenderer extends EasyTileRenderer<MagickBarrierTileEntity>{
    @Override
    public void render(MagickBarrierTileEntity tileEntityIn, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        if(tileEntityIn != null && !tileEntityIn.isRemoved() && tileEntityIn.isClosed())
        {
            RenderEvent.activeTileEntityRender(tileEntityIn);
            float[] color = tileEntityIn.getElementData().getElement().getRenderer().getColor();
            float alpha = 0.4f;
            float alphaAdd = 0.0f;
            matrixStackIn.push();
            IVertexBuilder buffer = bufferIn.getBuffer(RenderHelper.getTexedCylinderGlint(RenderHelper.blankTex, 1f, 0f));
            buffer.pos(matrixStackIn.getLast().getMatrix(), 0, 0, 0).color(color[0], color[1], color[2], alphaAdd + alpha * tileEntityIn.mana / tileEntityIn.requiredMana).
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

            IVertexBuilder line = bufferIn.getBuffer(RenderHelper.LINES);
            alphaAdd = 0.25f;
            line.pos(matrixStackIn.getLast().getMatrix(), 0, 0, 0).color(color[0], color[1], color[2], alphaAdd + alpha * tileEntityIn.mana / tileEntityIn.requiredMana).
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
}
