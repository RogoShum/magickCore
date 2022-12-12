package com.rogoshum.magickcore.client.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.tileentity.MagickCraftingTileEntity;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;

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

        Vector3d view = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double dis = view.distanceTo(Vector3d.copyCentered(tileEntityIn.getPos()));
        if(dis > 5) return;
        float baseAlpha = (float) ((5f - dis) * 0.1f);

        MagickCraftingTileEntity.CraftingMatrix craftingMatrix = tileEntityIn.getCraftingMatrix();
        int i = 1;
        int stack = 0;
        for(Vector3i vec : craftingMatrix.getMatrix().keySet()) {
            if(vec.getY() + 1 > stack) {
                i += vec.getY() + 1;
                stack = vec.getY() + 1;
            }
        }
        i = Math.min(3, i);

        for(int x = -1; x < 2; ++x) {
            for(int z = -1; z < 2; ++z) {
                for(int y = 0; y < i; ++y) {
                    renderCube(matrixStackIn, bufferIn, new Vector3i(x, y, z), y >= stack ? baseAlpha * 0.3f : baseAlpha);
                }
            }
        }
    }

    public void renderCube(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, Vector3i offset, float alpha) {
        WorldRenderer.drawBoundingBox(matrixStackIn, bufferIn.getBuffer(RenderType.getLines()), new AxisAlignedBB(BlockPos.ZERO).grow(-0.66666).offset(offset.getX() * 0.333, offset.getY() * 0.333 - 0.333, offset.getZ() * 0.333), 0, 0.2f, 0.9f, alpha);
    }
}
