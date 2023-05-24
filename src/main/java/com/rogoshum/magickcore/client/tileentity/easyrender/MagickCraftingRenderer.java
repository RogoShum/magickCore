package com.rogoshum.magickcore.client.tileentity.easyrender;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.tileentity.MagickCraftingTileEntity;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.units.qual.C;

import java.util.HashMap;
import java.util.OptionalDouble;
import java.util.function.Consumer;

public class MagickCraftingRenderer extends EasyTileRenderer<MagickCraftingTileEntity>{
    private static final RenderType CYLINDER_TYPE_0 = RenderHelper.getTexturedUniGlint(RenderHelper.ripple_5, 0.5f, 0);
    private static final RenderType CYLINDER_TYPE_1 = RenderHelper.getTexturedUniGlint(RenderHelper.ripple_4, 10, 0);
    private static final RenderType PARTICLE_TYPE = RenderHelper.getTexturedQuadsGlow(ModElements.ORIGIN.getRenderer().getOrbTexture());
    private RenderType PARTICLE_TYPE_DYNAMIC = null;
    private float alpha=0f;
    private static final RenderType TYPE = RenderType.create(MagickCore.MOD_ID + "_lines", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES, 256
            ,false, false, RenderType.CompositeState.builder().setLayeringState(RenderHelper.VIEW_OFFSET_Z_LAYERING).setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeLinesShader)).setTransparencyState(new RenderStateShard.TransparencyStateShard("magick_translucent_transparency", () -> {
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.depthMask(false);
            }, () -> {
                RenderSystem.depthMask(true);
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            })).setTexturingState(RenderHelper.lineState(OptionalDouble.of(5))).createCompositeState(false));

    public MagickCraftingRenderer(MagickCraftingTileEntity tile) {
        super(tile);
    }


    @Override
    public void update() {
        super.update();
        alpha = Math.min(tile.ticksExisted / 30f, 1.0f);
        PARTICLE_TYPE_DYNAMIC = RenderHelper.getTexturedQuadsGlow(new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/shield/element_shield_" + (tile.ticksExisted % 10) + ".png"));
    }

    @Override
    public void baseOffset(PoseStack matrixStackIn) {
        super.baseOffset(matrixStackIn);
        matrixStackIn.translate(0.0f, -0.49f, 0.0f);
    }

    public void renderCylinder0(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        RenderHelper.CylinderContext context = new RenderHelper.CylinderContext(0.9f, 1.2f, 2f, 2, 2.4f
                , 0, 0.2f, 0.4f);
        RenderHelper.renderCylinderCache(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), CYLINDER_TYPE_0), context
                , new RenderHelper.RenderContext(alpha, Color.BLUE_COLOR, RenderHelper.renderLight, true));
    }

    public void renderCylinder1(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(90));
        RenderHelper.CylinderContext context = new RenderHelper.CylinderContext(1.4f, 1.2f, 3f, 2, 1.6f
                , 0.5f, 1, 1f);
        RenderHelper.renderCylinderCache(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), CYLINDER_TYPE_1), context
        , new RenderHelper.RenderContext(alpha, Color.BLUE_COLOR, RenderHelper.renderLight, true));
    }

    public void renderParticle0(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(90));
        matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(90));
        matrixStackIn.scale(1.3f, 1.3f, 1f);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), PARTICLE_TYPE_DYNAMIC), new RenderHelper.RenderContext(0.1f * alpha, Color.BLUE_COLOR, RenderHelper.renderLight));
    }

    public void renderParticle1(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(90));
        matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(90));
        matrixStackIn.scale(1.3f, 1.3f, 1f);
        matrixStackIn.scale(1.17f, 1.17f, 1f);
        RenderHelper.renderStaticParticle(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), PARTICLE_TYPE), new RenderHelper.RenderContext(0.1f * alpha, Color.BLUE_COLOR, RenderHelper.renderLight));
    }

    public void renderCube(RenderParams params) {
        super.baseOffset(params.matrixStack);
        params.matrixStack.translate(-0.5, -0.5, -0.5);
        Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double dis = view.distanceTo(Vec3.atCenterOf(this.tile.getBlockPos()));
        if(dis > 5) return;
        float baseAlpha = (float) ((5f - dis) * 0.2f);

        MagickCraftingTileEntity.CraftingMatrix craftingMatrix = this.tile.getCraftingMatrix();
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
                    renderCube(params.matrixStack, params.buffer, new Vec3i(x, y, z), color, cAlpha);
                }
            }
        }
    }

    public void renderCube(PoseStack matrixStackIn, VertexConsumer buffer, Vec3i offset, Color color, float alpha) {
        LevelRenderer.renderLineBox(matrixStackIn, buffer, new AABB(BlockPos.ZERO)
                .inflate(-0.66666).move(offset.getX() * 0.333, offset.getY() * 0.333 - 0.333, offset.getZ() * 0.333), color.r(), color.g(), color.b(), alpha);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE), this::renderCube);
        map.put(new RenderMode(CYLINDER_TYPE_0), this::renderCylinder0);
        map.put(new RenderMode(CYLINDER_TYPE_1), this::renderCylinder1);
        map.put(new RenderMode(PARTICLE_TYPE), this::renderParticle1);
        if(PARTICLE_TYPE_DYNAMIC != null)
            map.put(new RenderMode(PARTICLE_TYPE_DYNAMIC), this::renderParticle0);
        return map;
    }
}
