package com.rogoshum.magickcore.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.OptionalDouble;

import static org.lwjgl.opengl.GL11.*;

@OnlyIn(Dist.CLIENT)
public class RenderHelper {
    public static final float[] ORIGIN = {1.0f, 1.0f, 1.0f};
    public static final float[] GREEN = {0.2f, 1.0f, 0.2f};
    public static final float[] RED = {1.0f, 0.2f, 0.2f};

    public static final float[] SOLAR = {1.0f, 0.6f, 0.3f};
    public static final float[] VOID = {0.25f, 0.0f, 1.0f};
    public static final float[] ARC = {0.68f, 1.0f, 1.0f};

    public static final float[] STASIS = {0.6f, 0.6f, 1.0f};
    public static final float[] WITHER = {0.3f, 0.7f, 0.2f};
    public static final float[] TAKEN = {0.5f, 0.5f, 0.5f};

    private static final HashMap<Integer, ArrayList<Vector3d>> sphereVertex = new HashMap<Integer, ArrayList<Vector3d>>();
    private static final HashMap<Integer, ArrayList<Vector2f>> sphereUV = new HashMap<Integer, ArrayList<Vector2f>>();
    private static final HashMap<ResourceLocation, RenderType> TexedOrb = new HashMap<ResourceLocation, RenderType>();

    public static final ResourceLocation blankTex = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    public static final ResourceLocation DISSOVE = new ResourceLocation(MagickCore.MOD_ID + ":textures/dissove.png");
    public static final int renderLight = 15728880;
    public static final int halfLight = 7864440;

