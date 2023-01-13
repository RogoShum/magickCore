package com.rogoshum.magickcore.client;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IPositionEntity;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.vertex.VectorHitReaction;
import com.rogoshum.magickcore.client.vertex.VertexShakerHelper;
import com.rogoshum.magickcore.client.event.ShaderEvent;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;

@OnlyIn(Dist.CLIENT)
public class RenderHelper {
    public static final Color GREEN = Color.create(0.2f, 1.0f, 0.2f);
    public static final Color RED = Color.create(1.0f, 0.2f, 0.2f);

    public static final ResourceLocation blankTex = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");

    public static final ResourceLocation DISSOVE = new ResourceLocation(MagickCore.MOD_ID + ":textures/dissove.png");
    public static final int renderLight = 15728880;
    public static final int renderLight2 = 15728640;
    public static final int halfLight = 7864440;
    public static boolean queueMode = false;
    private static Class optifineShader = null;
    private static boolean optifineShaderLoaded = false;
    private static boolean checkOptifine = false;
    private static boolean renderingWorld = false;
    private static MatrixStack worldMatrixStack = new MatrixStack();
    private static Matrix4f worldMatrix4f = new Matrix4f();
    protected static final RenderState.TransparencyState NO_TRANSPARENCY = new RenderState.TransparencyState("no_transparency", RenderSystem::disableBlend, () -> {
    });
    protected static final RenderState.TargetState TRANSLUCENT_TARGET = new RenderState.TargetState("translucent_target", () -> {
        /*
        if (isRenderingWorld() && Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        }
         */

    }, () -> {
        /*
        if (isRenderingWorld() && Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        }
         */

    });
    protected static final RenderState.TransparencyState LIGHTNING_TRANSPARENCY = new RenderState.TransparencyState("magick_lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
    }, () -> {
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    protected static final RenderState.TransparencyState ADDITIVE_TRANSPARENCY = new RenderState.TransparencyState("magick_additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
    }, () -> {
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderState.TransparencyState DEPTH_ADDITIVE_TRANSPARENCY = new RenderState.TransparencyState("depth_magick_additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderState.TransparencyState DEPTH_LIGHTNING_TRANSPARENCY = new RenderState.TransparencyState("depth_magick_lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderState.TransparencyState GLINT_TRANSPARENCY = new RenderState.TransparencyState("magick_glint_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
    }, () -> {
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderState.TransparencyState TRANSLUCENT_TRANSPARENCY = new RenderState.TransparencyState("magick_translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthMask(false);
    }, () -> {
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderState.TransparencyState DEPTH_GLINT_TRANSPARENCY = new RenderState.TransparencyState("depth_magick_glint_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    protected static final RenderState.TransparencyState DEPTH_TRANSLUCENT_TRANSPARENCY = new RenderState.TransparencyState("depth_magick_translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
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
        RenderType.State rendertype$state = RenderType.State.builder().setOutputState(TRANSLUCENT_TARGET)
                .setTextureState(new RenderState.TextureState(locationIn, false, false))
                .setTransparencyState(NO_TRANSPARENCY).setAlphaState(DEFAULT_ALPHA).setOverlayState(OVERLAY_ENABLED)
                .setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED).setLightmapState(LIGHTMAP_ENABLED).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Orb_Solid", DefaultVertexFormats.NEW_ENTITY, GL_QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexedParticle(ResourceLocation locationIn) {
        RenderType.State rendertype$state = RenderType.State.builder().setOutputState(TRANSLUCENT_TARGET)
                .setTextureState(new RenderState.TextureState(locationIn, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP_ENABLED).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Particle", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, GL_QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexedParticleGlow(ResourceLocation locationIn) {
        RenderType.State rendertype$state = RenderType.State.builder().setOutputState(TRANSLUCENT_TARGET)
                .setTextureState(new RenderState.TextureState(locationIn, false, false))
                .setTransparencyState(LIGHTNING_TRANSPARENCY).setLightmapState(LIGHTMAP_ENABLED).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Particle_Glow", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, GL_QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexedOrb(ResourceLocation locationIn) {
        RenderType.State rendertype$state = RenderType.State.builder().setOutputState(TRANSLUCENT_TARGET)
                .setTextureState(new RenderState.TextureState(locationIn, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED)
                .setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED).setLightmapState(LIGHTMAP_ENABLED).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Orb", DefaultVertexFormats.NEW_ENTITY, GL_QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexedOrbGlow(ResourceLocation locationIn) {
        RenderType.State rendertype$state = RenderType.State.builder().setOutputState(TRANSLUCENT_TARGET)
                .setTextureState(new RenderState.TextureState(locationIn, false, false))
                .setTransparencyState(LIGHTNING_TRANSPARENCY).setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED)
                .setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED).setLightmapState(LIGHTMAP_ENABLED).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Orb_Glow", DefaultVertexFormats.NEW_ENTITY, GL_QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexedOrbGlint(ResourceLocation locationIn, float glintScale, float glintRotate) {
        RenderType.State rendertype$state = RenderType.State.builder().setOutputState(TRANSLUCENT_TARGET)
                .setTextureState(new RenderState.TextureState(locationIn, false, false)).setCullState(CULL_DISABLED)
                .setTransparencyState(LIGHTNING_TRANSPARENCY).setAlphaState(DEFAULT_ALPHA).setOverlayState(OVERLAY_ENABLED)
                .setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED).setLightmapState(LIGHTMAP_ENABLED)
                .setTexturingState(getEntityGlint(glintScale, glintRotate)).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Orb_Glint_" + glintScale + "_" + glintRotate, DefaultVertexFormats.NEW_ENTITY, GL_QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexedEntityGlow(ResourceLocation locationIn) {
        RenderType.State rendertype$state = RenderType.State.builder()
                .setTextureState(new RenderState.TextureState(locationIn, false, false))
                .setTransparencyState(LIGHTNING_TRANSPARENCY).setShadeModelState(SHADE_ENABLED).setAlphaState(DEFAULT_ALPHA).setOutputState(TRANSLUCENT_TARGET)
                .setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED)
                .setLightmapState(LIGHTMAP_ENABLED).createCompositeState(true);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Entity_Glow", DefaultVertexFormats.NEW_ENTITY, GL_QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexedEntity(ResourceLocation locationIn) {
        RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(locationIn, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setShadeModelState(SHADE_ENABLED).setAlphaState(DEFAULT_ALPHA).setOutputState(TRANSLUCENT_TARGET)
                .setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED)
                .setLightmapState(LIGHTMAP_ENABLED).setDepthTestState(DEPTH_LEQUAL).createCompositeState(true);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Entity", DefaultVertexFormats.NEW_ENTITY, GL_QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexedEntityGlint(ResourceLocation locationIn) {
        RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(locationIn, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setShadeModelState(SHADE_ENABLED).setOutputState(TRANSLUCENT_TARGET)
                .setAlphaState(DEFAULT_ALPHA).setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED)
                .setLightmapState(LIGHTMAP_ENABLED).setTexturingState(getEntityGlint(2.0f, 0.0f)).createCompositeState(true);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Entity_Glint_Solid", DefaultVertexFormats.NEW_ENTITY, GL_QUADS, 256, false, false, rendertype$state);
    }

    public static RenderState.TexturingState getEntityGlint(float glintScale, float glintRotate) {
        return new RenderState.TexturingState("cylinder_glint_texturing_" + glintScale + "_" + glintRotate,
                () -> {
                    RenderSystem.matrixMode(GL_TEXTURE);
                    RenderSystem.pushMatrix();
                    RenderSystem.loadIdentity();
                    long i = Util.getMillis() * 8L;
                    float f = (float) (i % 110000L) / 110000.0F;
                    float f1 = (float) (i % 30000L) / 30000.0F;
                    RenderSystem.translatef(f, f1, 0.0F);
                    RenderSystem.rotatef(glintRotate, 0.0F, 1.0F, 0.0F);
                    RenderSystem.scalef(glintScale, glintScale, glintScale);
                    RenderSystem.matrixMode(GL_MODELVIEW);
                }
                , () -> {
            RenderSystem.matrixMode(GL_TEXTURE);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(GL_MODELVIEW);
        });
    }

    public static RenderState.TexturingState pointState(float size) {
        return new RenderState.TexturingState(MagickCore.MOD_ID+"point_size" + size,
                () -> {
                    if (size != 1) {
                        if (size > 1) {
                            GL11.glPointSize(size);
                        } else {
                            GL11.glPointSize(Math.max(2.5F, (float)Minecraft.getInstance().getWindow().getWidth() / 1920.0F * 2.5F));
                        }
                    }

                }, () -> {
            if (size != 1) {
                GL11.glPointSize(1.0F);
            }
        });
    }

    public static RenderType getTexedEntityGlint(ResourceLocation locationIn, float glintScale, float glintRotate) {
        RenderType.State rendertype$state =
                RenderType.State.builder().setTextureState(new RenderState.TextureState(locationIn, false, false))
                        .setTransparencyState(LIGHTNING_TRANSPARENCY).setShadeModelState(SHADE_ENABLED).setOutputState(TRANSLUCENT_TARGET)
                        .setAlphaState(DEFAULT_ALPHA).setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED)
                        .setLightmapState(LIGHTMAP_ENABLED).setTexturingState(getEntityGlint(glintScale, glintRotate)).createCompositeState(true);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Entity_Glint_" + glintScale + "_" + glintRotate, DefaultVertexFormats.NEW_ENTITY, GL_QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getLayerEntityGlint(ResourceLocation locationIn, float glintScale, float glintRotate) {
        RenderType.State rendertype$state =
                RenderType.State.builder().setTextureState(new RenderState.TextureState(locationIn, false, false))
                        .setTransparencyState(GLINT_TRANSPARENCY).setDepthTestState(DEPTH_EQUAL).setOutputState(TRANSLUCENT_TARGET)
                        .setAlphaState(DEFAULT_ALPHA).setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED)
                        .setLightmapState(LIGHTMAP_ENABLED).setTexturingState(getEntityGlint(glintScale, glintRotate)).createCompositeState(true);
        return RenderType.create(MagickCore.MOD_ID + ":Layer_Entity_Glint_" + glintScale + "_" + glintRotate, DefaultVertexFormats.NEW_ENTITY, GL_QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getLayerEntityGlintSolid(ResourceLocation locationIn, float glintScale, float glintRotate) {
        RenderType.State rendertype$state =
                RenderType.State.builder().setTextureState(new RenderState.TextureState(locationIn, false, false))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDepthTestState(DEPTH_EQUAL).setOutputState(TRANSLUCENT_TARGET)
                        .setAlphaState(DEFAULT_ALPHA).setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED)
                        .setLightmapState(LIGHTMAP_ENABLED).setTexturingState(getEntityGlint(glintScale, glintRotate)).createCompositeState(true);
        return RenderType.create(MagickCore.MOD_ID + ":Layer_Entity_Glint_Solid_" + glintScale + "_" + glintRotate, DefaultVertexFormats.NEW_ENTITY, GL_QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getLayerEntityGlint(ResourceLocation locationIn) {
        RenderType.State rendertype$state =
                RenderType.State.builder().setTextureState(new RenderState.TextureState(locationIn, false, false))
                        .setTransparencyState(GLINT_TRANSPARENCY).setDepthTestState(DEPTH_EQUAL).setOutputState(TRANSLUCENT_TARGET)
                        .setAlphaState(DEFAULT_ALPHA).setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED)
                        .setLightmapState(LIGHTMAP_ENABLED).setTexturingState(getEntityGlint(0.32f, 10f)).createCompositeState(true);
        return RenderType.create(MagickCore.MOD_ID + ":Layer_Entity_Glint_0.32_10", DefaultVertexFormats.NEW_ENTITY, GL_QUADS, 256, false, false, rendertype$state);
    }

    public static RenderState.TexturingState getLaserGlint(float glintScale) {
        return new RenderState.TexturingState("laser_glint_texturing_" + glintScale,
                () -> {
                    RenderSystem.matrixMode(GL_TEXTURE);
                    RenderSystem.pushMatrix();
                    RenderSystem.loadIdentity();
                    long i = Util.getMillis() * 8L;
                    float f1 = (i % (3000L * glintScale)) / (3000.0F * glintScale);
                    RenderSystem.translatef(0.0F, f1, 0.0F);
                    RenderSystem.scalef(1.0f, glintScale, 1.0f);

                    RenderSystem.matrixMode(GL_MODELVIEW);
                }
            , () -> {
            RenderSystem.matrixMode(GL_TEXTURE);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(GL_MODELVIEW);
        });
    }

    public static RenderType getTexedLaserGlint(ResourceLocation locationIn, float glintScale) {
        RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(locationIn, false, false)).setTransparencyState(LIGHTNING_TRANSPARENCY).setOutputState(TRANSLUCENT_TARGET).setShadeModelState(SHADE_ENABLED).setAlphaState(DEFAULT_ALPHA).setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED).setLightmapState(LIGHTMAP_ENABLED).setTexturingState(getLaserGlint(glintScale)).createCompositeState(true);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Laser_Glint_" + glintScale, DefaultVertexFormats.NEW_ENTITY, GL_QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexedLaser(ResourceLocation locationIn) {
        RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(locationIn, false, false)).setTransparencyState(LIGHTNING_TRANSPARENCY).setOutputState(TRANSLUCENT_TARGET).setShadeModelState(SHADE_ENABLED).setAlphaState(DEFAULT_ALPHA).setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED).setLightmapState(LIGHTMAP_ENABLED).createCompositeState(true);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Laser", DefaultVertexFormats.NEW_ENTITY, GL_QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexedCylinderGlow(ResourceLocation locationIn) {
        RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(locationIn, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(TRANSLUCENT_TARGET).setShadeModelState(SHADE_ENABLED).setAlphaState(DEFAULT_ALPHA).setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED).setLightmapState(LIGHTMAP_ENABLED).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Cylinder_Glow", DefaultVertexFormats.NEW_ENTITY, GL_TRIANGLE_STRIP, 256, false, false, rendertype$state);
    }

    public static RenderState.TexturingState getCylinderGlint(float glintScale, float glintRotate) {
        return new RenderState.TexturingState("cylinder_glint_texturing_" + glintScale + "_" + glintRotate,
                () -> {
                    RenderSystem.matrixMode(GL_TEXTURE);
                    RenderSystem.pushMatrix();
                    RenderSystem.loadIdentity();
                    long i = Util.getMillis() * 8L;
                    float f1 = (float) (i % 30000L) / 30000.0F;
                    RenderSystem.translatef(0.0f, f1, 0.0F);
                    RenderSystem.rotatef(glintRotate, 0.0F, 1.0F, 0.0F);
                    RenderSystem.scalef(1.0f, glintScale, 1.0f);
                    RenderSystem.matrixMode(GL_MODELVIEW);
                }
                , () -> {
            RenderSystem.matrixMode(GL_TEXTURE);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(GL_MODELVIEW);
        });
    }

    public static RenderType getTexedCylinderGlint(ResourceLocation locationIn, float glintScale, float glintRotate) {
        RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(locationIn, false, false))
                        .setTransparencyState(LIGHTNING_TRANSPARENCY).setShadeModelState(SHADE_ENABLED).setAlphaState(DEFAULT_ALPHA)
                .setOutputState(TRANSLUCENT_TARGET).setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED).setLightmapState(LIGHTMAP_ENABLED)
                        .setTexturingState(getCylinderGlint(glintScale, glintRotate)).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Cylinder_Glint_" + glintScale + "_" + glintRotate, DefaultVertexFormats.NEW_ENTITY, GL_TRIANGLE_STRIP, 256, false, false, rendertype$state);
    }

    public static RenderType getTexedSphereGlow(ResourceLocation locationIn, float glintScale, float glintRotate) {
        RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(locationIn, false, false)).setTransparencyState(LIGHTNING_TRANSPARENCY).setOutputState(TRANSLUCENT_TARGET).setShadeModelState(SHADE_ENABLED).setAlphaState(DEFAULT_ALPHA).setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED).setLightmapState(LIGHTMAP_ENABLED).setTexturingState(getEntityGlint(glintScale, glintRotate)).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Sphere_Glow_" + glintScale + "_" + glintRotate, DefaultVertexFormats.NEW_ENTITY, GL_QUAD_STRIP, 256, false, false, rendertype$state);
    }

    public static RenderType getTexedSphereDepth(ResourceLocation locationIn, float glintScale, float glintRotate) {
        RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(locationIn, false, false)).setTransparencyState(DEPTH_LIGHTNING_TRANSPARENCY).setOutputState(TRANSLUCENT_TARGET).setShadeModelState(SHADE_ENABLED).setAlphaState(DEFAULT_ALPHA).setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED).setLightmapState(LIGHTMAP_ENABLED).setTexturingState(getEntityGlint(glintScale, glintRotate)).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Sphere_Depth_" + glintScale + "_" + glintRotate, DefaultVertexFormats.NEW_ENTITY, GL_QUAD_STRIP, 256, false, false, rendertype$state);
    }

    public static RenderType getTexedSphere(ResourceLocation locationIn) {
        RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new RenderState.TextureState(locationIn, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(TRANSLUCENT_TARGET).setShadeModelState(SHADE_ENABLED).setAlphaState(DEFAULT_ALPHA).setOverlayState(OVERLAY_ENABLED).setTexturingState(getEntityGlint(1, 0)).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Sphere", DefaultVertexFormats.NEW_ENTITY, GL_QUAD_STRIP, 256, false, false, rendertype$state);
    }

    public static RenderType getLineLoopGlow(double width) {
        RenderType.State rendertype$state = RenderType.State.builder().setTransparencyState(ADDITIVE_TRANSPARENCY).setWriteMaskState(COLOR_DEPTH_WRITE).setOutputState(TRANSLUCENT_TARGET).setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED).setLightmapState(LIGHTMAP_ENABLED).setLineState(new RenderState.LineState(OptionalDouble.of(width))).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":LINE_LOOP_" + width, DefaultVertexFormats.NEW_ENTITY, GL_LINE_LOOP, 256, false, false, rendertype$state);
    }

    public static RenderType getLineStripGlow(double width) {
        RenderType.State rendertype$state = RenderType.State.builder().setTransparencyState(ADDITIVE_TRANSPARENCY).setWriteMaskState(COLOR_DEPTH_WRITE).setOutputState(TRANSLUCENT_TARGET).setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED).setLightmapState(LIGHTMAP_ENABLED).setLineState(new RenderState.LineState(OptionalDouble.of(width))).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":LINES_STRIP_" + width, DefaultVertexFormats.NEW_ENTITY, GL_LINE_STRIP, 256, false, false, rendertype$state);
    }

    public static RenderType getLineStripPC(double width) {
        RenderType.State rendertype$state = RenderType.State.builder().setTransparencyState(ADDITIVE_TRANSPARENCY).setWriteMaskState(COLOR_DEPTH_WRITE).setOutputState(TRANSLUCENT_TARGET).setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED).setLightmapState(LIGHTMAP_ENABLED).setLineState(new RenderState.LineState(OptionalDouble.of(width))).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":LINES_STRIP_PC_" + width, DefaultVertexFormats.POSITION_COLOR, 1, 256, false, false, rendertype$state);
    }
    public static RenderType getLinesGlow(double width) {
        RenderType.State rendertype$state = RenderType.State.builder().setTransparencyState(ADDITIVE_TRANSPARENCY).setWriteMaskState(COLOR_DEPTH_WRITE).setOutputState(TRANSLUCENT_TARGET).setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED).setLightmapState(LIGHTMAP_ENABLED).setLineState(new RenderState.LineState(OptionalDouble.of(width))).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":LINES_" + width, DefaultVertexFormats.NEW_ENTITY, GL_LINES, 256, false, false, rendertype$state);
    }

    public static RenderType getPointsGlow(float width) {
        RenderType.State rendertype$state = RenderType.State.builder().setTransparencyState(ADDITIVE_TRANSPARENCY).setWriteMaskState(COLOR_DEPTH_WRITE).setOutputState(TRANSLUCENT_TARGET).setDiffuseLightingState(DIFFUSE_LIGHTING_ENABLED).setLightmapState(LIGHTMAP_ENABLED).setTexturingState(pointState(width)).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Points_" + width, DefaultVertexFormats.NEW_ENTITY, GL_POINTS, 256, false, false, rendertype$state);
    }

    public static RenderType getDistortion(ResourceLocation locationIn) {
        RenderType.State rendertype$state = RenderType.State.builder().setTransparencyState(TRANSLUCENT_TRANSPARENCY).setTextureState(new RenderState.TextureState(locationIn, false, false)).setOutputState(TRANSLUCENT_TARGET).setCullState(CULL_DISABLED).setTexturingState(getEntityGlint(1, 0)).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Distortion", DefaultVertexFormats.POSITION_COLOR_TEX, GL_QUAD_STRIP, 256, rendertype$state);
    }

    public static final ResourceLocation TAKEN_LAYER = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/taken_layer.png");
    public static final ResourceLocation EMPTY_TEXTURE = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cycle.png");
    public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/enchanted_item_glint.png");
    public static final ResourceLocation ripple_4 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/ripple/ripple_4.png");
    public static final ResourceLocation ripple_2 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/ripple/ripple_2.png");
    public static final ResourceLocation ripple_5 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/ripple/ripple_5.png");
    public static final ResourceLocation SPHERE_ROTATE = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/sphere_rotate.png");
    public static final ResourceLocation CYLINDER_ROTATE = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_rotate.png");

    public static final float[][] vertex_list = {
            {-0.5f, -0.5f, -0.5f},
            {0.5f, -0.5f, -0.5f},
            {-0.5f,  0.5f, -0.5f},
            {0.5f,  0.5f, -0.5f},
            {-0.5f, -0.5f,  0.5f},
            {0.5f, -0.5f,  0.5f},
            {-0.5f,  0.5f,  0.5f},
            {0.5f,  0.5f,  0.5f}};
    public static final int[][] index_list = {
            {0, 2, 3, 1},
            {0, 4, 6, 2},
            {0, 1, 5, 4},
            {4, 5, 7, 6},
            {1, 3, 7, 5},
            {2, 6, 7, 3}};

    public static final HashMap<Object, Queue<VertexBuffer>> GL_LIST_INDEX = new HashMap<>();

    public static final Vector3d[] QuadVector = new Vector3d[]{new Vector3d(-1.0, -1.0, 0.0), new Vector3d(-1.0F, 1.0F, 0.0F), new Vector3d(1.0F, 1.0F, 0.0F), new Vector3d(1.0F, -1.0F, 0.0F)};

    public static void renderParticle(BufferContext pack, RenderContext renderContext) {
        renderParticle(pack, renderContext, EmptyVertexContext);
    }

    public static void callParticleVertex(BufferContext context, RenderContext renderContext) {
        MatrixStack matrixStack = context.matrixStack;
        Matrix4f matrix4f = matrixStack.last().pose();
        BufferBuilder buffer = context.buffer;
        Color color = renderContext.color;
        float alpha = renderContext.alpha;
        int lightmap = renderContext.packedLightIn;
        Vector3d[] quad = RenderHelper.QuadVector;

        setup(context);
        begin(context);
        buffer.vertex(matrix4f, (float) quad[0].x, (float) quad[0].y, (float) quad[0].z).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 1.0f)
                .uv2(lightmap).endVertex();
        buffer.vertex(matrix4f, (float) quad[1].x, (float) quad[1].y, (float) quad[1].z).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 0.0f)
                .uv2(lightmap).endVertex();
        buffer.vertex(matrix4f, (float) quad[2].x, (float) quad[2].y, (float) quad[2].z).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 0.0f)
                .uv2(lightmap).endVertex();
        buffer.vertex(matrix4f, (float) quad[3].x, (float) quad[3].y, (float) quad[3].z).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 1.0f)
                .uv2(lightmap).endVertex();
        finish(context);
        end(context);
    }

    public static void callQuadVertex(BufferContext context, RenderContext renderContext) {
        MatrixStack matrixStack = context.matrixStack;
        Matrix4f matrix4f = matrixStack.last().pose();
        BufferBuilder buffer = context.buffer;
        RenderType type = context.type;
        Color color = renderContext.color;
        float alpha = renderContext.alpha;
        int lightmap = renderContext.packedLightIn;
        String hash = "QUAD_VERTEX" + renderContext.hashCode();
        Vector3d[] quad = RenderHelper.QuadVector;

        setup(context);
        begin(context);
        buffer.vertex(matrix4f, (float) quad[0].x, (float) quad[0].y, (float) quad[0].z).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 1.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal((float) quad[0].x, (float) quad[0].y, (float) quad[0].z).endVertex();
        buffer.vertex(matrix4f, (float) quad[1].x, (float) quad[1].y, (float) quad[1].z).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 0.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal((float) quad[1].x, (float) quad[1].y, (float) quad[1].z).endVertex();
        buffer.vertex(matrix4f, (float) quad[2].x, (float) quad[2].y, (float) quad[2].z).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 0.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal((float) quad[2].x, (float) quad[2].y, (float) quad[2].z).endVertex();
        buffer.vertex(matrix4f, (float) quad[3].x, (float) quad[3].y, (float) quad[3].z).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 1.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal((float) quad[3].x, (float) quad[3].y, (float) quad[3].z).endVertex();
        finish(context);
        end(context);

        /*
        if(!GL_LIST_INDEX.containsKey(hash)) {
            Queue<VertexBuffer> vertexBuffers = Queues.newArrayDeque();
            VertexBuffer vertexBuffer = new VertexBuffer(DefaultVertexFormats.ENTITY);
            begin(context);

            buffer.finishDrawing();
            vertexBuffer.upload(buffer);
            vertexBuffers.add(vertexBuffer);
            GL_LIST_INDEX.put(hash, vertexBuffers);
        } else {
            setup(context);
            type.setupRenderState();
            Queue<VertexBuffer> vertexBuffers = GL_LIST_INDEX.get(hash);
            for (VertexBuffer vertexBuffer : vertexBuffers) {
                vertexBuffer.bindBuffer();
                DefaultVertexFormats.ENTITY.setupBufferState(0);
                vertexBuffer.draw(matrixStack.getLast().getMatrix(), GL_QUADS);
                VertexBuffer.unbindBuffer();
                DefaultVertexFormats.ENTITY.clearBufferState();
            }
            type.clearRenderState();
            end(context);
        }

         */
    }

    public static class CylinderContext {
        final float baseRadius;
        final float midRadius;
        final float midFactor;
        final float height;
        final int stacks;
        final float alpha;
        final float midAlpha;
        final float alphaFactor;
        final Color color;
        public CylinderContext(float baseRadius, float midRadius, float midFactor, float height, int stacks, float alpha, float midAlpha, float alphaFactor, Color color) {
            this.baseRadius = baseRadius;
            this.midRadius = midRadius;
            this.midFactor = midFactor;
            this.height = height;
            this.stacks = stacks;
            this.alpha = isRenderingWorld() && isRenderingShader() ? alpha * 0.1f : alpha;;
            this.midAlpha = isRenderingWorld() && isRenderingShader() ? midAlpha * 0.1f : midAlpha;;
            this.alphaFactor = alphaFactor;
            this.color = color;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CylinderContext)) return false;
            CylinderContext that = (CylinderContext) o;
            return Float.compare(that.baseRadius, baseRadius) == 0 && Float.compare(that.midRadius, midRadius) == 0 && Float.compare(that.midFactor, midFactor) == 0 && Float.compare(that.height, height) == 0 && stacks == that.stacks && Float.compare(that.alpha, alpha) == 0 && Float.compare(that.midAlpha, midAlpha) == 0 && Float.compare(that.alphaFactor, alphaFactor) == 0 && color.equals(that.color);
        }

        @Override
        public int hashCode() {
            return Objects.hash("CylinderContext", baseRadius, midRadius, midFactor, height, stacks, alpha, midAlpha, alphaFactor, color);
        }

        @Override
        public String toString() {
            return "CylinderContext{" +
                    "baseRadius=" + baseRadius +
                    ", midRadius=" + midRadius +
                    ", midFactor=" + midFactor +
                    ", height=" + height +
                    ", stacks=" + stacks +
                    ", alpha=" + alpha +
                    ", midAlpha=" + midAlpha +
                    ", alphaFactor=" + alphaFactor +
                    ", color=" + color +
                    '}';
        }
    }

    public static class RenderContext {
        public float alpha;
        public final Color color;
        int packedLightIn = halfLight;
        public RenderContext(float alpha, Color color, int packedLightIn) {
            this.alpha = isRenderingWorld() && isRenderingShader() ? alpha * 0.1f : alpha;
            this.color = color;
            this.packedLightIn = packedLightIn;
        }

        public RenderContext(float alpha, Color color) {
            this.alpha = isRenderingWorld() && isRenderingShader() ? alpha * 0.1f : alpha;
            this.color = color;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RenderContext)) return false;
            RenderContext that = (RenderContext) o;
            return Float.compare(that.alpha, alpha) == 0 && packedLightIn == that.packedLightIn && color.equals(that.color);
        }

        @Override
        public int hashCode() {
            return Objects.hash("RenderContext", alpha, color, packedLightIn);
        }
    }

    public static class VertexContext {
        final VectorHitReaction[] hitReaction;

        final boolean shake;
        final String shakeName;
        final float limit;

        public VertexContext(VectorHitReaction[] hitReaction, boolean shake, String shakeName, float limit) {
            this.hitReaction = hitReaction;
            this.shake = shake;
            this.shakeName = shakeName;
            this.limit = limit;
        }

        public VertexContext(boolean shake, String shakeName, float limit) {
            this.hitReaction = null;
            this.shake = shake;
            this.shakeName = shakeName;
            this.limit = limit;
        }

        public VertexContext(VectorHitReaction[] hitReaction, float limit) {
            this.hitReaction = hitReaction;
            this.shake = false;
            this.shakeName = "";
            this.limit = limit;
        }
    }

    public static void renderDistortion(BufferContext pack, RenderContext renderContext, VertexContext vertexContext, int stacks) {
        if (stacks <= 2)
            stacks = 2;

        if (stacks % 2 != 0)
            stacks++;

        if(vertexContext.hitReaction.length == 0) {
            renderDistortion(pack, renderContext, stacks);
            return;
        }
        pack.matrixStack.pushPose();
        Matrix4f matrix4f = pack.matrixStack.last().pose();
        IVertexBuilder buffer = pack.buffer;
        Queue<Queue<VertexAttribute>> cylinderQueue = drawSphere(stacks, renderContext, vertexContext);

        setup(pack);
        Iterator<Queue<VertexAttribute>> it = cylinderQueue.iterator();
        while (it.hasNext()) {
            Iterator<VertexAttribute> innerIt = it.next().iterator();
            pack.matrixStack.pushPose();
            begin(pack);
            while (innerIt.hasNext()) {
                VertexAttribute vertex = innerIt.next();
                buffer.vertex(matrix4f, vertex.posX, vertex.posY, vertex.posZ).color(vertex.color.r(), vertex.color.g(), vertex.color.b(), vertex.alpha).uv(vertex.texU, vertex.texV).endVertex();
            }
            finish(pack);
            pack.matrixStack.popPose();
        }
        end(pack);
        pack.matrixStack.popPose();
    }

    public static void renderDistortion(BufferContext pack, RenderContext renderContext, int stacks) {
        pack.matrixStack.pushPose();
        Matrix4f matrix4f = pack.matrixStack.last().pose();
        IVertexBuilder buffer = pack.buffer;
        String hash =stacks +"Distortion"+renderContext.hashCode();

        if(!GL_LIST_INDEX.containsKey(hash)) {
            Queue<Queue<VertexAttribute>> cylinderQueue = drawSphere(stacks, renderContext, EmptyVertexContext);
            setup(pack);
            begin(pack);
            Queue<VertexBuffer> vertexBuffers = Queues.newArrayDeque();
            Iterator<Queue<VertexAttribute>> it = cylinderQueue.iterator();
            while (it.hasNext()) {
                Iterator<VertexAttribute> innerIt = it.next().iterator();
                begin(pack);
                VertexBuffer vertexBuffer = new VertexBuffer(DefaultVertexFormats.POSITION_COLOR_TEX);
                while (innerIt.hasNext()) {
                    VertexAttribute vertex = innerIt.next();
                    buffer.vertex(vertex.posX, vertex.posY, vertex.posZ).color(vertex.color.r(), vertex.color.g(), vertex.color.b(), vertex.alpha).uv(vertex.texU, vertex.texV).endVertex();
                }
                pack.buffer.end();
                vertexBuffer.upload(pack.buffer);
                vertexBuffers.add(vertexBuffer);
            }

            GL_LIST_INDEX.put(hash, vertexBuffers);
            end(pack);
        } else {
            setup(pack);
            Queue<VertexBuffer> vertexBuffers = GL_LIST_INDEX.get(hash);
            pack.type.setupRenderState();
            for (VertexBuffer vertexBuffer : vertexBuffers) {
                vertexBuffer.bind();
                DefaultVertexFormats.POSITION_COLOR_TEX.setupBufferState(0);
                vertexBuffer.draw(matrix4f, pack.type.mode());
                VertexBuffer.unbind();
                DefaultVertexFormats.POSITION_COLOR_TEX.clearBufferState();
            }
            pack.type.clearRenderState();
            end(pack);
        }
        pack.matrixStack.popPose();
    }

    public static void renderCylinder(BufferContext pack, CylinderContext context) {
        pack.matrixStack.pushPose();
        Matrix4f matrix4f = pack.matrixStack.last().pose();
        IVertexBuilder buffer = pack.buffer;
        if(!GL_LIST_INDEX.containsKey(context)) {
            Queue<Queue<VertexAttribute>> cylinderQueue = drawCylinder(context, null, 0);
            setup(pack);
            Queue<VertexBuffer> vertexBuffers = Queues.newArrayDeque();
            Iterator<Queue<VertexAttribute>> it = cylinderQueue.iterator();
            while (it.hasNext()) {
                Iterator<VertexAttribute> innerIt = it.next().iterator();
                begin(pack);
                VertexBuffer vertexBuffer = new VertexBuffer(DefaultVertexFormats.NEW_ENTITY);
                while (innerIt.hasNext()) {
                    VertexAttribute vertex = innerIt.next();
                    buffer.vertex(vertex.posX, vertex.posY, vertex.posZ).color(vertex.color.r(), vertex.color.g(), vertex.color.b(), vertex.alpha).uv(vertex.texU, vertex.texV)
                            .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(RenderHelper.renderLight).normal(vertex.normalX, vertex.normalY, vertex.normalZ).endVertex();
                }
                pack.buffer.end();
                vertexBuffer.upload(pack.buffer);
                vertexBuffers.add(vertexBuffer);
            }
            GL_LIST_INDEX.put(context, vertexBuffers);
            end(pack);
        } else {
            setup(pack);
            Queue<VertexBuffer> vertexBuffers = GL_LIST_INDEX.get(context);
            pack.type.setupRenderState();
            for (VertexBuffer vertexBuffer : vertexBuffers) {
                vertexBuffer.bind();
                DefaultVertexFormats.NEW_ENTITY.setupBufferState(0);
                vertexBuffer.draw(matrix4f, pack.type.mode());
                VertexBuffer.unbind();
                DefaultVertexFormats.NEW_ENTITY.clearBufferState();
            }
            pack.type.clearRenderState();
            end(pack);
        }
        pack.matrixStack.popPose();
    }

    public static void renderCylinder(BufferContext pack, CylinderContext context, VectorHitReaction[] hitReaction, float limit) {
        if(hitReaction.length == 0) {
            renderCylinder(pack, context);
            return;
        }
        pack.matrixStack.pushPose();
        Matrix4f matrix4f = pack.matrixStack.last().pose();
        IVertexBuilder buffer = pack.buffer;
        Queue<Queue<VertexAttribute>> cylinderQueue = drawCylinder(context, hitReaction, limit);

        setup(pack);
        Iterator<Queue<VertexAttribute>> it = cylinderQueue.iterator();
        while (it.hasNext()) {
            Iterator<VertexAttribute> innerIt = it.next().iterator();
            begin(pack);
            while (innerIt.hasNext()) {
                VertexAttribute vertex = innerIt.next();
                buffer.vertex(matrix4f, vertex.posX, vertex.posY, vertex.posZ).color(vertex.color.r(), vertex.color.g(), vertex.color.b(), vertex.alpha).uv(vertex.texU, vertex.texV)
                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(RenderHelper.renderLight).normal(vertex.normalX, vertex.normalY, vertex.normalZ).endVertex();
            }
            finish(pack);
        }
        end(pack);
        pack.matrixStack.popPose();
    }

    public static void renderCylinder(BufferContext pack, Queue<Queue<VertexAttribute>> vertexQueue) {
        pack.matrixStack.pushPose();
        Matrix4f matrix4f = pack.matrixStack.last().pose();
        IVertexBuilder buffer = pack.buffer;

        setup(pack);
        Iterator<Queue<VertexAttribute>> it = vertexQueue.iterator();
        while (it.hasNext()) {
            Iterator<VertexAttribute> innerIt = it.next().iterator();
            pack.matrixStack.pushPose();
            begin(pack);
            while (innerIt.hasNext()) {
                VertexAttribute vertex = innerIt.next();
                buffer.vertex(matrix4f, vertex.posX, vertex.posY, vertex.posZ).color(vertex.color.r(), vertex.color.g(), vertex.color.b(), vertex.alpha).uv(vertex.texU, vertex.texV)
                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(RenderHelper.renderLight).normal(vertex.normalX, vertex.normalY, vertex.normalZ).endVertex();
            }
            finish(pack);
            pack.matrixStack.popPose();
        }
        end(pack);
        pack.matrixStack.popPose();
    }

    public static Queue<Queue<VertexAttribute>> drawCylinder(CylinderContext context, VectorHitReaction[] hitReaction, float limit) {
        double majorStep = context.height / context.stacks;
        double minorStep = 2.0 * Math.PI / context.stacks;
        int i, j;

        Queue<Queue<VertexAttribute>> queues = Queues.newArrayDeque();
        for (i = 0; i < context.stacks; ++i) {
            float z0 = (float) (0.5 * context.height - i * majorStep);
            float z1 = (float) (z0 - majorStep);
            Queue<VertexAttribute> queue = Queues.newArrayDeque();
            for (j = 0; j <= context.stacks; ++j) {
                double a = j * minorStep;
                float min = Math.min(1f, Math.abs(z0) * 2 / context.height);
                float min1 = Math.min(1f, Math.abs(z1) * 2 / context.height);

                float z0MidFactor = (float) Math.pow(min, context.alphaFactor);
                float z0EdgeFactor = (1 - z0MidFactor);
                float z0Alpha = z0MidFactor * context.alpha + z0EdgeFactor * context.midAlpha;


                float z1MidFactor = (float) Math.pow(min1, context.alphaFactor);
                float z1EdgeFactor = (1 - z1MidFactor);
                float z1Alpha = z1MidFactor * context.alpha + z1EdgeFactor * context.midAlpha;

                float z0Radius = (float) Math.pow(min, context.midFactor);
                z0Radius = z0Radius * context.baseRadius + (1 - z0Radius) * context.midRadius;

                float z1Radius = (float) Math.pow(min1, context.midFactor);
                z1Radius = z1Radius * context.baseRadius + (1 - z1Radius) * context.midRadius;

                float z0X = (float) (z0Radius * Math.cos(a));
                float z0Y = (float) (z0Radius * Math.sin(a));

                float z1X = (float) (z1Radius * Math.cos(a));
                float z1Y = (float) (z1Radius * Math.sin(a));

                VertexAttribute vertex0 = calculateVertex(z0X, z0, z0Y, j / (float) context.stacks, i / (float) context.stacks, new RenderContext(z0Alpha, context.color), hitReaction, limit);
                VertexAttribute vertex1 = calculateVertex(z1X, z1, z1Y, j / (float) context.stacks, (i + 1) / (float) context.stacks, new RenderContext(z1Alpha, context.color), hitReaction, limit);
                queue.add(vertex0);
                queue.add(vertex1);
            }
            queues.add(queue);
        }
        return queues;
    }

    public static void renderLaserParticle(BufferContext pack, RenderContext renderContext, VertexContext vertexContext, float length) {
        Color color = renderContext.color;
        boolean shake = vertexContext.shake;
        String shakeName = vertexContext.shakeName;
        float limit = vertexContext.limit;
        float alpha = renderContext.alpha;

        pack.matrixStack.pushPose();
        Matrix4f matrix4f = pack.matrixStack.last().pose();
        IVertexBuilder buffer = pack.buffer;
        //matrixStackIn.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
        setup(pack);
        begin(pack);
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

            buffer.vertex(matrix4f, (float) V0.x, (float) V0.y, (float) V0.z).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal((float) V0.x, (float) V0.y, (float) V0.z).endVertex();
            buffer.vertex(matrix4f, (float) V1.x, (float) V1.y, (float) V1.z).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal((float) V1.x, (float) V1.y, (float) V1.z).endVertex();
            buffer.vertex(matrix4f, (float) V2.x, (float) V2.y, (float) V2.z).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal((float) V2.x, (float) V2.y, (float) V2.z).endVertex();
            buffer.vertex(matrix4f, (float) V3.x, (float) V3.y, (float) V3.z).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal((float) V3.x, (float) V3.y, (float) V3.z).endVertex();
        } else {
            float center = length * 0.6f;
            float side = (length - center) * 0.5f;
            float side1 = center + side;
            buffer.vertex(matrix4f, -1.0F, 0.0F, 0.0F).color(color.r(), color.g(), color.b(), 0).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1.0F, 0.0F, 0.0F).endVertex();
            buffer.vertex(matrix4f, -1.0F, side, 0.0F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1.0F, side, 0.0F).endVertex();
            buffer.vertex(matrix4f, 1.0F, side, 0.0F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1.0F, side, 0.0F).endVertex();
            buffer.vertex(matrix4f, 1.0F, 0.0F, 0.0F).color(color.r(), color.g(), color.b(), 0).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1.0F, 0.0F, 0.0F).endVertex();

            buffer.vertex(matrix4f, 0.0F, 0.0F, -1.0F).color(color.r(), color.g(), color.b(), 0).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, 0.0F, -1.0F).endVertex();
            buffer.vertex(matrix4f, 0.0F, side, -1.0F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, side, -1.0F).endVertex();
            buffer.vertex(matrix4f, 0.0F, side, 1.0F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, side, 1.0F).endVertex();
            buffer.vertex(matrix4f, 0.0F, 0.0F, 1.0F).color(color.r(), color.g(), color.b(), 0).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, 0.0F, 1.0F).endVertex();

            buffer.vertex(matrix4f, -1.0F, side, 0.0F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1.0F, side, 0.0F).endVertex();
            buffer.vertex(matrix4f, -1.0F, side1, 0.0F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1.0F, side1, 0.0F).endVertex();
            buffer.vertex(matrix4f, 1.0F, side1, 0.0F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1.0F, side1, 0.0F).endVertex();
            buffer.vertex(matrix4f, 1.0F, side, 0.0F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1.0F, side, 0.0F).endVertex();

            buffer.vertex(matrix4f, 0.0F, side, -1.0F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, side, -1.0F).endVertex();
            buffer.vertex(matrix4f, 0.0F, side1, -1.0F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, side1, -1.0F).endVertex();
            buffer.vertex(matrix4f, 0.0F, side1, 1.0F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, side1, 1.0F).endVertex();
            buffer.vertex(matrix4f, 0.0F, side, 1.0F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, side, 1.0F).endVertex();

            buffer.vertex(matrix4f, -1.0F, side1, 0.0F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1.0F, side1, 0.0F).endVertex();
            buffer.vertex(matrix4f, -1.0F, length, 0.0F).color(color.r(), color.g(), color.b(), 0).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1.0F, length, 0.0F).endVertex();
            buffer.vertex(matrix4f, 1.0F, length, 0.0F).color(color.r(), color.g(), color.b(), 0).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1.0F, length, 0.0F).endVertex();
            buffer.vertex(matrix4f, 1.0F, side1, 0.0F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1.0F, side1, 0.0F).endVertex();

            buffer.vertex(matrix4f, 0.0F, side1, -1.0F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, side1, -1.0F).endVertex();
            buffer.vertex(matrix4f, 0.0F, length, -1.0F).color(color.r(), color.g(), color.b(), 0).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, length, -1.0F).endVertex();
            buffer.vertex(matrix4f, 0.0F, length, 1.0F).color(color.r(), color.g(), color.b(), 0).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, length, 1.0F).endVertex();
            buffer.vertex(matrix4f, 0.0F, side1, 1.0F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.0F, side1, 1.0F).endVertex();
        }
        finish(pack);
        end(pack);
        pack.matrixStack.popPose();
    }

    public static void renderLaserTop(BufferContext pack, RenderContext renderContext, float length) {
        Color color = renderContext.color;
        float alpha = renderContext.alpha;

        pack.matrixStack.pushPose();
        Matrix4f matrix4f = pack.matrixStack.last().pose();
        IVertexBuilder buffer = pack.buffer;
        setup(pack);
        begin(pack);
        int light = renderLight;
        float center = length * 0.9f;
        float side = (length - center) * 0.5f;
        float side1 = center + side;
        buffer.vertex(matrix4f, -1.0F, side1, 0.0F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1.0F, side1, 0.0F).endVertex();
        buffer.vertex(matrix4f, -1.0F, length, 0.0F).color(color.r(), color.g(), color.b(), 0).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1.0F, length, 0.0F).endVertex();
        buffer.vertex(matrix4f, 1.0F, length, 0.0F).color(color.r(), color.g(), color.b(), 0).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1.0F, length, 0.0F).endVertex();
        buffer.vertex(matrix4f, 1.0F, side1, 0.0F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1.0F, side1, 0.0F).endVertex();

        buffer.vertex(matrix4f, 0.5F, side1, -0.866025F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.5F, side1, -0.866025F).endVertex();
        buffer.vertex(matrix4f, 0.5F, length, -0.866025F).color(color.r(), color.g(), color.b(), 0).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.5F, length, -0.866025F).endVertex();
        buffer.vertex(matrix4f, -0.5F, length, 0.866025F).color(color.r(), color.g(), color.b(), 0).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-0.5F, length, 0.866025F).endVertex();
        buffer.vertex(matrix4f, -0.5F, side1, 0.866025F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-0.5F, side1, 0.866025F).endVertex();

        buffer.vertex(matrix4f, 0.5F, side1, 0.866025F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.5F, side1, 0.866025F).endVertex();
        buffer.vertex(matrix4f, 0.5F, length, 0.866025F).color(color.r(), color.g(), color.b(), 0).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.5F, length, 0.866025F).endVertex();
        buffer.vertex(matrix4f, -0.5F, length, -0.866025F).color(color.r(), color.g(), color.b(), 0).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-0.5F, length, -0.866025F).endVertex();
        buffer.vertex(matrix4f, -0.5F, side1, -0.866025F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-0.5F, side1, -0.866025F).endVertex();
        finish(pack);
        end(pack);
        pack.matrixStack.popPose();
    }

    public static void renderLaserMid(BufferContext pack, RenderContext renderContext, float length) {
        Color color = renderContext.color;
        float alpha = renderContext.alpha;

        pack.matrixStack.pushPose();
        Matrix4f matrix4f = pack.matrixStack.last().pose();
        IVertexBuilder buffer = pack.buffer;
        setup(pack);
        begin(pack);
        int light = renderLight;
        float center = length * 0.9f;
        float side = (length - center) * 0.5f;
        float side1 = center + side;
        buffer.vertex(matrix4f, -1.0F, side, 0.0F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1.0F, side, 0.0F).endVertex();
        buffer.vertex(matrix4f, -1.0F, side1, 0.0F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1.0F, side1, 0.0F).endVertex();
        buffer.vertex(matrix4f, 1.0F, side1, 0.0F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1.0F, side1, 0.0F).endVertex();
        buffer.vertex(matrix4f, 1.0F, side, 0.0F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1.0F, side, 0.0F).endVertex();

        buffer.vertex(matrix4f, 0.5F, side, -0.866025F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.5F, side, -0.866025F).endVertex();
        buffer.vertex(matrix4f, 0.5F, side1, -0.866025F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.5F, side1, -0.866025F).endVertex();
        buffer.vertex(matrix4f, -0.5F, side1, 0.866025F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-0.5F, side1, 0.866025F).endVertex();
        buffer.vertex(matrix4f, -0.5F, side, 0.866025F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-0.5F, side, 0.866025F).endVertex();

        buffer.vertex(matrix4f, 0.5F, side, 0.866025F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.5F, side, 0.866025F).endVertex();
        buffer.vertex(matrix4f, 0.5F, side1, 0.866025F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.5F, side1, 0.866025F).endVertex();
        buffer.vertex(matrix4f, -0.5F, side1, -0.866025F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-0.5F, side1, -0.866025F).endVertex();
        buffer.vertex(matrix4f, -0.5F, side, -0.866025F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-0.5F, side, -0.866025F).endVertex();

        finish(pack);
        end(pack);
        pack.matrixStack.popPose();
    }

    public static void renderLaserBottom(BufferContext pack, RenderContext renderContext, float length) {
        Color color = renderContext.color;
        float alpha = renderContext.alpha;

        pack.matrixStack.pushPose();
        Matrix4f matrix4f = pack.matrixStack.last().pose();
        IVertexBuilder buffer = pack.buffer;
        setup(pack);
        begin(pack);
        int light = renderLight;
        float center = length * 0.9f;
        float side = (length - center) * 0.5f;
        float side1 = center + side;

        buffer.vertex(matrix4f, -1.0F, 0.0F, 0.0F).color(color.r(), color.g(), color.b(), 0).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(matrix4f, -1.0F, side, 0.0F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1.0F, side, 0.0F).endVertex();
        buffer.vertex(matrix4f, 1.0F, side, 0.0F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1.0F, side, 0.0F).endVertex();
        buffer.vertex(matrix4f, 1.0F, 0.0F, 0.0F).color(color.r(), color.g(), color.b(), 0).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1.0F, 0.0F, 0.0F).endVertex();

        buffer.vertex(matrix4f, 0.5F, 0.0F, -0.866025F).color(color.r(), color.g(), color.b(), 0).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.5F, 0.0F, -0.866025F).endVertex();
        buffer.vertex(matrix4f, 0.5F, side, -0.866025F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.5F, side, -0.866025F).endVertex();
        buffer.vertex(matrix4f, -0.5F, side, 0.866025F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-0.5F, side, 0.866025F).endVertex();
        buffer.vertex(matrix4f, -0.5F, 0.0F, 0.866025F).color(color.r(), color.g(), color.b(), 0).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-0.5F, 0.0F, 0.866025F).endVertex();

        buffer.vertex(matrix4f, 0.5F, 0.0F, 0.866025F).color(color.r(), color.g(), color.b(), 0).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.5F, 0.0F, 0.866025F).endVertex();
        buffer.vertex(matrix4f, 0.5F, side, 0.866025F).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0.5F, side, 0.866025F).endVertex();
        buffer.vertex(matrix4f, -0.5F, side, -0.866025F).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-0.5F, side, -0.866025F).endVertex();
        buffer.vertex(matrix4f, -0.5F, 0.0F, -0.866025F).color(color.r(), color.g(), color.b(), 0).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-0.5F, 0.0F, -0.866025F).endVertex();

        finish(pack);
        end(pack);
        pack.matrixStack.popPose();
    }

    public static final VertexContext EmptyVertexContext = new VertexContext(false, "", 0.0f);

    public static void renderStaticParticle(BufferContext pack, RenderContext renderContext) {
        renderStaticParticle(pack, renderContext, EmptyVertexContext);
    }

    public static void renderStaticParticle(BufferContext pack, RenderContext renderContext, VertexContext vertexContext) {
        Color color = renderContext.color;
        boolean shake = vertexContext.shake;
        String shakeName = vertexContext.shakeName;
        float limit = vertexContext.limit;
        float alpha = renderContext.alpha;
        if(!shake) {
            callQuadVertex(pack, renderContext);
            return;
        }

        pack.matrixStack.pushPose();
        Matrix4f matrix4f = pack.matrixStack.last().pose();
        IVertexBuilder buffer = pack.buffer;
        setup(pack);
        begin(pack);
        int light = renderLight;
        Vector3d V0;
        Vector3d V1;
        Vector3d V2;
        Vector3d V3;

        VertexShakerHelper.VertexGroup group = VertexShakerHelper.getGroup(shakeName);
        group.putVertex(QuadVector[0].x(), QuadVector[0].y(), QuadVector[0].z(), limit);
        group.putVertex(QuadVector[1].x(), QuadVector[1].y(), QuadVector[1].z(), limit);
        group.putVertex(QuadVector[2].x(), QuadVector[2].y(), QuadVector[2].z(), limit);
        group.putVertex(QuadVector[3].x(), QuadVector[3].y(), QuadVector[3].z(), limit);

        V0 = group.getVertex(QuadVector[0].x(), QuadVector[0].y(), QuadVector[0].z()).getPositionVec();
        V1 = group.getVertex(QuadVector[1].x(), QuadVector[1].y(), QuadVector[1].z()).getPositionVec();
        V2 = group.getVertex(QuadVector[2].x(), QuadVector[2].y(), QuadVector[2].z()).getPositionVec();
        V3 = group.getVertex(QuadVector[3].x(), QuadVector[3].y(), QuadVector[3].z()).getPositionVec();

        buffer.vertex(matrix4f, (float) V0.x, (float) V0.y, (float) V0.z).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal((float) V0.x, (float) V0.y, (float) V0.z).endVertex();
        buffer.vertex(matrix4f, (float) V1.x, (float) V1.y, (float) V1.z).color(color.r(), color.g(), color.b(), alpha).uv(1.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal((float) V1.x, (float) V1.y, (float) V1.z).endVertex();
        buffer.vertex(matrix4f, (float) V2.x, (float) V2.y, (float) V2.z).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 0.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal((float) V2.x, (float) V2.y, (float) V2.z).endVertex();
        buffer.vertex(matrix4f, (float) V3.x, (float) V3.y, (float) V3.z).color(color.r(), color.g(), color.b(), alpha).uv(0.0f, 1.0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal((float) V3.x, (float) V3.y, (float) V3.z).endVertex();

        finish(pack);
        end(pack);
        pack.matrixStack.popPose();
    }

    public static void renderParticle(BufferContext pack, RenderContext renderContext, VertexContext vertexContext) {
        pack.matrixStack.pushPose();
        pack.matrixStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        renderStaticParticle(pack, renderContext, vertexContext);
        pack.matrixStack.popPose();
    }

    public static void renderCube(BufferContext context, RenderContext renderContext) {
        MatrixStack matrixStack = context.matrixStack;
        BufferBuilder buffer = context.buffer;
        RenderType type = context.type;
        Color color = renderContext.color;
        float alpha = renderContext.alpha;
        int lightmap = renderContext.packedLightIn;

        String hash = "CUBE_VERTEX" + renderContext.hashCode();

        if(!GL_LIST_INDEX.containsKey(hash)) {
            Queue<VertexBuffer> vertexBuffers = Queues.newArrayDeque();
            VertexBuffer vertexBuffer = new VertexBuffer(DefaultVertexFormats.NEW_ENTITY);
            begin(context);
            for(int i=0; i<6; ++i)
                for(int j=0; j<4; ++j) {
                    float[] pos = vertex_list[index_list[i][j]];
                    float u = 1.0f;
                    float v = 1.0f;
                    if(j == 2 || j == 3)
                        u = 0.0f;
                    if(j == 1 || j == 2)
                        v = 0.0f;
                    buffer.vertex(pos[0], pos[1], pos[2]).color(color.r(), color.g(), color.b(), alpha).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal(pos[0], pos[1], pos[2]).endVertex();
                }
            buffer.end();
            vertexBuffer.upload(buffer);
            vertexBuffers.add(vertexBuffer);
            GL_LIST_INDEX.put(hash, vertexBuffers);
        } else {
            setup(context);
            type.setupRenderState();
            Queue<VertexBuffer> vertexBuffers = GL_LIST_INDEX.get(hash);
            for (VertexBuffer vertexBuffer : vertexBuffers) {
                vertexBuffer.bind();
                DefaultVertexFormats.NEW_ENTITY.setupBufferState(0);
                vertexBuffer.draw(matrixStack.last().pose(), type.mode());
                VertexBuffer.unbind();
                DefaultVertexFormats.NEW_ENTITY.clearBufferState();
            }
            type.clearRenderState();
            end(context);
        }
    }

    public static void renderPoint(BufferContext context, RenderContext renderContext, List<Vector3d> vector3dList) {
        MatrixStack matrixStack = context.matrixStack;
        BufferBuilder buffer = context.buffer;
        RenderType type = context.type;
        Color color = renderContext.color;
        float alpha = renderContext.alpha;
        int lightmap = renderContext.packedLightIn;

        String hash = "POINT_VERTEX" + renderContext.hashCode() + "_" + vector3dList.hashCode();

        if(!GL_LIST_INDEX.containsKey(hash)) {
            Queue<VertexBuffer> vertexBuffers = Queues.newArrayDeque();
            VertexBuffer vertexBuffer = new VertexBuffer(DefaultVertexFormats.NEW_ENTITY);
            setup(context);
            begin(context);
            for (Vector3d vector3d : vector3dList) {
                buffer.vertex(vector3d.x, vector3d.y, vector3d.z).color(color.r(), color.g(), color.b(), alpha).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightmap).normal((float) vector3d.x, (float) vector3d.y, (float) vector3d.z).endVertex();
            }
            buffer.end();
            vertexBuffer.upload(buffer);
            vertexBuffers.add(vertexBuffer);
            GL_LIST_INDEX.put(hash, vertexBuffers);
            end(context);
        } else {
            setup(context);
            type.setupRenderState();
            Queue<VertexBuffer> vertexBuffers = GL_LIST_INDEX.get(hash);
            for (VertexBuffer vertexBuffer : vertexBuffers) {
                vertexBuffer.bind();
                DefaultVertexFormats.NEW_ENTITY.setupBufferState(0);
                vertexBuffer.draw(matrixStack.last().pose(), context.type.mode());
                VertexBuffer.unbind();
                DefaultVertexFormats.NEW_ENTITY.clearBufferState();
            }
            type.clearRenderState();
            end(context);
        }
    }

    public static void renderCubeDynamic(BufferContext context, RenderContext renderContext) {
        Color color = ModElements.ORIGIN_COLOR;
        if (renderContext.color != null)
            color = renderContext.color;

        float alpha = renderContext.alpha;

        context.matrixStack.pushPose();
        Matrix4f matrix4f = context.matrixStack.last().pose();
        IVertexBuilder buffer = context.buffer;
        setup(context);
        begin(context);
        int light = renderContext.packedLightIn;
        for(int i=0; i<6; ++i)
            for(int j=0; j<4; ++j) {
                float[] pos = vertex_list[index_list[i][j]];
                float u = 1.0f;
                float v = 1.0f;
                if(j == 2 || j == 3)
                    u = 0.0f;
                if(j == 1 || j == 2)
                    v = 0.0f;
                buffer.vertex(matrix4f, pos[0], pos[1], pos[2]).color(color.r(), color.g(), color.b(), alpha).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pos[0], pos[1], pos[2]).endVertex();
            }
        finish(context);
        end(context);
        context.matrixStack.popPose();
    }

    public static void renderSphere(BufferContext pack, RenderContext renderContext, int stacks) {
        pack.matrixStack.pushPose();
        Matrix4f matrix4f = pack.matrixStack.last().pose();
        IVertexBuilder buffer = pack.buffer;
        String hash =stacks +"Sphere"+renderContext.hashCode();

        if(!GL_LIST_INDEX.containsKey(hash)) {
            Queue<Queue<VertexAttribute>> cylinderQueue = drawSphere(stacks, renderContext, EmptyVertexContext);

            setup(pack);
            begin(pack);
            Queue<VertexBuffer> vertexBuffers = Queues.newArrayDeque();
            Iterator<Queue<VertexAttribute>> it = cylinderQueue.iterator();
            while (it.hasNext()) {
                Iterator<VertexAttribute> innerIt = it.next().iterator();
                begin(pack);
                VertexBuffer vertexBuffer = new VertexBuffer(DefaultVertexFormats.NEW_ENTITY);
                while (innerIt.hasNext()) {
                    VertexAttribute vertex = innerIt.next();
                    buffer.vertex(vertex.posX, vertex.posY, vertex.posZ).color(vertex.color.r(), vertex.color.g(), vertex.color.b(), vertex.alpha).uv(vertex.texU, vertex.texV)
                            .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(RenderHelper.renderLight).normal(vertex.normalX, vertex.normalY, vertex.normalZ).endVertex();
                }
                pack.buffer.end();
                vertexBuffer.upload(pack.buffer);
                vertexBuffers.add(vertexBuffer);
            }

            GL_LIST_INDEX.put(hash, vertexBuffers);
            end(pack);
        } else {
            setup(pack);
            Queue<VertexBuffer> vertexBuffers = GL_LIST_INDEX.get(hash);
            pack.type.setupRenderState();
            for (VertexBuffer vertexBuffer : vertexBuffers) {
                vertexBuffer.bind();
                DefaultVertexFormats.NEW_ENTITY.setupBufferState(0);
                vertexBuffer.draw(matrix4f, pack.type.mode());
                VertexBuffer.unbind();
                DefaultVertexFormats.NEW_ENTITY.clearBufferState();
            }
            pack.type.clearRenderState();
            end(pack);
        }
        pack.matrixStack.popPose();
    }

    public static boolean shouldRender(AxisAlignedBB aabbIn) {
        if(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null) return false;
        World world = Minecraft.getInstance().level;
        PlayerEntity player = Minecraft.getInstance().player;
        Vector3d start = player.getEyePosition(Minecraft.getInstance().getFrameTime());
        for(Vector3d vec : getAABBPoints(aabbIn)) {
            if(world.clip(new RayTraceContext(start, vec, RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, player)).getType() == RayTraceResult.Type.MISS)
                return true;
        }
        return false;
    }

    public static boolean shouldRender(LitParticle particle) {
        if(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null) return false;
        World world = Minecraft.getInstance().level;
        PlayerEntity player = Minecraft.getInstance().player;
        Vector3d start = player.getEyePosition(Minecraft.getInstance().getFrameTime());
        return world.clip(new RayTraceContext(start, particle.centerVec(), RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, player)).getType() == RayTraceResult.Type.MISS;
    }
    public static Queue<Vector3d> getAABBPoints(AxisAlignedBB aabbIn) {
        Queue<Vector3d> queue = Queues.newArrayDeque();
        queue.add(new Vector3d(aabbIn.minX, aabbIn.minY, aabbIn.minZ));
        queue.add(new Vector3d(aabbIn.maxX, aabbIn.minY, aabbIn.minZ));
        queue.add(new Vector3d(aabbIn.minX, aabbIn.maxY, aabbIn.minZ));
        queue.add(new Vector3d(aabbIn.minX, aabbIn.minY, aabbIn.maxZ));
        queue.add(new Vector3d(aabbIn.maxX, aabbIn.maxY, aabbIn.minZ));
        queue.add(new Vector3d(aabbIn.minX, aabbIn.maxY, aabbIn.maxZ));
        queue.add(new Vector3d(aabbIn.maxX, aabbIn.minY, aabbIn.maxZ));
        queue.add(new Vector3d(aabbIn.maxX, aabbIn.maxY, aabbIn.maxZ));
        return queue;
    }

    public static void renderSphere(BufferContext pack, RenderContext renderContext, VertexContext vertexContext, int stacks) {
        if (stacks <= 2)
            stacks = 2;

        if (stacks % 2 != 0)
            stacks++;

        if(vertexContext.hitReaction.length == 0) {
            renderSphere(pack, renderContext, stacks);
            return;
        }
        pack.matrixStack.pushPose();
        Matrix4f matrix4f = pack.matrixStack.last().pose();
        IVertexBuilder buffer = pack.buffer;
        Queue<Queue<VertexAttribute>> cylinderQueue = drawSphere(stacks, renderContext, vertexContext);

        setup(pack);
        Iterator<Queue<VertexAttribute>> it = cylinderQueue.iterator();
        while (it.hasNext()) {
            Iterator<VertexAttribute> innerIt = it.next().iterator();
            pack.matrixStack.pushPose();
            begin(pack);
            while (innerIt.hasNext()) {
                VertexAttribute vertex = innerIt.next();
                buffer.vertex(matrix4f, vertex.posX, vertex.posY, vertex.posZ).color(vertex.color.r(), vertex.color.g(), vertex.color.b(), vertex.alpha).uv(vertex.texU, vertex.texV)
                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(renderContext.packedLightIn).normal(vertex.normalX, vertex.normalY, vertex.normalZ).endVertex();
            }
            finish(pack);
            pack.matrixStack.popPose();
        }
        end(pack);
        pack.matrixStack.popPose();
    }

    public static void renderSphere(BufferContext pack, Queue<Queue<VertexAttribute>> vertexQueue) {
        pack.matrixStack.pushPose();
        Matrix4f matrix4f = pack.matrixStack.last().pose();
        IVertexBuilder buffer = pack.buffer;

        setup(pack);
        Iterator<Queue<VertexAttribute>> it = vertexQueue.iterator();
        while (it.hasNext()) {
            Iterator<VertexAttribute> innerIt = it.next().iterator();
            pack.matrixStack.pushPose();
            begin(pack);
            while (innerIt.hasNext()) {
                VertexAttribute vertex = innerIt.next();
                buffer.vertex(matrix4f, vertex.posX, vertex.posY, vertex.posZ).color(vertex.color.r(), vertex.color.g(), vertex.color.b(), vertex.alpha).uv(vertex.texU, vertex.texV)
                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(RenderHelper.renderLight).normal(vertex.normalX, vertex.normalY, vertex.normalZ).endVertex();
            }
            finish(pack);
            pack.matrixStack.popPose();
        }
        end(pack);
        pack.matrixStack.popPose();
    }

    public static void renderDistortion(BufferContext pack, Queue<Queue<VertexAttribute>> vertexQueue) {
        pack.matrixStack.pushPose();
        Matrix4f matrix4f = pack.matrixStack.last().pose();
        IVertexBuilder buffer = pack.buffer;

        setup(pack);
        Iterator<Queue<VertexAttribute>> it = vertexQueue.iterator();
        while (it.hasNext()) {
            Iterator<VertexAttribute> innerIt = it.next().iterator();
            pack.matrixStack.pushPose();
            begin(pack);
            while (innerIt.hasNext()) {
                VertexAttribute vertex = innerIt.next();
                buffer.vertex(matrix4f, vertex.posX, vertex.posY, vertex.posZ).color(vertex.color.r(), vertex.color.g(), vertex.color.b(), vertex.alpha).uv(vertex.texU, vertex.texV).endVertex();
            }
            finish(pack);
            pack.matrixStack.popPose();
        }
        end(pack);
        pack.matrixStack.popPose();
    }

    public static Queue<Queue<VertexAttribute>> drawSphere(int stacks, RenderContext renderContext, VertexContext context) {
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
        Queue<Queue<VertexAttribute>> queues = Queues.newArrayDeque();
        for (i = imin; i < imax; i++) {
            rho = i * drho;
            s = 0.0f;
            Queue<VertexAttribute> queue = Queues.newArrayDeque();
            for (j = 0; j <= stacks; j++) {
                theta = (j == stacks) ? 0.0f : j * dtheta;
                x = (float) (-Math.sin(theta) * Math.sin(rho));
                y = (float) (Math.cos(theta) * Math.sin(rho));
                z = (float) Math.cos(rho);
                VertexAttribute vertex0 = !context.shake ? calculateBlendVertex(x * radius, y * radius, z * radius, s, t, renderContext, context.hitReaction)
                        : calculateVertex(x * radius, y * radius, z * radius, s, t, renderContext, context.hitReaction, context.limit);
                queue.add(vertex0);
                x = (float) (-Math.sin(theta) * Math.sin(rho + drho));
                y = (float) (Math.cos(theta) * Math.sin(rho + drho));
                z = (float) (Math.cos(rho + drho));
                VertexAttribute vertex1 = !context.shake ? calculateBlendVertex(x * radius, y * radius, z * radius, s, t - dt, renderContext, context.hitReaction)
                        : calculateVertex(x * radius, y * radius, z * radius, s, t - dt, renderContext, context.hitReaction, context.limit);
                queue.add(vertex1);
                s += ds;
            }
            queues.add(queue);
            t -= dt;
        }
        return queues;
    }

    private static VertexAttribute calculateBlendVertex(float x, float y, float z, float texU, float TexV, RenderContext context, VectorHitReaction[] hitReaction) {
        float maxAlhpa = 0;

        if (hitReaction != null)
            for (int c = 0; c < hitReaction.length; c++) {
                VectorHitReaction reaction = hitReaction[c];
                float add = reaction.IsHit(new Vector3d(x, y, z));
                if (add > maxAlhpa)
                    maxAlhpa = add;
            }

        if (maxAlhpa + context.alpha > 1.0f) {
            maxAlhpa = 1.0f;
        } else
            maxAlhpa += context.alpha;

        VertexAttribute vertexAttribute = new VertexAttribute();
        vertexAttribute.setPos(x, y, z);
        vertexAttribute.setNormal(x, y, z);
        vertexAttribute.setUV(texU, TexV);
        vertexAttribute.alpha = maxAlhpa;
        vertexAttribute.setColor(context.color);
        vertexAttribute.setLightmap(context.packedLightIn);
        return vertexAttribute;
    }

    private static VertexAttribute calculateVertex(float x, float y, float z, float texU, float TexV, RenderContext context, VectorHitReaction[] hitReaction, float limit) {
        float maxAlhpa = 0;

        if (hitReaction != null)
            for (int c = 0; c < hitReaction.length; c++) {
                VectorHitReaction reaction = hitReaction[c];
                float add = reaction.IsHit(new Vector3d(x, y, z));
                if (add > maxAlhpa)
                    maxAlhpa = add;
            }


        float redScale = context.color.r() + (context.color.r() * maxAlhpa * 2);
        float greenScale = context.color.g() + (context.color.g() * maxAlhpa * 2);
        float blueScale = context.color.b() + (context.color.b() * maxAlhpa * 2);

        if (redScale > 1.0f) redScale = 1.0f;
        if (greenScale > 1.0f) greenScale = 1.0f;
        if (blueScale > 1.0f) blueScale = 1.0f;

        if (context.alpha + maxAlhpa >= 1.0f) {
            maxAlhpa = 1.0f - context.alpha;
        }

        float scale = 1f - maxAlhpa * limit;
        if(scale > 0)
            scale = -scale;

        VertexAttribute vertexAttribute = new VertexAttribute();
        vertexAttribute.setPos(x * scale, y * scale, z * scale);
        vertexAttribute.setNormal(x * scale, y * scale, z * scale);
        vertexAttribute.setUV(texU, TexV);
        vertexAttribute.alpha = context.alpha;
        vertexAttribute.setColor(Color.create(redScale, greenScale, blueScale));
        vertexAttribute.setLightmap(context.packedLightIn);
        return vertexAttribute;
    }

    public static void drawShape(MatrixStack matrixStackIn, IVertexBuilder bufferIn, VoxelShape shapeIn, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = matrixStackIn.last().pose();
        shapeIn.forAllEdges((p_230013_12_, p_230013_14_, p_230013_16_, p_230013_18_, p_230013_20_, p_230013_22_) -> {
            bufferIn.vertex(matrix4f, (float)(p_230013_12_ + xIn), (float)(p_230013_14_ + yIn), (float)(p_230013_16_ + zIn)).color(red, green, blue, alpha).endVertex();
            bufferIn.vertex(matrix4f, (float)(p_230013_18_ + xIn), (float)(p_230013_20_ + yIn), (float)(p_230013_22_ + zIn)).color(red, green, blue, alpha).endVertex();
        });
    }

    public static Color getRGB() {
        int rLimit = 120;
        int rHalf = rLimit / 2;
        int gLimit = 240;
        int gHalf = gLimit / 2;
        int bLimit = 360;
        int bHalf = bLimit / 2;
        int r = MagickCore.proxy.getRunTick() % rLimit;
        int g = MagickCore.proxy.getRunTick() % gLimit;
        int b = MagickCore.proxy.getRunTick() % bLimit;
        if(r >= rHalf)
            r = rHalf - (r-rHalf);
        if(g >= gHalf)
            g = gHalf - (g-gHalf);
        if(b >= bHalf)
            b = bHalf - (b-bHalf);
        return Color.create(r/(float)(rLimit)*2, g/(float)(gLimit)*2, b/(float)(bLimit)*2);
    }

    public static class VertexAttribute {
        public float posX;
        public float posY;
        public float posZ;
        public float normalX;
        public float normalY;
        public float normalZ;
        public float texU;
        public float texV;
        public float alpha;
        public int lightmap;
        public Color color = Color.ORIGIN_COLOR;

        public void setPos(float x, float y, float z) {
            this.posX = x;
            this.posY = y;
            this.posZ = z;
        }

        public void setLightmap(int lightmap) {
            this.lightmap = lightmap;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        public void setPos(Vector3d vec) {
            this.posX = (float) vec.x;
            this.posY = (float) vec.y;
            this.posZ = (float) vec.z;
        }

        public void setPos(Vector3f vec) {
            this.posX = vec.x();
            this.posY = vec.y();
            this.posZ = vec.z();
        }

        public void setNormal(float x, float y, float z) {
            this.normalX = x;
            this.normalY = y;
            this.normalZ = z;
        }

        public void setNormal(Vector3d vec) {
            this.normalX = (float) vec.x;
            this.normalY = (float) vec.y;
            this.normalZ = (float) vec.z;
        }

        public void setNormal(Vector3f vec) {
            this.normalX = vec.x();
            this.normalY = vec.y();
            this.normalZ = vec.z();
        }

        public void setUV(float u, float v) {
            this.texU = u;
            this.texV = v;
        }

        public void setUV(Vector2f uv) {
            this.texU = uv.x;
            this.texV = uv.y;
        }
    }

    public static void begin(BufferContext bufferContext) {
        if(!bufferContext.buffer.building()) {
            bufferContext.buffer.begin(bufferContext.type.mode(), bufferContext.type.format());
        }
    }

    public static void finish(BufferContext bufferContext) {
        if(!queueMode && bufferContext.buffer.building())
            bufferContext.type.end(bufferContext.buffer, 0, 0, 0);
    }

    public static void setup(BufferContext bufferContext) {
        if(stopShader()) return;
        if(!queueMode && !bufferContext.renderShader.isEmpty()) {
            ShaderGroup shader = ShaderEvent.getShaders(new ResourceLocation(bufferContext.renderShader.shaders[0]));
            if(shader != null) {
                Framebuffer framebuffer = shader.getTempTarget(
                        Objects.requireNonNull(ShaderEvent.getShaderFrameName(bufferContext.renderShader.shaders[0])));
                framebuffer.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
                framebuffer.bindWrite(false);
                ShaderEvent.pushRender(bufferContext.renderShader.shaders[0]);
            }
        }
    }

    public static void end(BufferContext bufferContext) {
        if(stopShader()) return;
        if(!queueMode && !bufferContext.renderShader.isEmpty()) {
            ShaderGroup shader = ShaderEvent.getShaders(new ResourceLocation(bufferContext.renderShader.shaders[0]));
            if(shader != null) {
                Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isInRangeToRender3d(IPositionEntity position, double x, double y, double z) {
        double d0 = position.positionVec().x - x;
        double d1 = position.positionVec().y - y;
        double d2 = position.positionVec().z - z;
        double d3 = d0 * d0 + d1 * d1 + d2 * d2;
        return isInRangeToRenderDist(position, d3);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isInRangeToRender3d(IPositionEntity position, double x, double y, double z, double scale) {
        double d0 = position.positionVec().x - x;
        double d1 = position.positionVec().y - y;
        double d2 = position.positionVec().z - z;
        double d3 = d0 * d0 + d1 * d1 + d2 * d2 * scale * scale;
        return isInRangeToRenderDist(position, d3);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isInRangeToRenderDist(IPositionEntity position, double distance) {
        double d0 = position.boundingBox().getSize();
        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }
        if(d0 < 0.7)
            d0 = 0.7;
        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }

    public static void setWorldMatrix(MatrixStack matrixStack) {
        worldMatrixStack = matrixStack;
    }

    public static MatrixStack getWorldMatrix() {
        return worldMatrixStack;
    }

    public static void setWorldMatrix4f(Matrix4f matrix4f) {
        worldMatrix4f = matrix4f;
    }

    public static Matrix4f getWorldMatrix4f() {
        return worldMatrix4f;
    }

    public static void setRenderingWorld(boolean renderingWorld) {
        RenderHelper.renderingWorld = renderingWorld;
    }

    public static boolean isRenderingWorld() {
        return renderingWorld;
    }

    public static boolean showDebug() {
        return Minecraft.getInstance().options.renderDebug;
    }

    public static boolean stopShader() {
        return isRenderingShader();
    }

    private static boolean checkShader() {
        initOptifineClazz();
        if(hasOptifine()) {
            try {
                Field field = optifineShader.getDeclaredField("shaderPackLoaded");
                field.setAccessible(true);
                boolean loaded = (Boolean) field.get(null);
                if(loaded) {
                    return true;
                } else if (optifineShaderLoaded) {
                    GL_LIST_INDEX.clear();
                    return false;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void checkRenderingShader() {
        optifineShaderLoaded = checkShader();
    }

    public static boolean isRenderingShader() {
        return optifineShaderLoaded;
    }

    public static boolean hasOptifine() {
        return optifineShader != null;
    }

    public static int getOptifineActiveProgram() {
        int i = 0;
        try {
            Field field = optifineShader.getDeclaredField("activeProgramID");
            field.setAccessible(true);
            i = (int) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return i;
    }

    public static void initOptifineClazz() {
        if(!checkOptifine) {
            checkOptifine = true;
            try {
                Class clazz = Class.forName("net.optifine.shaders.Shaders");
                if(clazz != null) {
                    optifineShader = clazz;
                }
            } catch (ClassNotFoundException e) {
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static PlayerEntity getPlayer() {
        return Minecraft.getInstance().player;
    }
}
