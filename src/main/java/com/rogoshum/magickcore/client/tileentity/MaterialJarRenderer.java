package com.rogoshum.magickcore.client.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.block.tileentity.MaterialJarTileEntity;
import com.rogoshum.magickcore.block.tileentity.SpiritCrystalTileEntity;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderHelper;
import com.rogoshum.magickcore.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class MaterialJarRenderer extends TileEntityRenderer<MaterialJarTileEntity> {
    protected static final ResourceLocation cylinder_bloom = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_bloom.png");

    public MaterialJarRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(MaterialJarTileEntity tile, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.push();
        matrixStackIn.translate(0.5, 0.1901, 0.5);

        matrixStackIn.push();
        matrixStackIn.scale(0.3f, 0.38f, 0.3f);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), RenderHelper.getTexedEntity(RenderHelper.blankTex))
                , new RenderHelper.RenderContext(0.2f, Color.ORIGIN_COLOR, combinedLightIn));
        matrixStackIn.scale(0.9f, 0.9f, 0.9f);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), RenderHelper.getTexedEntity(RenderHelper.blankTex))
                , new RenderHelper.RenderContext(0.05f, Color.ORIGIN_COLOR, RenderHelper.halfLight));
        matrixStackIn.pop();

        if(!tile.getStack().isEmpty()) {
            matrixStackIn.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
            matrixStackIn.push();
            matrixStackIn.translate(0, 0.15, 0);
            matrixStackIn.scale(0.01f, 0.01f, .01f);
            matrixStackIn.rotate(Vector3f.ZN.rotationDegrees(180));
            String count = String.valueOf(tile.getCount());
            Minecraft.getInstance().fontRenderer.drawString(matrixStackIn, count, -count.length()*3, 2, 0);
            matrixStackIn.pop();
            matrixStackIn.translate(0, -0.12f, 0);
            matrixStackIn.scale(0.5f, 0.5f,0.5f);
            IBakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(tile.getStack(), null, null);
            IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
            Minecraft.getInstance().getItemRenderer().renderItem(tile.getStack(), ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, renderTypeBuffer, combinedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel_);
            renderTypeBuffer.finish();
        }

        matrixStackIn.pop();
    }
}
