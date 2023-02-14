package com.rogoshum.magickcore.client.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.tileentity.MagickCraftingTileEntity;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;
import net.minecraft.core.Vec3i;

import java.util.OptionalDouble;

import com.mojang.blaze3d.vertex.Tesselator;

public class MagickCraftingRenderer implements BlockEntityRenderer<MagickCraftingTileEntity> {
    private static final RenderType TYPE = RenderType.create(MagickCore.MOD_ID + "_lines", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES, 256
            ,false, false, RenderType.CompositeState.builder().setTransparencyState(new RenderStateShard.TransparencyStateShard("magick_translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthMask(false);
    }, () -> {
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    })).setTexturingState(RenderHelper.lineState(OptionalDouble.of(5))).createCompositeState(false));

    public MagickCraftingRenderer(BlockEntityRendererProvider.Context p_173554_) {
    }

    @Override
    public void render(MagickCraftingTileEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        float alpha = Math.min(tileEntityIn.ticksExisted / 30f, 1.0f);
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5f, 0.001f, 0.5f);
        RenderType type = RenderHelper.getTexedCylinderGlint(RenderHelper.ripple_5, 0.5f, 0);
        matrixStackIn.mulPose(Vector3f.ZN.rotationDegrees(180));
        RenderHelper.CylinderContext context = new RenderHelper.CylinderContext(0.9f, 1.2f, 2f, 2.4f, 16
                , 0, 0.2f * alpha, 0.4f, Color.BLUE_COLOR);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), type), context);
        matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(90));
        type = RenderHelper.getTexedCylinderGlint(RenderHelper.ripple_4, 10, 0);
        context = new RenderHelper.CylinderContext(1.4f, 1.2f, 3f, 1.6f, 16
                , 0.5f * alpha, alpha, 1f, Color.BLUE_COLOR);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), type), context);
        matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(90));
        matrixStackIn.scale(1.3f, 1.3f, 1f);
        type = RenderHelper.getTexedOrbGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/shield/element_shield_" + (tileEntityIn.ticksExisted % 10) + ".png"));
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), type), new RenderHelper.RenderContext(0.3f * alpha, Color.BLUE_COLOR, RenderHelper.renderLight));
        matrixStackIn.scale(1.17f, 1.17f, 1f);
        type = RenderHelper.getTexedOrbGlow(ModElements.ORIGIN.getRenderer().getOrbTexture());
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), type), new RenderHelper.RenderContext(0.2f * alpha, Color.BLUE_COLOR, RenderHelper.renderLight));
        matrixStackIn.popPose();

        Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double dis = view.distanceTo(Vec3.atCenterOf(tileEntityIn.getBlockPos()));
        if(dis > 5) return;
        float baseAlpha = (float) ((5f - dis) * 0.1f);

        MagickCraftingTileEntity.CraftingMatrix craftingMatrix = tileEntityIn.getCraftingMatrix();
        int i = 1;
        int stack = 0;
        for(Vec3i vec : craftingMatrix.getMatrix().keySet()) {
            if(vec.getY() + 1 > stack) {
                i += vec.getY() + 1;
                stack = vec.getY() + 1;
            }
        }
        i = Math.min(3, i);

        for(int x = -1; x < 2; ++x) {
            for(int z = -1; z < 2; ++z) {
                for(int y = 0; y < i; ++y) {
                    boolean flag = y >= stack;
                    float cAlpha = flag ? baseAlpha * 0.25f : baseAlpha * 0.35f;
                    Color color = flag ? ModElements.VOID_COLOR : Color.BLUE_COLOR;
                    renderCube(matrixStackIn, bufferIn, new Vec3i(x, y, z), color, cAlpha);
                }
            }
        }
    }

    public void renderCube(PoseStack matrixStackIn, MultiBufferSource bufferIn, Vec3i offset, Color color, float alpha) {
        LevelRenderer.renderLineBox(matrixStackIn, bufferIn.getBuffer(TYPE), new AABB(BlockPos.ZERO)
                .inflate(-0.66666).move(offset.getX() * 0.333, offset.getY() * 0.333 - 0.333, offset.getZ() * 0.333), color.r(), color.g(), color.b(), alpha);
    }
}