    protected static final RenderState.TransparencyState NO_TRANSPARENCY = new RenderState.TransparencyState("no_transparency", RenderSystem::disableBlend, () -> {
    });
    protected static final RenderState.TransparencyState ADDITIVE_TRANSPARENCY = new RenderState.TransparencyState("additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
    }, () -> {
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderState.TransparencyState LIGHTNING_TRANSPARENCY = new RenderState.TransparencyState("lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
    }, () -> {
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    protected static final RenderState.TransparencyState NORMAL_LIGHTNING_TRANSPARENCY = new RenderState.TransparencyState("normal_lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    protected static final RenderState.TransparencyState GLINT_TRANSPARENCY = new RenderState.TransparencyState("glint_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
    }, () -> {
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderState.TransparencyState CRUMBLING_TRANSPARENCY = new RenderState.TransparencyState("crumbling_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.depthMask(false);
    }, () -> {
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderState.TransparencyState TRANSLUCENT_TRANSPARENCY = new RenderState.TransparencyState("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthMask(false);
    }, () -> {
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    protected static final RenderState.TargetState PARTICLE_TARGET = new RenderState.TargetState(MagickCore.MOD_ID + "particles_target", () -> {
        if (Minecraft.isFabulousGraphicsEnabled()) {
            Minecraft.getInstance().worldRenderer.func_239230_s_().bindFramebuffer(false);
        }

    }, () -> {
        if (Minecraft.isFabulousGraphicsEnabled()) {
            Minecraft.getInstance().getFramebuffer().bindFramebuffer(false);
        }

    });

    protected static final RenderState.TargetState OUTLINE_TARGET = new RenderState.TargetState(MagickCore.MOD_ID + "outline_target", () -> {
        Minecraft.getInstance().worldRenderer.getEntityOutlineFramebuffer().bindFramebuffer(false);
    }, () -> {
        Minecraft.getInstance().getFramebuffer().bindFramebuffer(false);
    });

    protected static final RenderState.TargetState TRANSLUCENT_TARGET = new RenderState.TargetState("translucent_target", () -> {
        if (Minecraft.isFabulousGraphicsEnabled()) {
            Minecraft.getInstance().worldRenderer.func_239228_q_().bindFramebuffer(false);
        }

    }, () -> {
        if (Minecraft.isFabulousGraphicsEnabled()) {
            Minecraft.getInstance().getFramebuffer().bindFramebuffer(false);
        }

    });
    protected static final RenderState.TargetState field_239238_U_ = new RenderState.TargetState("weather_target", () -> {
        if (Minecraft.isFabulousGraphicsEnabled()) {
            Minecraft.getInstance().worldRenderer.func_239231_t_().bindFramebuffer(false);
        }

    }, () -> {
        if (Minecraft.isFabulousGraphicsEnabled()) {
            Minecraft.getInstance().getFramebuffer().bindFramebuffer(false);
        }

    });
    protected static final RenderState.TargetState field_239239_V_ = new RenderState.TargetState("clouds_target", () -> {
        if (Minecraft.isFabulousGraphicsEnabled()) {
            Minecraft.getInstance().worldRenderer.func_239232_u_().bindFramebuffer(false);
        }

    }, () -> {
        if (Minecraft.isFabulousGraphicsEnabled()) {
            Minecraft.getInstance().getFramebuffer().bindFramebuffer(false);
        }

    });
    protected static final RenderState.TargetState field_241712_U_ = new RenderState.TargetState("item_entity_target", () -> {
        if (Minecraft.isFabulousGraphicsEnabled()) {
            Minecraft.getInstance().worldRenderer.func_239229_r_().bindFramebuffer(false);
        }

    }, () -> {
        if (Minecraft.isFabulousGraphicsEnabled()) {
            Minecraft.getInstance().getFramebuffer().bindFramebuffer(false);
        }

    });
    protected static final RenderState.TexturingState OUTLINE_TEXTURING = new RenderState.TexturingState(MagickCore.MOD_ID + "outline_texturing", () -> {
        RenderSystem.setupOutline();
    }, () -> {
        RenderSystem.teardownOutline();
    });

    public static void setupGlintTexturing() {
        RenderSystem.matrixMode(GL_TEXTURE);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        long i = Util.milliTime() * 8L;
        float f = (float) (i % 110000L) / 110000.0F;
        float f1 = (float) (i % 30000L) / 30000.0F;
        RenderSystem.translatef(f, f1, 0.0F);
        RenderSystem.rotatef(glintRotate, 0.0F, 1.0F, 0.0F);
        RenderSystem.scalef(glintScale, glintScale, glintScale);
        RenderSystem.matrixMode(GL_MODELVIEW);
    }

    public static float glintScale = 0.32f;
    public static float glintRotate = 90f;

    public static void setupLaserTexturing() {
        RenderSystem.matrixMode(GL_TEXTURE);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        long i = Util.milliTime() * 8L;
        float f1 = (i % (3000L * glintScale)) / (3000.0F * glintScale);
        RenderSystem.translatef(0.0F, f1, 0.0F);
        RenderSystem.scalef(1.0f, glintScale, 1.0f);

        RenderSystem.matrixMode(GL_MODELVIEW);
    }

    public static void setupCylinderTexturing() {
        RenderSystem.matrixMode(GL_TEXTURE);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        long i = Util.milliTime() * 8L;
        float f1 = (float) (i % 30000L) / 30000.0F;
        RenderSystem.translatef(0.0f, f1, 0.0F);
        RenderSystem.rotatef(glintRotate, 0.0F, 1.0F, 0.0F);
        RenderSystem.scalef(glintScale, glintScale, glintScale);
        RenderSystem.matrixMode(GL_MODELVIEW);
    }

    public static RenderState.TexturingState CYLINDER_GLINT_TEXTURING = new RenderState.TexturingState("cylinder_glint_texturing",
            RenderHelper::setupCylinderTexturing
            , () -> {
        RenderSystem.matrixMode(GL_TEXTURE);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(GL_MODELVIEW);
    });

    public static RenderState.TexturingState LASER_GLINT_TEXTURING = new RenderState.TexturingState("laser_glint_texturing",
            RenderHelper::setupLaserTexturing
            , () -> {
        RenderSystem.matrixMode(GL_TEXTURE);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(GL_MODELVIEW);
    });

    public static RenderState.TexturingState ENTITY_GLINT_TEXTURING = new RenderState.TexturingState("entity_glint_texturing",
            RenderHelper::setupGlintTexturing
            , () -> {
        RenderSystem.matrixMode(GL_TEXTURE);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(GL_MODELVIEW);
    });

    protected static final RenderState.WriteMaskState COLOR_DEPTH_WRITE = new RenderState.WriteMaskState(true, true);
    protected static final RenderState.WriteMaskState COLOR_WRITE = new RenderState.WriteMaskState(true, false);
    protected static final RenderState.CullState CULL_DISABLED = new RenderState.CullState(false);
    protected static final RenderState.DiffuseLightingState DIFFUSE_LIGHTING_ENABLED = new RenderState.DiffuseLightingState(true);
    protected static final RenderState.AlphaState DEFAULT_ALPHA = new RenderState.AlphaState(0.003921569F);
    protected static final RenderState.LightmapState LIGHTMAP_ENABLED = new RenderState.LightmapState(true);
    protected static final RenderState.OverlayState OVERLAY_ENABLED = new RenderState.OverlayState(true);
    protected static final RenderState.DepthTestState DEPTH_ALWAYS = new RenderState.DepthTestState("always", 519);
    protected static final RenderState.DepthTestState DEPTH_GREATER = new RenderState.DepthTestState(">", 516);
    protected static final RenderState.DepthTestState DEPTH_NOTE = new RenderState.DepthTestState("!=", 517);
    protected static final RenderState.DepthTestState DEPTH_EGREATER = new RenderState.DepthTestState(">=", 518);
    protected static final RenderState.DepthTestState DEPTH_EQUAL = new RenderState.DepthTestState("==", 514);
    protected static final RenderState.DepthTestState DEPTH_NEVER = new RenderState.DepthTestState("never", 512);
    protected static final RenderState.DepthTestState DEPTH_LESS = new RenderState.DepthTestState("<", 513);
    protected static final RenderState.DepthTestState DEPTH_LEQUAL = new RenderState.DepthTestState("<=", 515);
    protected static final RenderState.ShadeModelState SHADE_ENABLED = new RenderState.ShadeModelState(true);
    protected static final RenderState.LineState DEFAULT_LINE = new RenderState.LineState(OptionalDouble.of(6.0D));

    public static RenderType getTexedOrbSolid(ResourceLocation locationIn) {
        RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new RenderState.TextureState(locationIn, false, false)).transparency(NO_TRANSPARENCY).alpha(DEFAULT_ALPHA).shadeModel(SHADE_ENABLED).overlay(OVERLAY_ENABLED).writeMask(COLOR_DEPTH_WRITE).cull(CULL_DISABLED).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).build(false);
        RenderType type = RenderType.makeType("_Tex_Orb_Solid", DefaultVertexFormats.ENTITY, GL_QUADS, 256, true, true, rendertype$state);
        return type;
    }

    public static RenderType getTexedOrb(ResourceLocation locationIn) {
        return getTexedOrb(locationIn, 0.003921569F);
    }

    public static RenderType getTexedOrbGlow(ResourceLocation locationIn) {
        return getTexedOrbGlow(locationIn, 0.003921569F);
    }

    public static RenderType getTexedOrb(ResourceLocation locationIn, float alpha) {
        RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new RenderState.TextureState(locationIn, false, false)).cull(CULL_DISABLED).transparency(TRANSLUCENT_TRANSPARENCY).target(TRANSLUCENT_TARGET).alpha(new RenderState.AlphaState(alpha)).overlay(OVERLAY_ENABLED).writeMask(COLOR_DEPTH_WRITE).cull(CULL_DISABLED).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).build(false);
        RenderType type = RenderType.makeType("_Tex_Orb", DefaultVertexFormats.ENTITY, GL_QUADS, 256, false, false, rendertype$state);
        return type;
    }

    public static RenderType getTexedOrbGlow(ResourceLocation locationIn, float alpha) {
        RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new RenderState.TextureState(locationIn, false, false)).cull(CULL_DISABLED).transparency(LIGHTNING_TRANSPARENCY).target(TRANSLUCENT_TARGET).alpha(new RenderState.AlphaState(alpha)).overlay(OVERLAY_ENABLED).writeMask(COLOR_DEPTH_WRITE).cull(CULL_DISABLED).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).build(false);
        RenderType type = RenderType.makeType("_TexedOrbGlow", DefaultVertexFormats.ENTITY, GL_QUADS, 256, false, false, rendertype$state);
        return type;
    }

    public static RenderType getTexedOrbGlint(ResourceLocation locationIn, float glintScale, float glintRotate) {
        RenderHelper.glintScale = glintScale;
        RenderHelper.glintRotate = glintRotate;
        RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new RenderState.TextureState(locationIn, false, false)).cull(CULL_DISABLED).transparency(GLINT_TRANSPARENCY).target(TRANSLUCENT_TARGET).alpha(DEFAULT_ALPHA).overlay(OVERLAY_ENABLED).writeMask(COLOR_DEPTH_WRITE).cull(CULL_DISABLED).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).texturing(ENTITY_GLINT_TEXTURING).build(false);
        RenderType type = RenderType.makeType("_TexedOrbGlint", DefaultVertexFormats.ENTITY, GL_QUADS, 256, true, true, rendertype$state);
        return type;
    }

    public static RenderType getTexedEntityGlow(ResourceLocation locationIn) {
        RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new RenderState.TextureState(locationIn, false, false)).transparency(LIGHTNING_TRANSPARENCY).target(TRANSLUCENT_TARGET).writeMask(COLOR_DEPTH_WRITE).alpha(DEFAULT_ALPHA).overlay(OVERLAY_ENABLED).shadeModel(SHADE_ENABLED).cull(CULL_DISABLED).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).build(true);
        RenderType type = RenderType.makeType("_TexedEntityGlow", DefaultVertexFormats.ENTITY, GL_QUADS, 256, true, true, rendertype$state);
        return type;
    }

    public static RenderType getTexedEntity(ResourceLocation locationIn) {
        RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new RenderState.TextureState(locationIn, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).target(TRANSLUCENT_TARGET).writeMask(COLOR_DEPTH_WRITE).alpha(DEFAULT_ALPHA).overlay(OVERLAY_ENABLED).shadeModel(SHADE_ENABLED).cull(CULL_DISABLED).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).depthTest(DEPTH_LEQUAL).build(true);
        RenderType type = RenderType.makeType("_TexedEntity", DefaultVertexFormats.ENTITY, GL_QUADS, 256, true, true, rendertype$state);
        return type;
    }

    public static RenderType getTexedEntityGlint(ResourceLocation locationIn, float glintScale, float glintRotate) {
        RenderHelper.glintScale = glintScale;
        RenderHelper.glintRotate = glintRotate;
        RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new RenderState.TextureState(locationIn, false, false)).transparency(LIGHTNING_TRANSPARENCY).target(TRANSLUCENT_TARGET).writeMask(COLOR_DEPTH_WRITE).alpha(DEFAULT_ALPHA).overlay(OVERLAY_ENABLED).shadeModel(SHADE_ENABLED).cull(CULL_DISABLED).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).texturing(ENTITY_GLINT_TEXTURING).build(true);
        RenderType type = RenderType.makeType("_TexedEntityGlint", DefaultVertexFormats.ENTITY, GL_QUADS, 256, true, true, rendertype$state);
        return type;
    }

    public static RenderType getTexedLaserGlint(ResourceLocation locationIn, float glintScale) {
        RenderHelper.glintScale = glintScale;
        RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new RenderState.TextureState(locationIn, false, false)).transparency(LIGHTNING_TRANSPARENCY).target(TRANSLUCENT_TARGET).writeMask(COLOR_DEPTH_WRITE).alpha(DEFAULT_ALPHA).overlay(OVERLAY_ENABLED).shadeModel(SHADE_ENABLED).cull(CULL_DISABLED).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).texturing(LASER_GLINT_TEXTURING).build(true);
        RenderType type = RenderType.makeType("_TexedEntityGlint", DefaultVertexFormats.ENTITY, GL_QUADS, 256, true, true, rendertype$state);
        return type;
    }

    public static RenderType getTexedCylinderGlow(ResourceLocation locationIn) {
        RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new RenderState.TextureState(locationIn, false, false)).transparency(LIGHTNING_TRANSPARENCY).target(TRANSLUCENT_TARGET).writeMask(COLOR_DEPTH_WRITE).alpha(DEFAULT_ALPHA).overlay(OVERLAY_ENABLED).shadeModel(SHADE_ENABLED).cull(CULL_DISABLED).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).build(false);
        RenderType type = RenderType.makeType("_TexedCylinderGlow", DefaultVertexFormats.ENTITY, GL_TRIANGLE_STRIP, 256, true, false, rendertype$state);
        return type;
    }

    public static RenderType getTexedCylinderGlint(ResourceLocation locationIn, float glintScale, float glintRotate) {
        RenderHelper.glintScale = glintScale;
        RenderHelper.glintRotate = glintRotate;
        RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new RenderState.TextureState(locationIn, false, false)).transparency(LIGHTNING_TRANSPARENCY).target(TRANSLUCENT_TARGET).writeMask(COLOR_DEPTH_WRITE).alpha(DEFAULT_ALPHA).overlay(OVERLAY_ENABLED).shadeModel(SHADE_ENABLED).cull(CULL_DISABLED).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).texturing(CYLINDER_GLINT_TEXTURING).build(false);
        RenderType type = RenderType.makeType("_TexedCylinderGlint", DefaultVertexFormats.ENTITY, GL_TRIANGLE_STRIP, 256, true, false, rendertype$state);
        return type;
    }

    public static RenderType getTexedSphereGlow(ResourceLocation locationIn, float glintScale, float glintRotate) {
        RenderHelper.glintScale = glintScale;
        RenderHelper.glintRotate = glintRotate;
        RenderType.State rendertype$state = RenderType.State.getBuilder().texture(new RenderState.TextureState(locationIn, false, false)).transparency(LIGHTNING_TRANSPARENCY).target(TRANSLUCENT_TARGET).writeMask(COLOR_DEPTH_WRITE).alpha(DEFAULT_ALPHA).overlay(OVERLAY_ENABLED).shadeModel(SHADE_ENABLED).cull(CULL_DISABLED).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).texturing(ENTITY_GLINT_TEXTURING).build(false);
        RenderType type = RenderType.makeType("_TexedSphereGlow", DefaultVertexFormats.ENTITY, GL_QUAD_STRIP, 256, true, false, rendertype$state);
        return type;
    }

    public static RenderType getLineGlow(double width) {
        RenderType.State rendertype$state = RenderType.State.getBuilder().writeMask(COLOR_WRITE).shadeModel(SHADE_ENABLED).transparency(LIGHTNING_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).target(TRANSLUCENT_TARGET).overlay(OVERLAY_ENABLED).line(new RenderState.LineState(OptionalDouble.of(width))).build(false);
        RenderType type = RenderType.makeType(MagickCore.MOD_ID + "_LINES", DefaultVertexFormats.ENTITY, GL_LINE_LOOP, 256, true, false, rendertype$state);
        return type;
    }

    public static final ResourceLocation TAKEN_LAYER = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/taken_layer.png");
    public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/enchanted_item_glint.png");
    public static final ResourceLocation ripple_4 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/ripple/ripple_4.png");
    public static final ResourceLocation ripple_2 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/ripple/ripple_2.png");
    public static final ResourceLocation ripple_5 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/ripple/ripple_5.png");
    public static final RenderType POINTS = RenderType.makeType(MagickCore.MOD_ID + "_POINTS", DefaultVertexFormats.ENTITY, GL_POINTS, 256, false, true, RenderType.State.getBuilder().writeMask(COLOR_WRITE).shadeModel(SHADE_ENABLED).transparency(LIGHTNING_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).target(TRANSLUCENT_TARGET).overlay(OVERLAY_ENABLED).build(false));
    public static final RenderType LINES = RenderType.makeType(MagickCore.MOD_ID + "_LINES", DefaultVertexFormats.ENTITY, GL_LINE_LOOP, 256, false, true, RenderType.State.getBuilder().writeMask(COLOR_WRITE).shadeModel(SHADE_ENABLED).transparency(LIGHTNING_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).target(TRANSLUCENT_TARGET).overlay(OVERLAY_ENABLED).build(false));
    public static final RenderType ORB = RenderType.makeType(MagickCore.MOD_ID + "_Orb", DefaultVertexFormats.ENTITY, GL_QUADS, 256, true, true, RenderType.State.getBuilder().transparency(LIGHTNING_TRANSPARENCY).writeMask(COLOR_DEPTH_WRITE).shadeModel(SHADE_ENABLED).cull(CULL_DISABLED).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).alpha(DEFAULT_ALPHA).overlay(OVERLAY_ENABLED).target(TRANSLUCENT_TARGET).build(false));
    public static final RenderType SPHERE = RenderType.makeType(MagickCore.MOD_ID + "_Sphere", DefaultVertexFormats.ENTITY, GL_QUADS, 256, true, true, RenderType.State.getBuilder().writeMask(COLOR_DEPTH_WRITE).shadeModel(SHADE_ENABLED).cull(CULL_DISABLED).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).alpha(DEFAULT_ALPHA).overlay(OVERLAY_ENABLED).target(TRANSLUCENT_TARGET).build(false));
    public static final RenderType CRUMBLING = RenderType.makeType(MagickCore.MOD_ID + "_CRUMBLING", DefaultVertexFormats.ENTITY, GL_QUADS, 256, true, true, RenderType.State.getBuilder().writeMask(COLOR_DEPTH_WRITE).shadeModel(SHADE_ENABLED).cull(CULL_DISABLED).transparency(CRUMBLING_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).alpha(DEFAULT_ALPHA).overlay(OVERLAY_ENABLED).target(TRANSLUCENT_TARGET).build(false));
    public static final RenderType LIGHTING = RenderType.makeType(MagickCore.MOD_ID + "_Lighting", DefaultVertexFormats.ENTITY, GL_QUADS, 256, true, true, RenderType.State.getBuilder().writeMask(COLOR_DEPTH_WRITE).shadeModel(SHADE_ENABLED).cull(CULL_DISABLED).transparency(LIGHTNING_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).alpha(DEFAULT_ALPHA).overlay(OVERLAY_ENABLED).target(TRANSLUCENT_TARGET).build(false));
    public static final RenderType OUTLINE = RenderType.makeType(MagickCore.MOD_ID + "_OUTLINE", DefaultVertexFormats.POSITION, GL_QUADS, 256, true, true, RenderType.State.getBuilder().writeMask(COLOR_DEPTH_WRITE).shadeModel(SHADE_ENABLED).cull(CULL_DISABLED).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(DIFFUSE_LIGHTING_ENABLED).lightmap(LIGHTMAP_ENABLED).alpha(DEFAULT_ALPHA).overlay(OVERLAY_ENABLED).target(OUTLINE_TARGET).build(true));

    public static final RenderType GLOW_BLOCK = RenderType.makeType(MagickCore.MOD_ID + "_GLOW_BLOCK", DefaultVertexFormats.BLOCK, 7, 262144, false, true, getTranslucentState());

    private static RenderType.State getTranslucentState() {
        return RenderType.State.getBuilder().shadeModel(SHADE_ENABLED).lightmap(LIGHTMAP_ENABLED).texture(new RenderState.TextureState(AtlasTexture.LOCATION_BLOCKS_TEXTURE, false, true)).transparency(NORMAL_LIGHTNING_TRANSPARENCY).target(TRANSLUCENT_TARGET).build(true);
    }

    public static void renderSphere(Matrix4f matrix4f, IRenderTypeBuffer.Impl bufferIn, RenderType type, int stacks, float alpha, VectorHitReaction[] hitReaction, float[] color, int packedLightIn, float limit) {
        renderSphere(matrix4f, bufferIn, type, stacks, alpha, hitReaction, color, packedLightIn, false, null, limit);
    }

    public static void renderSphere(Matrix4f matrix4f, IRenderTypeBuffer.Impl bufferIn, RenderType type, int stacks, float alpha, float[] color, int packedLightIn) {
        renderSphere(matrix4f, bufferIn, type, stacks, alpha, null, color, packedLightIn, 0);
    }

    public static final Vector3f[] QuadVector = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};

    public static void renderParticle(MatrixStack matrixStackIn, IVertexBuilder buffer, float alpha, float[] color) {
        renderParticle(matrixStackIn, buffer, alpha, color, false, null, 0f);
    }

    public static void renderCylinder(RenderType type, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float alpha, float alphaMid, float[] color, float baseRadius, float height, int stacks, VectorHitReaction[] hitReaction, float limit) {
        double majorStep = height / stacks;
        double minorStep = 2.0 * Math.PI / stacks;
        int i, j;

        matrixStackIn.push();
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        for (i = 0; i < stacks; ++i) {
            float z0 = (float) (0.5 * height - i * majorStep);
            float z1 = (float) (z0 - majorStep);
            matrixStackIn.push();
            IVertexBuilder buffer = bufferIn.getBuffer(type);
            for (j = 0; j <= stacks; ++j) {
                double a = j * minorStep;
                float x = (float) (baseRadius * Math.cos(a));
                float y = (float) (baseRadius * Math.sin(a));

                float z0MidFactor = (float) Math.pow(Math.min(1f, Math.abs(z0) * 2 / height), 0.25);
                float z0EdgeFactor = (1 - z0MidFactor);
                float z0Alpha = z0MidFactor * alpha + z0EdgeFactor * alphaMid;

                float z1MidFactor = (float) Math.pow(Math.min(1f, Math.abs(z1) * 2 / height), 0.25);
                float z1EdgeFactor = (1 - z1MidFactor);
                float z1Alpha = z1MidFactor * alpha + z1EdgeFactor * alphaMid;

                posCylinderVertex(matrix4f, buffer, x, z0, y, j / (float) stacks, i / (float) stacks, z0Alpha, color[0], color[1], color[2], RenderHelper.renderLight, hitReaction, limit);
                posCylinderVertex(matrix4f, buffer, x, z1, y, j / (float) stacks, (i + 1) / (float) stacks, z1Alpha, color[0], color[1], color[2], RenderHelper.renderLight, hitReaction, limit);
                //buffer.pos(matrix4f, (float)V0.x, (float)V0.y, (float)V0.z).color(color[0], color[1], color[2], alpha).tex(j / (float) stacks, i / (float) stacks).overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(x / baseRadius, 0.0f, y / baseRadius).endVertex();
                //buffer.pos(matrix4f, (float)V1.x, (float)V1.y, (float)V1.z).color(color[0], color[1], color[2], alpha).tex(j / (float) stacks, (i + 1) / (float) stacks).overlay(OverlayTexture.NO_OVERLAY).lightmap(RenderHelper.renderLight).normal(x / baseRadius, 0.0f, y / baseRadius).endVertex();
            }
            bufferIn.finish(type);
            matrixStackIn.pop();
        }

        matrixStackIn.pop();
    }

    public static void renderLaserParticle(MatrixStack matrixStackIn, IVertexBuilder buffer, float length, float alpha, float[] color, boolean shake, String shakeName, float limit) {
        matrixStackIn.push();
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        //matrixStackIn.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
        int light = renderLight;
        if (shake) {
            VertexShakerHelper.VertexGroup group = VertexShakerHelper.getGroup(shakeName);
            group.putVertex(-1.0F, 0.0F, 0.0F, limit);
            group.putVertex(-1.0F, length, 0.0F, limit);
            group.putVertex(1.0F, length, 0.0F, limit);
            group.putVertex(1.0F, 0.0F, 0.0F, limit);

            Vector3d V0 = group.getVertex(-1.0F, 0.0F, 0.0F).getPositionVec();
            Vector3d V1 = group.getVertex(-1.0F, length, 0.0F).getPositionVec();
            Vector3d V2 = group.getVertex(1.0F, length, 0.0F).getPositionVec();
            Vector3d V3 = group.getVertex(1.0F, 0.0F, 0.0F).getPositionVec();

            buffer.pos(matrix4f, (float) V0.x, (float) V0.y, (float) V0.z).color(color[0], color[1], color[2], alpha).tex(1.0f, 1.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal((float) V0.x, (float) V0.y, (float) V0.z).endVertex();
            buffer.pos(matrix4f, (float) V1.x, (float) V1.y, (float) V1.z).color(color[0], color[1], color[2], alpha).tex(1.0f, 0.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal((float) V1.x, (float) V1.y, (float) V1.z).endVertex();
            buffer.pos(matrix4f, (float) V2.x, (float) V2.y, (float) V2.z).color(color[0], color[1], color[2], alpha).tex(0.0f, 0.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal((float) V2.x, (float) V2.y, (float) V2.z).endVertex();
            buffer.pos(matrix4f, (float) V3.x, (float) V3.y, (float) V3.z).color(color[0], color[1], color[2], alpha).tex(0.0f, 1.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal((float) V3.x, (float) V3.y, (float) V3.z).endVertex();
        } else {
            buffer.pos(matrix4f, -1.0F, 0.0F, 0.0F).color(color[0], color[1], color[2], alpha).tex(1.0f, 1.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(-1.0F, 0.0F, 0.0F).endVertex();
            buffer.pos(matrix4f, -1.0F, length, 0.0F).color(color[0], color[1], color[2], alpha).tex(1.0f, 0.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(-1.0F, 1.0F * length, 0.0F).endVertex();
            buffer.pos(matrix4f, 1.0F, length, 0.0F).color(color[0], color[1], color[2], alpha).tex(0.0f, 0.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0F, 1.0F * length, 0.0F).endVertex();
            buffer.pos(matrix4f, 1.0F, 0.0F, 0.0F).color(color[0], color[1], color[2], alpha).tex(0.0f, 1.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1.0F, 0.0F, 0.0F).endVertex();

            buffer.pos(matrix4f, 0.0F, 0.0F, -1.0F).color(color[0], color[1], color[2], alpha).tex(1.0f, 1.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(0.0F, 0.0F, -1.0F).endVertex();
            buffer.pos(matrix4f, 0.0F, length, -1.0F).color(color[0], color[1], color[2], alpha).tex(1.0f, 0.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(0.0F, 1.0F * length, -1.0F).endVertex();
            buffer.pos(matrix4f, 0.0F, length, 1.0F).color(color[0], color[1], color[2], alpha).tex(0.0f, 0.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(0.0F, 1.0F * length, 1.0F).endVertex();
            buffer.pos(matrix4f, 0.0F, 0.0F, 1.0F).color(color[0], color[1], color[2], alpha).tex(0.0f, 1.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(0.0F, 0.0F, 1.0F).endVertex();
        }
        matrixStackIn.pop();
    }

    public static void renderStaticParticle(MatrixStack matrixStackIn, IVertexBuilder buffer, float alpha, float[] color) {
        renderStaticParticle(matrixStackIn, buffer, alpha, color, false, "", 0.0f);
    }

    public static void renderStaticParticle(MatrixStack matrixStackIn, IVertexBuilder buffer, float alpha, float[] color, boolean shake, String shakeName, float limit) {
        matrixStackIn.push();
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        int light = renderLight;
        if (shake) {
            VertexShakerHelper.VertexGroup group = VertexShakerHelper.getGroup(shakeName);
            group.putVertex(QuadVector[0].getX(), QuadVector[0].getY(), QuadVector[0].getZ(), limit);
            group.putVertex(QuadVector[1].getX(), QuadVector[1].getY(), QuadVector[1].getZ(), limit);
            group.putVertex(QuadVector[2].getX(), QuadVector[2].getY(), QuadVector[2].getZ(), limit);
            group.putVertex(QuadVector[3].getX(), QuadVector[3].getY(), QuadVector[3].getZ(), limit);

            Vector3d V0 = group.getVertex(QuadVector[0].getX(), QuadVector[0].getY(), QuadVector[0].getZ()).getPositionVec();
            Vector3d V1 = group.getVertex(QuadVector[1].getX(), QuadVector[1].getY(), QuadVector[1].getZ()).getPositionVec();
            Vector3d V2 = group.getVertex(QuadVector[2].getX(), QuadVector[2].getY(), QuadVector[2].getZ()).getPositionVec();
            Vector3d V3 = group.getVertex(QuadVector[3].getX(), QuadVector[3].getY(), QuadVector[3].getZ()).getPositionVec();

            buffer.pos(matrix4f, (float) V0.x, (float) V0.y, (float) V0.z).color(color[0], color[1], color[2], alpha).tex(1.0f, 1.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal((float) V0.x, (float) V0.y, (float) V0.z).endVertex();
            buffer.pos(matrix4f, (float) V1.x, (float) V1.y, (float) V1.z).color(color[0], color[1], color[2], alpha).tex(1.0f, 0.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal((float) V1.x, (float) V1.y, (float) V1.z).endVertex();
            buffer.pos(matrix4f, (float) V2.x, (float) V2.y, (float) V2.z).color(color[0], color[1], color[2], alpha).tex(0.0f, 0.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal((float) V2.x, (float) V2.y, (float) V2.z).endVertex();
            buffer.pos(matrix4f, (float) V3.x, (float) V3.y, (float) V3.z).color(color[0], color[1], color[2], alpha).tex(0.0f, 1.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal((float) V3.x, (float) V3.y, (float) V3.z).endVertex();
        } else {
            buffer.pos(matrix4f, QuadVector[0].getX(), QuadVector[0].getY(), QuadVector[0].getZ()).color(color[0], color[1], color[2], alpha).tex(1.0f, 1.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1, 1, 1).endVertex();
            buffer.pos(matrix4f, QuadVector[1].getX(), QuadVector[1].getY(), QuadVector[1].getZ()).color(color[0], color[1], color[2], alpha).tex(1.0f, 0.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1, 1, 1).endVertex();
            buffer.pos(matrix4f, QuadVector[2].getX(), QuadVector[2].getY(), QuadVector[2].getZ()).color(color[0], color[1], color[2], alpha).tex(0.0f, 0.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1, 1, 1).endVertex();
            buffer.pos(matrix4f, QuadVector[3].getX(), QuadVector[3].getY(), QuadVector[3].getZ()).color(color[0], color[1], color[2], alpha).tex(0.0f, 1.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(1, 1, 1).endVertex();
        }
        matrixStackIn.pop();
    }

    public static void renderParticle(MatrixStack matrixStackIn, IVertexBuilder buffer, float alpha, float[] color, boolean shake, String shakeName, float limit) {
        matrixStackIn.push();
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        matrixStackIn.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
        int light = renderLight;
        if (shake) {
            VertexShakerHelper.VertexGroup group = VertexShakerHelper.getGroup(shakeName);
            group.putVertex(QuadVector[0].getX(), QuadVector[0].getY(), QuadVector[0].getZ(), limit);
            group.putVertex(QuadVector[1].getX(), QuadVector[1].getY(), QuadVector[1].getZ(), limit);
            group.putVertex(QuadVector[2].getX(), QuadVector[2].getY(), QuadVector[2].getZ(), limit);
            group.putVertex(QuadVector[3].getX(), QuadVector[3].getY(), QuadVector[3].getZ(), limit);

            Vector3d V0 = group.getVertex(QuadVector[0].getX(), QuadVector[0].getY(), QuadVector[0].getZ()).getPositionVec();
            Vector3d V1 = group.getVertex(QuadVector[1].getX(), QuadVector[1].getY(), QuadVector[1].getZ()).getPositionVec();
            Vector3d V2 = group.getVertex(QuadVector[2].getX(), QuadVector[2].getY(), QuadVector[2].getZ()).getPositionVec();
            Vector3d V3 = group.getVertex(QuadVector[3].getX(), QuadVector[3].getY(), QuadVector[3].getZ()).getPositionVec();

            buffer.pos(matrix4f, (float) V0.x, (float) V0.y, (float) V0.z).color(color[0], color[1], color[2], alpha).tex(1.0f, 1.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(0.5f, 0.5f, 0.5f).endVertex();
            buffer.pos(matrix4f, (float) V1.x, (float) V1.y, (float) V1.z).color(color[0], color[1], color[2], alpha).tex(1.0f, 0.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(0.5f, 0.5f, 0.5f).endVertex();
            buffer.pos(matrix4f, (float) V2.x, (float) V2.y, (float) V2.z).color(color[0], color[1], color[2], alpha).tex(0.0f, 0.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(0.5f, 0.5f, 0.5f).endVertex();
            buffer.pos(matrix4f, (float) V3.x, (float) V3.y, (float) V3.z).color(color[0], color[1], color[2], alpha).tex(0.0f, 1.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(0.5f, 0.5f, 0.5f).endVertex();
        } else {
            buffer.pos(matrix4f, QuadVector[0].getX(), QuadVector[0].getY(), QuadVector[0].getZ()).color(color[0], color[1], color[2], alpha).tex(1.0f, 1.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(0.5f, 0.5f, 0.5f).endVertex();
            buffer.pos(matrix4f, QuadVector[1].getX(), QuadVector[1].getY(), QuadVector[1].getZ()).color(color[0], color[1], color[2], alpha).tex(1.0f, 0.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(0.5f, 0.5f, 0.5f).endVertex();
            buffer.pos(matrix4f, QuadVector[2].getX(), QuadVector[2].getY(), QuadVector[2].getZ()).color(color[0], color[1], color[2], alpha).tex(0.0f, 0.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(0.5f, 0.5f, 0.5f).endVertex();
            buffer.pos(matrix4f, QuadVector[3].getX(), QuadVector[3].getY(), QuadVector[3].getZ()).color(color[0], color[1], color[2], alpha).tex(0.0f, 1.0f).overlay(OverlayTexture.NO_OVERLAY).lightmap(light).normal(0.5f, 0.5f, 0.5f).endVertex();
        }
        matrixStackIn.pop();
    }

    public static void renderSphere(Matrix4f matrix4f, IRenderTypeBuffer.Impl bufferIn, RenderType type, int stacks, float alpha, VectorHitReaction[] hitReaction, float[] color, int packedLightIn, boolean shake, String name, float limit) {
        if (color == null)
            color = ORIGIN;
        if (stacks <= 2)
            stacks = 2;

        if (stacks % 2 != 0)
            stacks++;

        if (sphereVertex.containsKey(stacks) && sphereUV.containsKey(stacks)) {
            ArrayList<Vector3d> vertexList = sphereVertex.get(stacks);
            ArrayList<Vector2f> uvList = sphereUV.get(stacks);

            if (vertexList.size() == uvList.size()) {
                int order = 0;
                for (int i = 0; i < stacks / 2; i++) {
                    IVertexBuilder buffer = bufferIn.getBuffer(type);
                    for (int j = 0; j <= stacks; j++) {
                        Vector3d vector3d = vertexList.get(order);
                        Vector2f uv = uvList.get(order++);
                        if(shake)
                            posBlendVertex(matrix4f, buffer, (float) vector3d.x, (float) vector3d.y, (float) vector3d.z, uv.x, uv.y, alpha, color[0], color[1], color[2], packedLightIn, hitReaction, limit);
                        else
                            posVertex(matrix4f, buffer, (float) vector3d.x, (float) vector3d.y, (float) vector3d.z, uv.x, uv.y, alpha, color[0], color[1], color[2], packedLightIn, hitReaction, limit);

                        vector3d = vertexList.get(order);
                        uv = uvList.get(order++);
                        if(shake)
                            posBlendVertex(matrix4f, buffer, (float) vector3d.x, (float) vector3d.y, (float) vector3d.z, uv.x, uv.y, alpha, color[0], color[1], color[2], packedLightIn, hitReaction, limit);
                        else
                            posVertex(matrix4f, buffer, (float) vector3d.x, (float) vector3d.y, (float) vector3d.z, uv.x, uv.y, alpha, color[0], color[1], color[2], packedLightIn, hitReaction, limit);
                    }
                    bufferIn.finish(type);
                }
            }

        } else {
            ArrayList<Vector3d> vertex = new ArrayList<>();
            ArrayList<Vector2f> uv = new ArrayList<>();

            float rho, drho, theta, dtheta;
            float x, y, z;
            float s, t, ds, dt;
            int i, j, imin, imax;

            drho = (float) (2.0f * Math.PI / stacks);
            dtheta = (float) (2.0f * Math.PI / stacks);
            ds = 1.0f / stacks;
            dt = 1.0f / stacks;
            t = 1.0f; // because loop now runs from 0
            float radius = 0.5f;
            imin = 0;
            imax = stacks / 2;
            // draw intermediate stacks as quad strips
            for (i = imin; i < imax; i++) {
                rho = i * drho;
                IVertexBuilder buffer = bufferIn.getBuffer(type);
                s = 0.0f;
                for (j = 0; j <= stacks; j++) {
                    theta = (j == stacks) ? 0.0f : j * dtheta;
                    x = (float) (-Math.sin(theta) * Math.sin(rho));
                    y = (float) (Math.cos(theta) * Math.sin(rho));
                    z = (float) Math.cos(rho);

                    posVertex(matrix4f, buffer, x * radius, y * radius, z * radius, s, t, alpha, color[0], color[1], color[2], packedLightIn, hitReaction, limit);
                    vertex.add(new Vector3d(x * radius, z * radius, y * radius));
                    uv.add(new Vector2f(s, t));
                    x = (float) (-Math.sin(theta) * Math.sin(rho + drho));
                    y = (float) (Math.cos(theta) * Math.sin(rho + drho));
                    z = (float) (Math.cos(rho + drho));
                    posVertex(matrix4f, buffer, x * radius, y * radius, z * radius, s, t - dt, alpha, color[0], color[1], color[2], packedLightIn, hitReaction, limit);
                    vertex.add(new Vector3d(x * radius, z * radius, y * radius));
                    uv.add(new Vector2f(s, t - dt));
                    s += ds;
                }
                bufferIn.finish(type);
                t -= dt;
            }

            sphereVertex.put(stacks, vertex);
            sphereUV.put(stacks, uv);
        }
    }

    private static void posVertex(Matrix4f matrix4f, IVertexBuilder bufferIn, float x, float y, float z, float texU, float TexV, float alhpa, float red, float green, float blue, int packedLightIn, VectorHitReaction[] hitReaction, float limit) {
        float maxAlhpa = 0;

        if (hitReaction != null)
            for (int c = 0; c < hitReaction.length; c++) {
                VectorHitReaction reaction = hitReaction[c];
                float add = reaction.IsHit(new Vector3d(x, y, z));
                if (add > maxAlhpa)
                    maxAlhpa = add;
            }

        float redScale = red + (red * maxAlhpa * 2);
        float greenScale = green + (green * maxAlhpa * 2);
        float blueScale = blue + (blue * maxAlhpa * 2);

        if (redScale > 1.0f) redScale = 1.0f;
        if (greenScale > 1.0f) greenScale = 1.0f;
        if (blueScale > 1.0f) blueScale = 1.0f;

        if (alhpa + maxAlhpa >= 1.0f) {
            maxAlhpa = 1.0f - alhpa;
        }

        float scale = 1f - maxAlhpa * limit;

        bufferIn.pos(matrix4f, x * scale, y * scale, z * scale).color(redScale, greenScale, blueScale, alhpa + maxAlhpa).tex(texU, TexV).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(x * scale, y * scale, z * scale).endVertex();
    }

    private static void posBlendVertex(Matrix4f matrix4f, IVertexBuilder bufferIn, float x, float y, float z, float texU, float TexV, float alhpa, float red, float green, float blue, int packedLightIn, VectorHitReaction[] hitReaction, float limit) {
        float maxAlhpa = 0;

        if (hitReaction != null)
            for (int c = 0; c < hitReaction.length; c++) {
                VectorHitReaction reaction = hitReaction[c];
                float add = reaction.IsHit(new Vector3d(x, y, z));
                if (add > maxAlhpa)
                    maxAlhpa = add;
            }

        if (maxAlhpa - alhpa < 0.0f) {
            maxAlhpa = alhpa;
        }

        bufferIn.pos(matrix4f, x, y, z).color(red, green, blue, maxAlhpa - alhpa).tex(texU, TexV).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(x, y, z).endVertex();
    }

    private static void posCylinderVertex(Matrix4f matrix4f, IVertexBuilder bufferIn, float x, float y, float z, float texU, float TexV, float alhpa, float red, float green, float blue, int packedLightIn, VectorHitReaction[] hitReaction, float limit) {
        float maxAlhpa = 0;

        if (hitReaction != null)
            for (int c = 0; c < hitReaction.length; c++) {
                VectorHitReaction reaction = hitReaction[c];
                float add = reaction.IsHit(new Vector3d(x, y, z));
                if (add > maxAlhpa)
                    maxAlhpa = add;
            }

        if (alhpa + maxAlhpa >= 1.0f) {
            maxAlhpa = 1.0f - alhpa;
        }

        float scale = 1f - maxAlhpa * limit;

        bufferIn.pos(matrix4f, x * scale, y * scale, z * scale).color(red, green, blue, alhpa).tex(texU, TexV).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(x * scale, y * scale, z * scale).endVertex();
    }

    private static void posBloomVertex(Matrix4f matrix4f, IVertexBuilder bufferIn, float x, float y, float z, float texU, float TexV, float alhpa, float red, float green, float blue, int packedLightIn, VectorHitReaction[] hitReaction) {
        float maxAlhpa = 0;

        if (hitReaction != null)
            for (int c = 0; c < hitReaction.length; c++) {
                VectorHitReaction reaction = hitReaction[c];
                float add = reaction.IsHit(new Vector3d(x, y, z));
                if (add > maxAlhpa)
                    maxAlhpa = add;
            }
        if (alhpa + maxAlhpa >= 1.0f) {
            maxAlhpa = 1.0f - alhpa;
        }

        bufferIn.pos(matrix4f, x, y, z).color(red, green, blue, alhpa + maxAlhpa).tex(texU, TexV).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLightIn).normal(x, y, z).endVertex();
    }

    private static void posVertexInside(Matrix4f matrix4f, IVertexBuilder bufferIn, float x, float y, float z, float texU, float TexV, float alhpa, float red, float green, float blue, int packedLightIn, VectorHitReaction[] hitReaction) {
        float maxAlhpa = 0;
        if (hitReaction != null)
            for (int c = 0; c < hitReaction.length; c++) {
                VectorHitReaction reaction = hitReaction[c];
                float add = reaction.IsHit(new Vector3d(x, y, -z));
                if (add > maxAlhpa)
                    maxAlhpa = add;
            }

        bufferIn.pos(matrix4f, x, y, -z).color(red, green, blue, alhpa + maxAlhpa).normal(x, y, -z).tex(texU, TexV).endVertex();
    }
}
