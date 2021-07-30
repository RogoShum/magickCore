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
            IVertexBuilder buffer = bufferIn.getBuffer(RenderHelper.getTexedCylinderGlint(RenderHelper.blankTex));
            float[] color = RenderHelper.ORIGIN;
            float alpha = 0.2f;
            buffer.pos(matrixStackIn.getLast().getMatrix(), 0, 0, 0).color(color[0], color[1], color[2], alpha).
                    tex(0, 0).overlay(OverlayTexture.NO_OVERLAY).
                    lightmap(RenderHelper.renderLight).normal(0.5f, 0.5f, 0.5f).endVertex();

            float offsetX = ((MagickBarrierTileEntity)tileEntityIn.getOutputFirst()).getPos().getX() - tileEntityIn.getPos().getX();
            float offsetY = ((MagickBarrierTileEntity)tileEntityIn.getOutputFirst()).getPos().getY() - tileEntityIn.getPos().getY();
            float offsetZ = ((MagickBarrierTileEntity)tileEntityIn.getOutputFirst()).getPos().getZ() - tileEntityIn.getPos().getZ();

            matrixStackIn.translate(offsetX, offsetY, offsetZ);

            buffer.pos(matrixStackIn.getLast().getMatrix(), 0, 0, 0).color(color[0], color[1], color[2], alpha).
                    tex(1, 0).overlay(OverlayTexture.NO_OVERLAY).
                    lightmap(RenderHelper.renderLight).normal(0.5f, 0.5f, 0.5f).endVertex();

            offsetX = ((MagickBarrierTileEntity)tileEntityIn.getOutputSecond()).getPos().getX() - ((MagickBarrierTileEntity)tileEntityIn.getOutputFirst()).getPos().getX();
            offsetY = ((MagickBarrierTileEntity)tileEntityIn.getOutputSecond()).getPos().getY() - ((MagickBarrierTileEntity)tileEntityIn.getOutputFirst()).getPos().getY();
            offsetZ = ((MagickBarrierTileEntity)tileEntityIn.getOutputSecond()).getPos().getZ() - ((MagickBarrierTileEntity)tileEntityIn.getOutputFirst()).getPos().getZ();

            matrixStackIn.translate(offsetX, offsetY, offsetZ);

            buffer.pos(matrixStackIn.getLast().getMatrix(), 0, 0, 0).color(color[0], color[1], color[2], alpha).
                    tex(1, 1).overlay(OverlayTexture.NO_OVERLAY).
                    lightmap(RenderHelper.renderLight).normal(0.5f, 0.5f, 0.5f).endVertex();
        }
    }
}
