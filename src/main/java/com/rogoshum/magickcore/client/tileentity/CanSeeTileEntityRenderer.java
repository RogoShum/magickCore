package com.rogoshum.magickcore.client.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.block.tileentity.CanSeeTileEntity;
import com.rogoshum.magickcore.block.tileentity.MagickRepeaterTileEntity;
import com.rogoshum.magickcore.event.RenderEvent;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;

public class CanSeeTileEntityRenderer<T extends CanSeeTileEntity> extends TileEntityRenderer<T> {
    public CanSeeTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        RenderEvent.activeTileEntityRender(tileEntityIn);
    }
}
