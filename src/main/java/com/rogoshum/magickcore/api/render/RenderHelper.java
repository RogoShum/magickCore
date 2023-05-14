package com.rogoshum.magickcore.api.render;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector4f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IPositionEntity;
import com.rogoshum.magickcore.client.init.ClientConfig;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.client.render.Cylinder;
import com.rogoshum.magickcore.client.render.Polyhedron;
import com.rogoshum.magickcore.client.render.instanced.LightingInstanceRenderer;
import com.rogoshum.magickcore.client.render.instanced.ModelInstanceRenderer;
import com.rogoshum.magickcore.client.vertex.VectorHitReaction;
import com.rogoshum.magickcore.client.event.ShaderEvent;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.util.MutableFloat;
import com.rogoshum.magickcore.mixin.AccessorRenderType;
import com.rogoshum.magickcore.mixin.AccessorTextureState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.util.*;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryUtil;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class RenderHelper {
    public static final ResourceLocation TEXTURE = MagickCore.fromId("entity_texture_atlas.png");
    public static final TextureAtlas TEXTURE_ATLAS = new TextureAtlas(TEXTURE);
    private static final HashMap<ResourceLocation, ResourceLocation> TEXTURE_MAP = new HashMap<>();

    public static final HashMap<Object, Queue<VertexAttribute>> VERTEX_CACHE = new HashMap<>();
    public static final HashMap<Object, ModelInstanceRenderer> INSTANCE_CACHE = new HashMap<>();
    public static final HashSet<ModelInstanceRenderer> UPDATE_INSTANCE = new HashSet<>();
    public static final HashMap<ChunkPos, Boolean> PARTICLE_CHUNK_TRACE = new HashMap<>();
    public static LightingInstanceRenderer LIGHTING_INSTANCE_RENDERER;
    static {
        RenderSystem.recordRenderCall(() -> LIGHTING_INSTANCE_RENDERER = new LightingInstanceRenderer());
    }

    public static final ResourceLocation BLANK_TEX = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");

    public static final ResourceLocation DISSOVE = new ResourceLocation(MagickCore.MOD_ID + ":textures/dissove.png");
    public static final ResourceLocation RED_AND_BLUE = new ResourceLocation(MagickCore.MOD_ID, "textures/red_and_blue_2014.png");
    public static final ResourceLocation RED_AND_BLUE_AND_GREEN = new ResourceLocation(MagickCore.MOD_ID, "textures/red_and_blue_and_green.png");
    public static final ResourceLocation COLORS = new ResourceLocation(MagickCore.MOD_ID, "textures/theyaremanycolors.png");
    public static final int renderLight = 15728880;
    public static final int halfLight = 7864440;
    public static boolean queueMode = false;
    private static Class<?> oculusShader = null;
    private static boolean oculusShaderLoaded = false;
    private static boolean checkIris = false;
    private static boolean renderingWorld = false;
    private static PoseStack viewMatrix = new PoseStack();
    private static PoseStack modelMatrix = new PoseStack();
    private static PoseStack vertexMatrix = new PoseStack();
    private static Matrix4f projectionMatrix4f = new Matrix4f();
    public static MutableFloat WORLD_FOG = new MutableFloat();
    public static float[] shaderFogColor = null;
    private static final float[] posScale = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
    public static ResourceLocation GLOBAL_TEXTURE = null;
    private static int gl33 = -1;

    public static float[] getPosScale() {
        return posScale;
    }

    public static void setPosScale(float x, float y, float z, float s) {
        posScale[0] = x;
        posScale[1] = y;
        posScale[2] = z;
        posScale[3] = s;
    }

    public static ShaderInstance getRendertypeEntityTranslucentShader() {
        return rendertypeEntityTranslucentDistShader;
    }

    public static ShaderInstance getRendertypeEntityTranslucentInstanceShader() {
        return rendertypeEntityTranslucentDistInstanceShader;
    }

    public static void setRendertypeEntityTranslucentInstanceShader(ShaderInstance rendertypeEntityTranslucentShader) {
        RenderHelper.rendertypeEntityTranslucentDistInstanceShader = rendertypeEntityTranslucentShader;
    }

    public static void setRendertypeEntityLightShader(ShaderInstance rendertypeEntityTranslucentShader) {
        RenderHelper.rendertypeEntityLightShader = rendertypeEntityTranslucentShader;
    }

    public static ShaderInstance getRendertypeEntityLightShader() {
        return rendertypeEntityLightShader;
    }

    public static void setRendertypeEntityLightInstanceShader(ShaderInstance rendertypeEntityTranslucentShader) {
        RenderHelper.rendertypeEntityLightInstanceShader = rendertypeEntityTranslucentShader;
    }

    public static ShaderInstance getRendertypeEntityLightInstanceShader() {
        return rendertypeEntityLightInstanceShader;
    }

    public static void setRendertypeEntityTranslucentShader(ShaderInstance rendertypeEntityTranslucentShader) {
        RenderHelper.rendertypeEntityTranslucentDistShader = rendertypeEntityTranslucentShader;
    }

    public static ShaderInstance getRendertypeEntityTranslucentNoiseShader() {
        return rendertypeEntityTranslucentNoiseShader;
    }

    public static void setRendertypeEntityQuadrantShader(ShaderInstance rendertypeEntityTranslucentShader) {
        RenderHelper.rendertypeEntityQuadrantShader = rendertypeEntityTranslucentShader;
    }

    public static ShaderInstance getRendertypeEntityQuadrantShader() {
        return rendertypeEntityQuadrantShader;
    }

    public static void setRendertypeEntityTranslucentNoiseShader(ShaderInstance rendertypeEntityTranslucentShader) {
        RenderHelper.rendertypeEntityTranslucentNoiseShader = rendertypeEntityTranslucentShader;
    }

    public static ShaderInstance getPositionColorTexLightmapShader() {
        return positionColorTexLightShader;
    }

    public static void setPositionColorTexLightmapShader(ShaderInstance positionColorTexLightShader) {
        RenderHelper.positionColorTexLightShader = positionColorTexLightShader;
    }

    public static ShaderInstance getPositionColorTexLightmapDistInstanceShader() {
        return positionColorTexLightDistInstanceShader;
    }

    public static void setPositionColorTexLightmapDistInstanceShader(ShaderInstance positionColorTexLightShader) {
        RenderHelper.positionColorTexLightDistInstanceShader = positionColorTexLightShader;
    }
    public static ShaderInstance getPositionColorTexLightmapDistShader() {
        return positionColorTexLightDistShader;
    }

    public static void setPositionColorTexLightmapDistShader(ShaderInstance positionColorTexLightShader) {
        RenderHelper.positionColorTexLightDistShader = positionColorTexLightShader;
    }

    public static ShaderInstance getPositionTextureShader() {
        return positionTextureShader;
    }

    public static void setPositionTextureShader(ShaderInstance positionTextureShader) {
        RenderHelper.positionTextureShader = positionTextureShader;
    }

    private static ShaderInstance rendertypeEntityLightShader;
    private static ShaderInstance rendertypeEntityLightInstanceShader;
    private static ShaderInstance rendertypeEntityTranslucentDistShader;
    private static ShaderInstance rendertypeEntityTranslucentDistInstanceShader;
    private static ShaderInstance rendertypeEntityTranslucentNoiseShader;
    private static ShaderInstance rendertypeEntityQuadrantShader;

    private static ShaderInstance positionColorTexLightShader;
    private static ShaderInstance positionColorTexLightDistShader;
    private static ShaderInstance positionColorTexLightDistInstanceShader;
    private static ShaderInstance positionTextureShader;
    protected static final RenderStateShard.TransparencyStateShard NO_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("no_transparency", RenderSystem::disableBlend, () -> {
    });

    protected static final RenderStateShard.TransparencyStateShard LIGHTNING_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("magick_lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
    }, () -> {
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    protected static final RenderStateShard.TransparencyStateShard ADDITIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("magick_additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
    }, () -> {
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderStateShard.TransparencyStateShard DEPTH_ADDITIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("depth_magick_additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderStateShard.TransparencyStateShard DEPTH_LIGHTNING_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("depth_magick_lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderStateShard.TransparencyStateShard GLINT_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("magick_glint_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
    }, () -> {
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderStateShard.TransparencyStateShard TRANSLUCENT_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("magick_translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthMask(false);
    }, () -> {
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderStateShard.TransparencyStateShard DEPTH_GLINT_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("depth_magick_glint_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    protected static final RenderStateShard.TransparencyStateShard DEPTH_TRANSLUCENT_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("depth_magick_translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_ORIGINAL_TRANSLUCENT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityTranslucentShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntitySolidShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_SHADER = new RenderStateShard.ShaderStateShard(RenderHelper::getRendertypeEntityTranslucentShader);
    protected static final RenderStateShard.ShaderStateShard POSITION_COLOR_TEX_LIGHTMAP_DIST_SHADER = new RenderStateShard.ShaderStateShard(RenderHelper::getPositionColorTexLightmapDistShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENERGY_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEnergySwirlShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_NOISE_SHADER = new RenderStateShard.ShaderStateShard(RenderHelper::getRendertypeEntityTranslucentNoiseShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_QUADRANT_SHADER = new RenderStateShard.ShaderStateShard(RenderHelper::getRendertypeEntityQuadrantShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_LIGHT_SHADER = new RenderStateShard.ShaderStateShard(RenderHelper::getRendertypeEntityLightShader);
    protected static final RenderStateShard.ShaderStateShard LINE_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeLinesShader);
    protected static final RenderStateShard.WriteMaskStateShard COLOR_DEPTH_WRITE = new RenderStateShard.WriteMaskStateShard(true, true);
    protected static final RenderStateShard.CullStateShard CULL_DISABLED = new RenderStateShard.CullStateShard(false);
    protected static final RenderStateShard.LightmapStateShard LIGHTMAP_ENABLED = new RenderStateShard.LightmapStateShard(true);
    protected static final RenderStateShard.OverlayStateShard OVERLAY_ENABLED = new RenderStateShard.OverlayStateShard(true);
    protected static final RenderStateShard.DepthTestStateShard DEPTH_EQUAL = new RenderStateShard.DepthTestStateShard("==", 514);
    protected static final RenderStateShard.DepthTestStateShard DEPTH_LEQUAL = new RenderStateShard.DepthTestStateShard("<=", 515);

    public static class LineStateShard extends RenderStateShard.TexturingStateShard {
        private final OptionalDouble width;
        public LineStateShard(OptionalDouble size) {
            super(MagickCore.MOD_ID+"_line_width_" + size, () -> {
                if (!Objects.equals(size, OptionalDouble.of(1.0D))) {
                    if (size.isPresent()) {
                        RenderSystem.lineWidth((float)size.getAsDouble());
                    } else {
                        RenderSystem.lineWidth(Math.max(2.5F, (float)Minecraft.getInstance().getWindow().getWidth() / 1920.0F * 2.5F));
                    }
                }
            }, () -> {
                if (!Objects.equals(size, OptionalDouble.of(1.0D))) {
                    RenderSystem.lineWidth(1.0F);
                }
            });
            this.width = size;
        }

        public String toString() {
            return this.name + "[" + (this.width.isPresent() ? this.width.getAsDouble() : "window_scale") + "]";
        }
    }

    public static class ParticleShakeStateShard extends RenderStateShard.TexturingStateShard {
        private final MutableFloat shakeLimit;
        private final MutableFloat shakeSpeed;

        public ParticleShakeStateShard(MutableFloat size, MutableFloat shakeSpeed) {
            super(MagickCore.MOD_ID+"_shake_" + size+"_"+shakeSpeed, () -> {
                WORLD_FOG.set(RenderSystem.getShaderFogStart());
                RenderSystem.setShaderFogStart(size.get());
            }, () -> {
                RenderSystem.setShaderFogStart(WORLD_FOG.get());
            });
            this.shakeLimit = size;
            this.shakeSpeed = shakeSpeed;
        }

        public String toString() {
            return this.name + "[" + this.shakeLimit.get() + "-" + + this.shakeSpeed.get() + "]";
        }
    }

    public static class EntityGlintStateShard extends RenderStateShard.TexturingStateShard {
        private final MutableFloat shakeLimit;
        private final MutableFloat shakeSpeed;
        private final MutableFloat glintScale;
        private final MutableFloat glintRotate;

        public EntityGlintStateShard(MutableFloat size, MutableFloat shakeSpeed, MutableFloat glintScale, MutableFloat glintRotate) {
            super(MagickCore.MOD_ID+"_glint_" + size+"_"+shakeSpeed+"_"+glintScale+"_"+glintRotate, () -> {
                shaderFogColor = RenderSystem.getShaderFogColor();
                RenderSystem.setShaderFogColor(1.0f, 1.0f, 1.0f, size.get());
                RenderSystem.setShaderTexture(3, RED_AND_BLUE_AND_GREEN);
                RenderSystem.setShaderGameTime((long) (MagickCore.proxy.getRunTick()), shakeSpeed.get()*Minecraft.getInstance().getFrameTime());
                if(glintScale.get() != 0) {
                    long i = Util.getMillis() * 8L;
                    float f1 = (float) (i % 15000L) / 15000.0F;
                    if(glintScale.get() < 0)
                        f1 = -f1;
                    Matrix4f matrix4f = Matrix4f.createTranslateMatrix(0.0f, f1, 0.0F);
                    matrix4f.multiply(Vector3f.YP.rotationDegrees(glintRotate.get()));
                    matrix4f.multiply(Matrix4f.createScaleMatrix(1.0f, glintScale.get(), 1.0f));
                    RenderSystem.setTextureMatrix(matrix4f);
                }
            }, () -> {
                if(shaderFogColor != null)
                    RenderSystem.setShaderFogColor(shaderFogColor[0], shaderFogColor[1], shaderFogColor[2], shaderFogColor[3]);
                RenderSystem.setShaderGameTime(Minecraft.getInstance().level.getGameTime(), Minecraft.getInstance().getFrameTime());
                RenderSystem.resetTextureMatrix();
            });
            this.shakeLimit = size;
            this.shakeSpeed = shakeSpeed;
            this.glintScale = glintScale;
            this.glintRotate = glintRotate;
        }

        public String toString() {
            return this.name + "[" + this.shakeLimit.get() + "-" + + this.shakeSpeed.get() + "-" + this.glintScale.get() + "-" + this.glintRotate.get() + "]";
        }
    }

    public static class EntityShakeStateShard extends RenderStateShard.TexturingStateShard {
        private final MutableFloat shakeLimit;
        private final MutableFloat shakeSpeed;
        private final MutableFloat glintScale;
        private final MutableFloat glintRotate;

        public static EntityShakeStateShard create() {
            return new EntityShakeStateShard(new MutableFloat(0), new MutableFloat(0), new MutableFloat(0), new MutableFloat(0));
        }

        public static EntityShakeStateShard create(float glintScale, float glintRotate) {
            return new EntityShakeStateShard(new MutableFloat(0), new MutableFloat(0), new MutableFloat(glintScale), new MutableFloat(glintRotate));
        }

        public static EntityShakeStateShard create(float size, float shakeSpeed, float glintScale, float glintRotate) {
            return new EntityShakeStateShard(new MutableFloat(size), new MutableFloat(shakeSpeed), new MutableFloat(glintScale), new MutableFloat(glintRotate));
        }

        public EntityShakeStateShard(MutableFloat size, MutableFloat shakeSpeed, MutableFloat glintScale, MutableFloat glintRotate) {
            super(MagickCore.MOD_ID+"_shake_" + size+"_"+shakeSpeed+"_"+glintScale+"_"+glintRotate, () -> {
                shaderFogColor = RenderSystem.getShaderFogColor();
                RenderSystem.setShaderFogColor(1.0f, 1.0f, 1.0f, size.get());
                RenderSystem.setShaderTexture(3, RED_AND_BLUE_AND_GREEN);
                RenderSystem.setShaderGameTime((long) (MagickCore.proxy.getRunTick()), shakeSpeed.get()*Minecraft.getInstance().getFrameTime());
                if(glintScale.get() > 0) {
                    long i = Util.getMillis() * 8L;
                    float f = (float) (i % 110000L) / 110000.0F;
                    float f1 = (float) (i % 30000L) / 30000.0F;
                    Matrix4f matrix4f = Matrix4f.createTranslateMatrix(f, f1, 0.0F);
                    matrix4f.multiply(Vector3f.YP.rotationDegrees(glintRotate.get()));
                    matrix4f.multiply(Matrix4f.createScaleMatrix(glintScale.get(), glintScale.get(), glintScale.get()));
                    RenderSystem.setTextureMatrix(matrix4f);
                }
            }, () -> {
                if(shaderFogColor != null)
                    RenderSystem.setShaderFogColor(shaderFogColor[0], shaderFogColor[1], shaderFogColor[2], shaderFogColor[3]);
                RenderSystem.setShaderGameTime(Minecraft.getInstance().level.getGameTime(), Minecraft.getInstance().getFrameTime());
                if(glintScale.get() > 0) {
                    RenderSystem.resetTextureMatrix();
                }
            });
            this.shakeLimit = size;
            this.shakeSpeed = shakeSpeed;
            this.glintScale = glintScale;
            this.glintRotate = glintRotate;
        }

        public String toString() {
            return this.name + "[" + this.shakeLimit.get() + "-" + + this.shakeSpeed.get() + "-" + this.glintScale.get() + "-" + this.glintRotate.get() + "]";
        }
    }

    public static RenderStateShard.TexturingStateShard lineState(OptionalDouble size) {
        return new LineStateShard(size);
    }

    public static void addTexture(ResourceLocation texture) {
        if(texture == null) return;
        if(!TEXTURE.equals(texture) && !TEXTURE_MAP.containsKey(texture)) {
            ResourceLocation atlas = new ResourceLocation(texture.getNamespace(), texture.getPath().replace("textures/", "").replace(".png", ""));
            if(RenderSystem.isOnRenderThread()) {
                TEXTURE_MAP.put(texture, atlas);
                TextureAtlas.Preparations preparations = TEXTURE_ATLAS.prepareToStitch(Minecraft.getInstance().getResourceManager(), TEXTURE_MAP.values().stream(), InactiveProfiler.INSTANCE, 0);
                TEXTURE_ATLAS.reload(preparations);
            } else {
                RenderSystem.recordRenderCall(() -> {
                    TEXTURE_MAP.put(texture, atlas);
                    TextureAtlas.Preparations preparations = TEXTURE_ATLAS.prepareToStitch(Minecraft.getInstance().getResourceManager(), TEXTURE_MAP.values().stream(), InactiveProfiler.INSTANCE, 0);
                    TEXTURE_ATLAS.reload(preparations);
                });
            }
        }
    }

    public static void addTexture(Collection<ResourceLocation> textures) {
        HashSet<ResourceLocation> res = new HashSet<>();
        for(ResourceLocation tex : textures) {
            ResourceLocation atlas = new ResourceLocation(tex.getNamespace(), tex.getPath().replace("textures/", "").replace(".png", ""));
            res.add(atlas);
            TEXTURE_MAP.put(tex, atlas);
        }

        RenderSystem.recordRenderCall(() -> {
            TextureAtlas.Preparations preparations = TEXTURE_ATLAS.prepareToStitch(Minecraft.getInstance().getResourceManager(), TEXTURE_MAP.values().stream(), InactiveProfiler.INSTANCE, 0);
            TEXTURE_ATLAS.reload(preparations);
        });
    }

    public static RenderType getTexturedParticle(ResourceLocation locationIn, float shakeLimit) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setShaderState(POSITION_COLOR_TEX_LIGHTMAP_DIST_SHADER).setTexturingState(new ParticleShakeStateShard(new MutableFloat(shakeLimit), new MutableFloat(1f)))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP_ENABLED).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Particle", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.TRIANGLES, 256, false, false, rendertype$state);
    }

    public static RenderType getTexturedParticleGlow(ResourceLocation locationIn, float shakeLimit) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setShaderState(POSITION_COLOR_TEX_LIGHTMAP_DIST_SHADER).setTexturingState(new ParticleShakeStateShard(new MutableFloat(shakeLimit), new MutableFloat(1f)))
                .setTransparencyState(LIGHTNING_TRANSPARENCY).setLightmapState(LIGHTMAP_ENABLED).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Particle_Glow", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.TRIANGLES, 256, false, false, rendertype$state);
    }

    public static RenderType getTexturedQuadsSolid(ResourceLocation locationIn) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(NO_TRANSPARENCY).setTexturingState(EntityShakeStateShard.create()).setLightmapState(LIGHTMAP_ENABLED)
                .setShaderState(RENDERTYPE_ENTITY_SHADER).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Quads_SolidType", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexturedQuadsTranslucent(ResourceLocation locationIn) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED)
                .setLightmapState(LIGHTMAP_ENABLED).setTexturingState(EntityShakeStateShard.create()).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Quads_Translucent_Instanced", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexturedQuadsEnergy(ResourceLocation locationIn) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOverlayState(OVERLAY_ENABLED)
                .setLightmapState(LIGHTMAP_ENABLED).setShaderState(RENDERTYPE_ENERGY_SHADER).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Quads_Energy", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$state);
    }

    public static RenderType getTexturedQuadsGlint(ResourceLocation locationIn) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(LIGHTNING_TRANSPARENCY).setOverlayState(OVERLAY_ENABLED)
                .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setLightmapState(LIGHTMAP_ENABLED)
                .setTexturingState(EntityShakeStateShard.create(1.0f, 0.0f)).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Quads_GlintType_Instanced", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexturedQuadsGlow(ResourceLocation locationIn) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(LIGHTNING_TRANSPARENCY).setOverlayState(OVERLAY_ENABLED)
                .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setCullState(CULL_DISABLED)
                .setLightmapState(LIGHTMAP_ENABLED).setTexturingState(EntityShakeStateShard.create()).createCompositeState(true);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Entity_Glow_Instanced", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexturedEntityGlint(ResourceLocation locationIn, float glintScale, float glintRotate) {
        return getTexturedEntityGlintShake(locationIn, glintScale, glintRotate, 0.0f, 0.0f);
    }

    public static RenderType getTexturedEntityGlintShake(ResourceLocation locationIn, float glintScale, float glintRotate, float dist, float speed) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setTransparencyState(LIGHTNING_TRANSPARENCY)
                .setOverlayState(OVERLAY_ENABLED).setLightmapState(LIGHTMAP_ENABLED).setCullState(CULL_DISABLED)
                .setTexturingState(EntityShakeStateShard.create(dist, speed, glintScale, glintRotate)).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Entity_GlintType_Instanced", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexturedEntityGlowNoise(ResourceLocation locationIn) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(LIGHTNING_TRANSPARENCY).setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED)
                .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_NOISE_SHADER)
                .setLightmapState(LIGHTMAP_ENABLED).createCompositeState(true);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Entity_GlintType_Noise", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexturedSegmentLaser(ResourceLocation locationIn, float glintScale) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(LIGHTNING_TRANSPARENCY)
                .setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setLightmapState(LIGHTMAP_ENABLED).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                .setTexturingState(new EntityGlintStateShard(new MutableFloat(0.05f), new MutableFloat(0), new MutableFloat(glintScale), new MutableFloat(0))).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Uni_GlintType", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexturedLaserGlint(ResourceLocation locationIn, float glintScale) {
        return getTexturedUniGlint(locationIn, glintScale, 0, 0.05f, 0);
    }

    public static RenderType getTexturedUniGlint(ResourceLocation locationIn, float glintScale, float glintRotate) {
        return getTexturedUniGlint(locationIn, glintScale, glintRotate, 0, 0);
    }

    public static RenderType getTexturedUniGlint(ResourceLocation locationIn, float glintScale, float glintRotate, float dist, float speed) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(LIGHTNING_TRANSPARENCY)
                .setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setLightmapState(LIGHTMAP_ENABLED).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                .setTexturingState(new EntityGlintStateShard(new MutableFloat(dist), new MutableFloat(speed), new MutableFloat(glintScale), new MutableFloat(glintRotate))).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Uni_GlintType_Instanced", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getColorDecal() {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                .setTransparencyState(LIGHTNING_TRANSPARENCY).setShaderState(RENDERTYPE_ENTITY_LIGHT_SHADER)
                .setTexturingState(new RenderStateShard.TexturingStateShard("depth_off", () -> {
                    GL11.glCullFace(GL11.GL_FRONT);
                    RenderSystem.disableDepthTest();
                }, () -> {
                    RenderSystem.enableDepthTest();
                    GL11.glCullFace(GL11.GL_BACK);
                })).createCompositeState(true);
        return RenderType.create(MagickCore.MOD_ID + ":Colored_Decal_LightingInstanced", DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, VertexFormat.Mode.QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getEntityQuadrant(ResourceLocation locationIn) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                .setTransparencyState(LIGHTNING_TRANSPARENCY).setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setOverlayState(OVERLAY_ENABLED).setTexturingState(EntityShakeStateShard.create()).setCullState(CULL_DISABLED)
                .setLightmapState(LIGHTMAP_ENABLED).setShaderState(RENDERTYPE_ENTITY_QUADRANT_SHADER).createCompositeState(true);
        return RenderType.create(MagickCore.MOD_ID + ":Quadrant", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexturedSphereGlowNoise(ResourceLocation locationIn, float glintScale, float glintRotate) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false)).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_NOISE_SHADER).setTransparencyState(LIGHTNING_TRANSPARENCY).setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setLightmapState(LIGHTMAP_ENABLED).setTexturingState(EntityShakeStateShard.create(glintScale, glintRotate)).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Sphere_Glow_Noise_" + glintScale + "_" + glintRotate, DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, rendertype$state);
    }

    //===================================================================================================================================================================//

    public static RenderType getTexturedCylinderItem(ResourceLocation locationIn, float glintScale, float glintRotate) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(DEPTH_LIGHTNING_TRANSPARENCY)
                .setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setLightmapState(LIGHTMAP_ENABLED).setShaderState(RENDERTYPE_ENTITY_ORIGINAL_TRANSLUCENT_SHADER)
                .setTexturingState(new EntityGlintStateShard(new MutableFloat(), new MutableFloat(), new MutableFloat(glintScale), new MutableFloat(glintRotate))).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Cylinder_Item_" + glintScale + "_" + glintRotate, DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$state);
    }

    public static RenderType getTexturedItemGlint(ResourceLocation locationIn, float glintScale, float glintRotate) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(LIGHTNING_TRANSPARENCY).setLightmapState(LIGHTMAP_ENABLED).setShaderState(RENDERTYPE_ENERGY_SHADER)
                .setTexturingState(EntityShakeStateShard.create(glintScale, glintRotate)).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Item_GlintType", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getTexturedShaderItemTranslucent(ResourceLocation locationIn) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(DEPTH_TRANSLUCENT_TRANSPARENCY).setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED)
                .setLightmapState(LIGHTMAP_ENABLED).setShaderState(RENDERTYPE_ENTITY_ORIGINAL_TRANSLUCENT_SHADER).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":Textured_Shader_Item", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$state);
    }

    public static RenderType getLayerEntityGlint(ResourceLocation locationIn, float glintScale, float glintRotate) {
        RenderType.CompositeState rendertype$state =
                RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                        .setTransparencyState(GLINT_TRANSPARENCY).setDepthTestState(DEPTH_EQUAL)
                        .setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                        .setLightmapState(LIGHTMAP_ENABLED).setTexturingState(EntityShakeStateShard.create(glintScale, glintRotate)).createCompositeState(true);
        return RenderType.create(MagickCore.MOD_ID + ":Layer_Entity_Glint_" + glintScale + "_" + glintRotate, DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getLayerEntityGlintSolid(ResourceLocation locationIn, float glintScale, float glintRotate) {
        RenderType.CompositeState rendertype$state =
                RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDepthTestState(DEPTH_EQUAL)
                        .setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                        .setLightmapState(LIGHTMAP_ENABLED).setTexturingState(EntityShakeStateShard.create(glintScale, glintRotate)).createCompositeState(true);
        return RenderType.create(MagickCore.MOD_ID + ":Layer_Entity_Glint_SolidType_" + glintScale + "_" + glintRotate, DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, rendertype$state);
    }

    public static RenderType getLayerEntityGlint(ResourceLocation locationIn) {
        RenderType.CompositeState rendertype$state =
                RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                        .setTransparencyState(GLINT_TRANSPARENCY).setDepthTestState(DEPTH_EQUAL)
                        .setOverlayState(OVERLAY_ENABLED).setCullState(CULL_DISABLED).setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                        .setLightmapState(LIGHTMAP_ENABLED).setTexturingState(EntityShakeStateShard.create(0.32f, 10f)).createCompositeState(true);
        return RenderType.create(MagickCore.MOD_ID + ":Layer_Entity_Glint_0.32_10", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, rendertype$state);
    }

    public static final RenderStateShard.LayeringStateShard VIEW_OFFSET_Z_LAYERING = new RenderStateShard.LayeringStateShard("view_offset_z_layering", () -> {
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.scale(0.99975586F, 0.99975586F, 0.99975586F);
        RenderSystem.applyModelViewMatrix();
    }, () -> {
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
    });

    public static RenderType getLineStripGlow(double width) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder().setTransparencyState(ADDITIVE_TRANSPARENCY).setLayeringState(VIEW_OFFSET_Z_LAYERING).setShaderState(LINE_SHADER).setWriteMaskState(COLOR_DEPTH_WRITE).setLightmapState(LIGHTMAP_ENABLED).setCullState(CULL_DISABLED).setTexturingState(lineState(OptionalDouble.of(width))).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":LINES_STRIP_" + width, DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.LINES, 256, false, false, rendertype$state);
    }

    public static RenderType getLineStripPC(double width) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder().setShaderState(LINE_SHADER).setTransparencyState(ADDITIVE_TRANSPARENCY).setLightmapState(LIGHTMAP_ENABLED).setWriteMaskState(COLOR_DEPTH_WRITE).setLayeringState(VIEW_OFFSET_Z_LAYERING).setCullState(CULL_DISABLED).setTexturingState(lineState(OptionalDouble.of(width))).createCompositeState(false);
        return RenderType.create(MagickCore.MOD_ID + ":LINES_STRIP_PC_" + width, DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES, 256, false, false, rendertype$state);
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

    public static final Vec3[] QUAD_VECTOR = new Vec3[]{
            new Vec3(-1.0, -1.0, 0.0)
            , new Vec3(-1.0F, 1.0F, 0.0F)
            , new Vec3(1.0F, 1.0F, 0.0F)
            , new Vec3(1.0F, -1.0F, 0.0F)};

    public static final Vec3[] FAN_PARTICLE = new Vec3[]{
            new Vec3(0.0, 0.0, 0.0),
            new Vec3(-1.0, -1.0, 0.0)
            , new Vec3(-1.0F, 1.0F, 0.0F)
            , new Vec3(1.0F, 1.0F, 0.0F)
            , new Vec3(1.0F, -1.0F, 0.0F)};

    public static void callQuadVertexDynamic(BufferContext context, RenderContext renderContext) {
        PoseStack matrixStack = context.matrixStack;
        Matrix4f matrix4f = matrixStack.last().pose();
        BufferBuilder buffer = context.buffer;
        Color color = renderContext.color;
        float alpha = renderContext.alpha;
        int lightmap = renderContext.packedLightIn;
        String hash = "QUAD_VERTEX";
        Vec3[] quad = RenderHelper.QUAD_VECTOR;

        if(GLOBAL_TEXTURE != null) {
            if(VERTEX_CACHE.containsKey(hash)) {
                buildCacheVertexQueue(context, renderContext, hash);
            } else {
                Queue<VertexAttribute> queue = Queues.newArrayDeque();
                for (int i = 0; i < 4; ++i) {
                    Vec3 vec3 = RenderHelper.QUAD_VECTOR[i];
                    VertexAttribute vertex = new VertexAttribute();
                    vertex.pos(vec3);
                    vertex.color(color);
                    vertex.lightmap(lightmap);
                    vertex.normal(vec3);
                    if(i == 0)
                        vertex.uv(1.0f, 1.0f);
                    else if(i == 1)
                        vertex.uv(1.0f, 0.0f);
                    else if(i == 2)
                        vertex.uv(0.0f, 0.0f);
                    else vertex.uv(0.0f, 1.0f);
                    vertex.alpha(1);
                    queue.add(vertex);
                }
                VERTEX_CACHE.put(hash, queue);
            }
        } else {
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
        }
    }

    public static class CylinderContext{
        public final float baseRadius;
        public final float midRadius;
        public final float scaleFactor;
        public final int depth;
        public final float height;
        public final float edgeAlpha;
        public final float midAlpha;
        public final float alphaFactor;
        public CylinderContext(float baseRadius, float midRadius, float scaleFactor, int depth,
                               float height, float edgeAlpha, float midAlpha, float alphaFactor) {
            this.baseRadius = baseRadius;
            this.midRadius = midRadius;
            this.scaleFactor = scaleFactor;
            this.depth = depth;
            this.height = height;
            this.edgeAlpha = this.depth > 0 ? edgeAlpha : Math.max(edgeAlpha, midAlpha);
            this.midAlpha = this.depth > 0 ? midAlpha : Math.max(edgeAlpha, midAlpha);
            this.alphaFactor = alphaFactor;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CylinderContext that)) return false;
            return Float.compare(that.baseRadius, baseRadius) == 0 && Float.compare(that.midRadius, midRadius) == 0 && Float.compare(that.scaleFactor, scaleFactor) == 0 && depth == that.depth && Float.compare(that.height, height) == 0 && Float.compare(that.edgeAlpha, edgeAlpha) == 0 && Float.compare(that.midAlpha, midAlpha) == 0 && Float.compare(that.alphaFactor, alphaFactor) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(baseRadius, midRadius, scaleFactor, depth, height, edgeAlpha, midAlpha, alphaFactor);
        }

        @Override
        public String toString() {
            return "CylinderContext{" +
                    "baseRadius=" + baseRadius +
                    ", midRadius=" + midRadius +
                    ", scaleFactor=" + scaleFactor +
                    ", depth=" + depth +
                    ", height=" + height +
                    ", edgeAlpha=" + edgeAlpha +
                    ", midAlpha=" + midAlpha +
                    ", alphaFactor=" + alphaFactor +
                    '}';
        }
    }

    public static class RenderContext {
        public float alpha;
        public final Color color;
        public final int packedLightIn;
        public final boolean scaleAlpha;

        public RenderContext(float alpha, Color color, int packedLightIn, boolean scaleAlpha) {
            this.alpha = alpha;
            this.color = color;
            this.packedLightIn = packedLightIn;
            this.scaleAlpha = scaleAlpha;
        }

        public RenderContext(float alpha, Color color, int packedLightIn) {
            this.alpha = alpha;
            this.color = color;
            this.packedLightIn = packedLightIn;
            this.scaleAlpha = false;
        }

        public RenderContext(float alpha, Color color) {
            this.alpha = alpha;
            this.color = color;
            this.scaleAlpha = false;
            packedLightIn = RenderHelper.renderLight;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RenderContext that)) return false;
            return Float.compare(that.alpha, alpha) == 0 && packedLightIn == that.packedLightIn && color.equals(that.color) && scaleAlpha == that.scaleAlpha;
        }

        @Override
        public int hashCode() {
            return Objects.hash("RenderContext", alpha, color, packedLightIn, scaleAlpha);
        }
    }

    public static class VertexContext {
        public final VectorHitReaction[] hitReaction;

        public final boolean shake;
        public final float limit;

        public VertexContext(VectorHitReaction[] hitReaction, float limit) {
            this.hitReaction = hitReaction;
            this.limit = limit;
            this.shake = hitReaction != null && limit > 0;
        }

        public VertexContext() {
            this.hitReaction = null;
            this.limit = 0;
            this.shake = false;
        }
    }

    public static void buildCacheVertexQueue(BufferContext pack, RenderContext render, Object hash) {
        boolean instancingRender = RenderHelper.isRenderingWorld() && RenderHelper.isRenderTypeInstanced(pack.type) && RenderHelper.gl33();//
        TextureAtlasSprite sprite = getRenderTypeSpirit(pack.type);
        Matrix4f matrix4f = pack.matrixStack.last().pose();
        if(!instancingRender) {
            VertexConsumer buffer = pack.buffer;
            Queue<VertexAttribute> vertexBuffers = VERTEX_CACHE.get(hash);
            setup(pack);
            begin(pack);
            for (VertexAttribute vertex : vertexBuffers) {
                if(RenderHelper.isRenderTypeGlint(pack.type))
                    vertex.build(matrix4f, buffer, render);
                else
                    vertex.build(matrix4f, buffer, render, sprite);
            }
            finish(pack);
            end(pack);
        } else {
            ModelInstanceRenderer instance = INSTANCE_CACHE.get(hash);
            if(instance == null) {
                instance = ModelInstanceRenderer.fromVertexAttrib(VERTEX_CACHE.get(hash));
                INSTANCE_CACHE.put(hash, instance);
            }
            UPDATE_INSTANCE.add(instance);
            instance.addInstanceAttrib((buffer -> {
                buffer.put(render.packedLightIn & '\uffff');
                buffer.put(render.packedLightIn >> 16 & '\uffff');

                if(RenderHelper.isRenderTypeGlint(pack.type)) {
                    buffer.put(0);
                    buffer.put(1);
                    buffer.put(0);
                    buffer.put(1);
                } else {
                    buffer.put(sprite.getU0());
                    buffer.put(sprite.getU1());
                    buffer.put(sprite.getV0());
                    buffer.put(sprite.getV1());
                }

                buffer.put(render.color.r());
                buffer.put(render.color.g());
                buffer.put(render.color.b());
                buffer.put(render.alpha);

                FloatBuffer matBuffer = MemoryUtil.memAllocFloat(16);
                matrix4f.store(matBuffer);
                matBuffer.clear();
                buffer.put(matBuffer);
                MemoryUtil.memFree(matBuffer);
            }));
        }
    }

    public static void renderVertexQueueDynamic(BufferContext pack, Queue<VertexAttribute> vertexs) {
        Matrix4f matrix4f = pack.matrixStack.last().pose();
        VertexConsumer buffer = pack.buffer;
        setup(pack);
        begin(pack);
        TextureAtlasSprite sprite = getRenderTypeSpirit(pack.type);
        for (VertexAttribute vertex : vertexs) {
            if(RenderHelper.isRenderTypeGlint(pack.type))
                vertex.build(matrix4f, buffer, null);
            else
                vertex.build(matrix4f, buffer, null, sprite);
        }
        finish(pack);
        end(pack);
    }

    public static void renderDistortionCache(BufferContext pack, RenderContext renderContext, VertexContext vertexContext, int stacks) {
        if (stacks <= 2)
            stacks = 2;

        if (stacks % 2 != 0)
            stacks++;

        if(vertexContext.hitReaction.length == 0) {
            renderDistortionCache(pack, renderContext, stacks);
            return;
        }
        pack.matrixStack.pushPose();
        Queue<VertexAttribute> cylinderQueue = Polyhedron.drawPolyhedron(renderContext, vertexContext, stacks);
        renderVertexQueueDynamic(pack, cylinderQueue);
        pack.matrixStack.popPose();
    }

    public static void renderDistortionCache(BufferContext pack, RenderContext renderContext, int stacks) {
        pack.matrixStack.pushPose();
        String hash =stacks +"_Distortion";

        if(!VERTEX_CACHE.containsKey(hash)) {
            Queue<VertexAttribute> cylinderQueue = Polyhedron.drawPolyhedron(renderContext, EMPTY_VERTEX_CONTEXT, stacks);
            VERTEX_CACHE.put(hash, cylinderQueue);
        } else {
            buildCacheVertexQueue(pack, renderContext, hash);
        }
        pack.matrixStack.popPose();
    }

    public static void renderCylinderCache(BufferContext pack, CylinderContext context, RenderContext render) {
        pack.matrixStack.pushPose();
        if(!VERTEX_CACHE.containsKey(context)) {
            Queue<VertexAttribute> cylinderQueue = Cylinder.drawCylinder(context, EMPTY_VERTEX_CONTEXT);
            VERTEX_CACHE.put(context, cylinderQueue);
        } else {
            buildCacheVertexQueue(pack, render, context);
        }
        pack.matrixStack.popPose();
    }

    public static void renderLaserParticle(BufferContext pack, RenderContext renderContext) {
        Color color = renderContext.color;
        float alpha = 1;
        int light = renderContext.packedLightIn;

        String hash = "LaserParticle";
        if(!VERTEX_CACHE.containsKey(hash)) {
            Queue<VertexAttribute> queue = Queues.newArrayDeque();
            queue.add(VertexAttribute.create().pos(-1.0F, 0.0F, 0.0F).color(color).alpha(0).uv(1.0f, 1.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(-1.0F, 0.2f, 0.0F).color(color).alpha(alpha).uv(1.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(1.0F, 0.2f, 0.0F).color(color).alpha(alpha).uv(0.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(1.0F, 0.0F, 0.0F).color(color).alpha(0).uv(0.0f, 1.0f).lightmap(light).normalAsPos());

            queue.add(VertexAttribute.create().pos(0.0F, 0.0F, -1.0F).color(color).alpha(0).uv(1.0f, 1.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(0.0F, 0.2f, -1.0F).color(color).alpha(alpha).uv(1.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(0.0F, 0.2f, 1.0F).color(color).alpha(alpha).uv(0.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(0.0F, 0.0F, 1.0F).color(color).alpha(0).uv(0.0f, 1.0f).lightmap(light).normalAsPos());

            queue.add(VertexAttribute.create().pos(-1.0F, 0.2f, 0.0F).color(color).alpha(alpha).uv(1.0f, 1.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(-1.0F, 0.8f, 0.0F).color(color).alpha(alpha).uv(1.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(1.0F, 0.8f, 0.0F).color(color).alpha(alpha).uv(0.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(1.0F, 0.2f, 0.0F).color(color).alpha(alpha).uv(0.0f, 1.0f).lightmap(light).normalAsPos());

            queue.add(VertexAttribute.create().pos(0.0F, 0.2f, -1.0F).color(color).alpha(alpha).uv(1.0f, 1.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(0.0F, 0.8f, -1.0F).color(color).alpha(alpha).uv(1.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(0.0F, 0.8f, 1.0F).color(color).alpha(alpha).uv(0.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(0.0F, 0.2f, 1.0F).color(color).alpha(alpha).uv(0.0f, 1.0f).lightmap(light).normalAsPos());

            queue.add(VertexAttribute.create().pos(-1.0F, 0.8f, 0.0F).color(color).alpha(alpha).uv(1.0f, 1.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(-1.0F, 1, 0.0F).color(color).alpha(0).uv(1.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(1.0F, 1, 0.0F).color(color).alpha(0).uv(0.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(1.0F, 0.8f, 0.0F).color(color).alpha(alpha).uv(0.0f, 1.0f).lightmap(light).normalAsPos());

            queue.add(VertexAttribute.create().pos(0.0F, 0.8f, -1.0F).color(color).alpha(alpha).uv(1.0f, 1.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(0.0F, 1, -1.0F).color(color).alpha(0).uv(1.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(0.0F, 1, 1.0F).color(color).alpha(0).uv(0.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(0.0F, 0.8f, 1.0F).color(color).alpha(alpha).uv(0.0f, 1.0f).lightmap(light).normalAsPos());
            VERTEX_CACHE.put(hash, queue);
        } else {
            buildCacheVertexQueue(pack, renderContext, hash);
        }
    }

    public static void renderLaserScale(BufferContext pack, RenderContext renderContext, float length, float begin, float end) {
        Color color = renderContext.color;
        float alpha = renderContext.alpha;

        pack.matrixStack.pushPose();
        int light = renderLight;
        Queue<VertexAttribute> queue = Queues.newArrayDeque();
        queue.add(VertexAttribute.create().pos(-1.0F, 0.0f, 0.0F).color(color).alpha(alpha).uv(1.0f, end).lightmap(light).normalAsPos());
        queue.add(VertexAttribute.create().pos(-1.0F, length, 0.0F).color(color).alpha(alpha).uv(1.0f, begin).lightmap(light).normalAsPos());
        queue.add(VertexAttribute.create().pos(1.0F, length, 0.0F).color(color).alpha(alpha).uv(0.0f, begin).lightmap(light).normalAsPos());
        queue.add(VertexAttribute.create().pos(1.0F, 0.0f, 0.0F).color(color).alpha(alpha).uv(0.0f, end).lightmap(light).normalAsPos());

        queue.add(VertexAttribute.create().pos(0.0F, 0.0F, -1.0F).color(color).alpha(alpha).uv(1.0f, end).lightmap(light).normalAsPos());
        queue.add(VertexAttribute.create().pos(0.0F, length, -1.0F).color(color).alpha(alpha).uv(1.0f, begin).lightmap(light).normalAsPos());
        queue.add(VertexAttribute.create().pos(0.0F, length, 1.0F).color(color).alpha(alpha).uv(0.0f, begin).lightmap(light).normalAsPos());
        queue.add(VertexAttribute.create().pos(0.0F, 0.0F, 1.0F).color(color).alpha(alpha).uv(0.0f, end).lightmap(light).normalAsPos());
        renderVertexQueueDynamic(pack, queue);
        pack.matrixStack.popPose();
    }

    public static void renderLaserTop(BufferContext pack, RenderContext renderContext) {
        Color color = renderContext.color;
        float alpha = 1;
        int light = renderContext.packedLightIn;

        String hash = "LaserTop";
        if(!VERTEX_CACHE.containsKey(hash)) {
            Queue<VertexAttribute> queue = Queues.newArrayDeque();
            queue.add(VertexAttribute.create().pos(-1.0F, 0.95f, 0.0F).color(color).alpha(alpha).uv(1.0f, 1.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(-1.0F, 1, 0.0F).color(color).alpha(0).uv(1.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(1.0F, 1, 0.0F).color(color).alpha(0).uv(0.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(1.0F, 0.95f, 0.0F).color(color).alpha(alpha).uv(0.0f, 1.0f).lightmap(light).normalAsPos());

            queue.add(VertexAttribute.create().pos(0.5F, 0.95f, -0.866025F).color(color).alpha(alpha).uv(1.0f, 1.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(0.5F, 1, -0.866025F).color(color).alpha(0).uv(1.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(-0.5F, 1, 0.866025F).color(color).alpha(0).uv(0.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(-0.5F, 0.95f, 0.866025F).color(color).alpha(alpha).uv(0.0f, 1.0f).lightmap(light).normalAsPos());

            queue.add(VertexAttribute.create().pos(0.5F, 0.95f, 0.866025F).color(color).alpha(alpha).uv(1.0f, 1.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(0.5F, 1, 0.866025F).color(color).alpha(0).uv(1.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(-0.5F, 1, -0.866025F).color(color).alpha(0).uv(0.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(-0.5F, 0.95f, -0.866025F).color(color).alpha(alpha).uv(0.0f, 1.0f).lightmap(light).normalAsPos());
            VERTEX_CACHE.put(hash, queue);
        } else {
            buildCacheVertexQueue(pack, renderContext, hash);
        }
    }

    public static void renderLaserMid(BufferContext pack, RenderContext renderContext) {
        Color color = renderContext.color;
        float alpha = 1;
        int light = renderContext.packedLightIn;

        String hash = "LaserMid";
        if(!VERTEX_CACHE.containsKey(hash)) {
            Queue<VertexAttribute> queue = Queues.newArrayDeque();
            queue.add(VertexAttribute.create().pos(-1.0F, 0.05f, 0.0F).color(color).alpha(alpha).uv(1.0f, 1.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(-1.0F, 0.95f, 0.0F).color(color).alpha(alpha).uv(1.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(1.0F, 0.95f, 0.0F).color(color).alpha(alpha).uv(0.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(1.0F, 0.05f, 0.0F).color(color).alpha(alpha).uv(0.0f, 1.0f).lightmap(light).normalAsPos());

            queue.add(VertexAttribute.create().pos(0.5F, 0.05f, -0.866025F).color(color).alpha(alpha).uv(1.0f, 1.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(0.5F, 0.95f, -0.866025F).color(color).alpha(alpha).uv(1.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(-0.5F, 0.95f, 0.866025F).color(color).alpha(alpha).uv(0.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(-0.5F, 0.05f, 0.866025F).color(color).alpha(alpha).uv(0.0f, 1.0f).lightmap(light).normalAsPos());

            queue.add(VertexAttribute.create().pos(0.5F, 0.05f, 0.866025F).color(color).alpha(alpha).uv(1.0f, 1.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(0.5F, 0.95f, 0.866025F).color(color).alpha(alpha).uv(1.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(-0.5F, 0.95f, -0.866025F).color(color).alpha(alpha).uv(0.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(-0.5F, 0.05f, -0.866025F).color(color).alpha(alpha).uv(0.0f, 1.0f).lightmap(light).normalAsPos());
            VERTEX_CACHE.put(hash, queue);
        } else {
            buildCacheVertexQueue(pack, renderContext, hash);
        }
    }

    public static void renderLaserBottom(BufferContext pack, RenderContext renderContext) {
        Color color = renderContext.color;
        float alpha = 1;
        int light = renderContext.packedLightIn;

        String hash = "LaserBottom";
        if(!VERTEX_CACHE.containsKey(hash)) {
            Queue<VertexAttribute> queue = Queues.newArrayDeque();
            queue.add(VertexAttribute.create().pos(-1.0F, 0.0F, 0.0F).color(color).alpha(0).uv(1.0f, 1.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(-1.0F, 0.05f, 0.0F).color(color).alpha(alpha).uv(1.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(1.0F, 0.05f, 0.0F).color(color).alpha(alpha).uv(0.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(1.0F, 0.0F, 0.0F).color(color).alpha(0).uv(0.0f, 1.0f).lightmap(light).normalAsPos());

            queue.add(VertexAttribute.create().pos(0.5F, 0.0F, -0.866025F).color(color).alpha(0).uv(1.0f, 1.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(0.5F, 0.05f, -0.866025F).color(color).alpha(alpha).uv(1.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(-0.5F, 0.05f, 0.866025F).color(color).alpha(alpha).uv(0.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(-0.5F, 0.0F, 0.866025F).color(color).alpha(0).uv(0.0f, 1.0f).lightmap(light).normalAsPos());

            queue.add(VertexAttribute.create().pos(0.5F, 0.0F, 0.866025F).color(color).alpha(0).uv(1.0f, 1.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(0.5F, 0.05f, 0.866025F).color(color).alpha(alpha).uv(1.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(-0.5F, 0.05f, -0.866025F).color(color).alpha(alpha).uv(0.0f, 0.0f).lightmap(light).normalAsPos());
            queue.add(VertexAttribute.create().pos(-0.5F, 0.0F, -0.866025F).color(color).alpha(0).uv(0.0f, 1.0f).lightmap(light).normalAsPos());
            VERTEX_CACHE.put(hash, queue);
        } else {
            buildCacheVertexQueue(pack, renderContext, hash);
        }
    }

    public static final VertexContext EMPTY_VERTEX_CONTEXT = new VertexContext();

    public static void renderStaticParticle(BufferContext pack, RenderContext renderContext) {
        callQuadVertexDynamic(pack, renderContext);
    }

    public static void renderParticle(BufferContext pack, RenderContext renderContext) {
        pack.matrixStack.pushPose();
        pack.matrixStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        renderStaticParticle(pack, renderContext);
        pack.matrixStack.popPose();
    }

    public static void renderPoint(BufferContext context, RenderContext renderContext, List<Vec3> vector3dList) {
        Color color = renderContext.color;
        int lightmap = renderContext.packedLightIn;

        String hash = "POINT_VERTEX_" + vector3dList.hashCode();

        if(!VERTEX_CACHE.containsKey(hash)) {
            Queue<VertexAttribute> attributes = Queues.newArrayDeque();
            for (Vec3 vector3d : vector3dList) {
                VertexAttribute attribute = new VertexAttribute();
                attribute.pos(vector3d);
                attribute.color(color);
                attribute.uv(0, 0);
                attribute.lightmap(lightmap);
                attribute.normalAsPos();
                attribute.alpha(1);
                attributes.add(attribute);
            }
            VERTEX_CACHE.put(hash, attributes);
        } else {
            buildCacheVertexQueue(context, renderContext, hash);
        }
    }

    public static void renderCubeCache(BufferContext context, RenderContext renderContext) {
        Color color = renderContext.color;
        float alpha = renderContext.alpha;
        int lightmap = renderContext.packedLightIn;

        String hash = "CUBE_VERTEX";

        if(!VERTEX_CACHE.containsKey(hash)) {
            Queue<VertexAttribute> vertexBuffers = Queues.newArrayDeque();
            for(int i=0; i<6; ++i)
                for(int j=0; j<4; ++j) {
                    float[] pos = vertex_list[index_list[i][j]];
                    float u = 1.0f;
                    float v = 1.0f;
                    if(j == 2 || j == 3)
                        u = 0.0f;
                    if(j == 1 || j == 2)
                        v = 0.0f;
                    VertexAttribute attribute = new VertexAttribute();
                    attribute.pos(pos[0], pos[1], pos[2]);
                    attribute.color(color);
                    attribute.alpha(1);
                    attribute.uv(u, v);
                    attribute.lightmap(lightmap);
                    attribute.normal(pos[0], pos[1], pos[2]);
                    vertexBuffers.add(attribute);
                }
            VERTEX_CACHE.put(hash, vertexBuffers);
        } else {
            buildCacheVertexQueue(context, renderContext, hash);
        }
    }

    public static void renderCubeDynamic(BufferContext context, RenderContext renderContext, float size) {
        Color color = renderContext.color;
        float alpha = renderContext.alpha;
        int lightmap = renderContext.packedLightIn;

        String hash = size+ "_SIZE_CUBE_VERTEX";

        if(!VERTEX_CACHE.containsKey(hash)) {
            Queue<VertexAttribute> vertexBuffers = Queues.newArrayDeque();
            for(int i=0; i<6; ++i)
                for(int j=0; j<4; ++j) {
                    float[] pos = vertex_list[index_list[i][j]];
                    float u = 1.0f;
                    float v = 1.0f;
                    if(j == 2 || j == 3)
                        u = 0.0f;
                    if(j == 1 || j == 2)
                        v = 0.0f;
                    VertexAttribute attribute = new VertexAttribute();
                    attribute.pos(pos[0]*size, pos[1]*size, pos[2]*size);
                    attribute.color(color);
                    attribute.alpha(1);
                    attribute.uv(u, v);
                    attribute.lightmap(lightmap);
                    attribute.normalAsPos();
                    vertexBuffers.add(attribute);
                }
            VERTEX_CACHE.put(hash, vertexBuffers);
        } else {
            buildCacheVertexQueue(context, renderContext, hash);
        }
    }

    public static void renderDecal(BufferContext context, RenderContext renderContext, float size, float normalScale, float x, float y, float z) {
        if(gl33()) {
            LIGHTING_INSTANCE_RENDERER.addInstanceAttrib((buffer -> {
                buffer.put(normalScale);
                buffer.put(size);

                buffer.put(renderContext.color.r());
                buffer.put(renderContext.color.g());
                buffer.put(renderContext.color.b());
                buffer.put(renderContext.alpha);

                buffer.put(x);
                buffer.put(y);
                buffer.put(z);
            }));
            return;
        }
        Color color = ModElements.ORIGIN_COLOR;
        if (renderContext.color != null)
            color = renderContext.color;

        Matrix4f matrix4f = context.matrixStack.last().pose();
        float alpha = renderContext.alpha;
        VertexConsumer buffer = context.buffer;
        for(int i=0; i<6; ++i)
            for(int j=0; j<4; ++j) {
                float[] pos = vertex_list[index_list[i][j]];
                buffer.vertex(matrix4f, pos[0]*normalScale, pos[1]*normalScale, pos[2]*normalScale).uv(normalScale*0.01f, size*0.01f).color(color.r(), color.g(), color.b(), alpha).normal(pos[0], pos[1], pos[2]).endVertex();
            }
    }

    public static void renderSphereCache(BufferContext pack, RenderContext renderContext, int stacks) {
        pack.matrixStack.pushPose();
        String hash =stacks +"_Sphere";

        if(!VERTEX_CACHE.containsKey(hash)) {
            Queue<VertexAttribute> cylinderQueue = Polyhedron.drawPolyhedron(renderContext, EMPTY_VERTEX_CONTEXT, stacks);
            VERTEX_CACHE.put(hash, cylinderQueue);
            end(pack);
        } else {
            buildCacheVertexQueue(pack, renderContext, hash);
        }
        pack.matrixStack.popPose();
    }

    public static void renderSphereDynamic(BufferContext pack, RenderContext renderContext, VertexContext vertexContext, int stacks) {
        if(!vertexContext.shake) {
            renderSphereCache(pack, renderContext, stacks);
            return;
        }
        pack.matrixStack.pushPose();
        Queue<VertexAttribute> cylinderQueue = Polyhedron.drawPolyhedron(renderContext, vertexContext, stacks);
        renderVertexQueueDynamic(pack, cylinderQueue);
        pack.matrixStack.popPose();
    }

    public static boolean shouldRender(AABB aabbIn) {
        if(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null) return false;
        Level world = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        Vec3 start = player.getEyePosition(Minecraft.getInstance().getFrameTime());
        return !hasSolidBlock(world, start, getAABBPoints(start, aabbIn));
    }

    public static boolean shouldRender(LitParticle particle) {
        if(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null) return false;
        if (true) return true;
        Level world = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        Vec3 start = player.getEyePosition(Minecraft.getInstance().getFrameTime());
        return !hasSolidBlock(world, start, particle.centerVec());
    }

    public static boolean hasSolidBlock(Level level, Vec3 start, Queue<Vec3> endPoints) {
        if(endPoints.isEmpty()) return false;
        HashSet<BlockPos> air = new HashSet<>();
        Iterator<Vec3> it = endPoints.iterator();
        while (it.hasNext()) {
            Vec3 end = it.next();
            int dis = (int) (start.distanceTo(end)) / 2 + 1;
            Vec3 back = start.subtract(end).normalize();
            Vec3 front = back.scale(-1);
            for(int i = 0; i <= dis; ++i) {
                BlockPos pos = new BlockPos(front.scale(i).add(start));
                if(!air.contains(pos)) {
                    if(!level.getBlockState(pos).getVisualShape(level, pos, CollisionContext.empty()).isEmpty()) {
                        it.remove();
                        break;
                    }
                    air.add(pos);
                }

                pos = new BlockPos(back.scale(i).add(end));
                if(!air.contains(pos)) {
                    if(!level.getBlockState(pos).getVisualShape(level, pos, CollisionContext.empty()).isEmpty()) {
                        it.remove();
                        break;
                    }
                    air.add(pos);
                }
            }
        }
        return endPoints.isEmpty();
    }

    public static boolean hasSolidBlock(Level level, Vec3 start, Vec3 end) {
        int stacks = 0;
        int dis = (int) (start.distanceTo(end)) / 2 + 1;
        Vec3 back = start.subtract(end).normalize();
        Vec3 front = back.scale(-1);
        for(int i = 0; i <= dis; ++i) {
            BlockPos pos = new BlockPos(front.scale(i).add(start));
            if(!level.getBlockState(new BlockPos(pos)).getVisualShape(level, pos, CollisionContext.empty()).isEmpty()) {
                stacks+=1;
                if(stacks >= 2)
                    return true;
            }
            pos = new BlockPos(back.scale(i).add(end));
            if(!level.getBlockState(new BlockPos(pos)).getVisualShape(level, pos, CollisionContext.empty()).isEmpty()) {
                stacks+=1;
                if(stacks >= 2)
                    return true;
            }
        }
        return false;
    }

    public static Queue<Vec3> getAABBPoints(Vec3 start, AABB aabbIn) {
        Vec3 center = aabbIn.getCenter();
        HashSet<Direction> opposite = new HashSet<>();

        Queue<Vec3> queue = Queues.newArrayDeque();
        queue.add(new Vec3(aabbIn.minX, aabbIn.minY, aabbIn.minZ));
        queue.add(new Vec3(aabbIn.maxX, aabbIn.minY, aabbIn.minZ));
        queue.add(new Vec3(aabbIn.minX, aabbIn.maxY, aabbIn.minZ));
        queue.add(new Vec3(aabbIn.minX, aabbIn.minY, aabbIn.maxZ));
        queue.add(new Vec3(aabbIn.maxX, aabbIn.maxY, aabbIn.minZ));
        queue.add(new Vec3(aabbIn.minX, aabbIn.maxY, aabbIn.maxZ));
        queue.add(new Vec3(aabbIn.maxX, aabbIn.minY, aabbIn.maxZ));
        queue.add(new Vec3(aabbIn.maxX, aabbIn.maxY, aabbIn.maxZ));

        Iterator<Vec3> it = queue.iterator();
        while (it.hasNext()) {
            Vec3 vec = it.next();
            boolean notCulling = false;
            for(Direction direction : findNearestPoints(vec.subtract(center))) {
                if(opposite.contains(direction))
                    continue;
                Vec3 normal = Vec3.atLowerCornerOf(direction.getNormal());
                if(faceCulling(start, center.add(normal), normal))
                    opposite.add(direction);
                else
                    notCulling = true;
            }
            if(!notCulling) {
                it.remove();
            }
        }
        return queue;
    }

    public static Queue<Direction> findNearestPoints(Vec3 start) {

        Map<Direction, Double> distances = new HashMap<>();
        double far = 0;
        start = start.normalize().scale(3);

        for (Direction p : Direction.values()) {
            double distance = start.distanceTo(Vec3.atLowerCornerOf(p.getNormal()));
            distances.put(p, distance);
            if(distance > far)
                far = distance;
        }

        Queue<Direction> result = Queues.newArrayDeque();
        int count = 0;
        for (Map.Entry<Direction, Double> entry : distances.entrySet()) {
            if(entry.getValue() < far) {
                result.add(entry.getKey());
                count++;
                if (count == 3) {
                    break;
                }
            }
        }

        return result;
    }

    public static boolean faceCulling(Vec3 start, Vec3 point, Vec3 normal) {
        double a = normal.x, b = normal.y, c = normal.z;
        double d = - (a * point.x + b * point.y + c * point.z);

        double x0 = start.x, y0 = start.y, z0 = start.z;
        double t = - (a * x0 + b * y0 + c * z0 + d) / (a * a + b * b + c * c);

        double x = x0 + a * t;
        double y = y0 + b * t;
        double z = z0 + c * t;
        Vec3 foot = new Vec3(x, y, z);
        return start.subtract(foot).dot(normal) <= 0;
    }

    public static VertexAttribute calculateVertex(float x, float y, float z, float texU, float TexV, RenderContext context, VectorHitReaction[] hitReaction, float limit) {
        float maxAlhpa = 0;

        if (hitReaction != null)
            for (int c = 0; c < hitReaction.length; c++) {
                VectorHitReaction reaction = hitReaction[c];
                float add = reaction.IsHit(new Vec3(x, y, z));
                if (add > maxAlhpa)
                    maxAlhpa = add;
            }


        float redScale = context.color.r() + (context.color.r() * maxAlhpa * 2);
        float greenScale = context.color.g() + (context.color.g() * maxAlhpa * 2);
        float blueScale = context.color.b() + (context.color.b() * maxAlhpa * 2);

        if (redScale > 1.0f) redScale = 1.0f;
        if (greenScale > 1.0f) greenScale = 1.0f;
        if (blueScale > 1.0f) blueScale = 1.0f;

        float scale = 1f - maxAlhpa * limit;
        if(scale < 0)
            scale = 0;

        VertexAttribute vertexAttribute = new VertexAttribute();
        vertexAttribute.pos(x * scale, y * scale, z * scale);
        if(limit < 0)
            vertexAttribute.normal(x * limit, y * limit, z * limit);
        else
            vertexAttribute.normal(x * scale, y * scale, z * scale);
        vertexAttribute.uv(texU, TexV);
        vertexAttribute.alpha(context.alpha);
        vertexAttribute.color(Color.create(redScale, greenScale, blueScale));
        vertexAttribute.lightmap(context.packedLightIn);
        return vertexAttribute;
    }

    public static void drawShape(PoseStack matrixStackIn, VertexConsumer bufferIn, VoxelShape shapeIn, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha) {
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

        public static VertexAttribute create() {
            return new VertexAttribute();
        }

        public VertexAttribute pos(float x, float y, float z) {
            this.posX = x;
            this.posY = y;
            this.posZ = z;
            return this;
        }

        public VertexAttribute lightmap(int lightmap) {
            this.lightmap = lightmap;
            return this;
        }

        public VertexAttribute alpha(float alpha) {
            this.alpha = alpha;
            return this;
        }

        public VertexAttribute color(Color color) {
            this.color = color;
            return this;
        }

        public Color color() {
            return color;
        }

        public VertexAttribute pos(Vec3 vec) {
            this.posX = (float) vec.x;
            this.posY = (float) vec.y;
            this.posZ = (float) vec.z;
            return this;
        }

        public VertexAttribute pos(Vector3f vec) {
            this.posX = vec.x();
            this.posY = vec.y();
            this.posZ = vec.z();
            return this;
        }

        public VertexAttribute normalAsPos() {
            this.normalX = posX;
            this.normalY = posY;
            this.normalZ = posZ;
            return this;
        }

        public VertexAttribute normal(float x, float y, float z) {
            this.normalX = x;
            this.normalY = y;
            this.normalZ = z;
            return this;
        }

        public VertexAttribute normal(Vec3 vec) {
            this.normalX = (float) vec.x;
            this.normalY = (float) vec.y;
            this.normalZ = (float) vec.z;
            return this;
        }

        public VertexAttribute normal(Vector3f vec) {
            this.normalX = vec.x();
            this.normalY = vec.y();
            this.normalZ = vec.z();
            return this;
        }

        public VertexAttribute uv(float u, float v) {
            this.texU = u;
            this.texV = v;
            return this;
        }

        public VertexAttribute uv(Vec2 uv) {
            this.texU = uv.x;
            this.texV = uv.y;
            return this;
        }

        public void build(Matrix4f matrix4f, VertexConsumer vertexConsumer, @Nullable RenderContext render, @Nullable TextureAtlasSprite sprite) {
            vertexConsumer.getVertexFormat().getElements().forEach(element -> {
                if(element == DefaultVertexFormat.ELEMENT_POSITION)
                    vertexConsumer.vertex(matrix4f, this.posX, this.posY, this.posZ);
                if(element == DefaultVertexFormat.ELEMENT_COLOR)
                    if(render!=null) {
                        vertexConsumer.color(render.color.r(), render.color.g(), render.color.b(),
                                render.scaleAlpha ? render.alpha*this.alpha : render.alpha);
                    } else
                        vertexConsumer.color(this.color.r(), this.color.g(), this.color.b(), this.alpha);
                if(element == DefaultVertexFormat.ELEMENT_UV0 || element == DefaultVertexFormat.ELEMENT_UV) {
                    if(RenderHelper.isRenderingWorld() && sprite != null) {
                        vertexConsumer.uv(getU(sprite, texU), getV(sprite, texV));
                    } else
                        vertexConsumer.uv(texU, texV);
                }
                if(element == DefaultVertexFormat.ELEMENT_UV1)
                    vertexConsumer.overlayCoords(OverlayTexture.NO_OVERLAY);
                if(element == DefaultVertexFormat.ELEMENT_UV2)
                    if(render!=null)
                        vertexConsumer.uv2(render.packedLightIn);
                    else
                        vertexConsumer.uv2(this.lightmap);
                if(element == DefaultVertexFormat.ELEMENT_NORMAL)
                    vertexConsumer.normal(this.normalX, this.normalY, this.normalZ);
            });
            vertexConsumer.endVertex();
        }

        public void build(Matrix4f matrix4f, VertexConsumer vertexConsumer, RenderContext render) {
            build(matrix4f, vertexConsumer, render, null);
        }
    }

    public static float getU(TextureAtlasSprite sprite, float u) {
        float sub = sprite.getU1() - sprite.getU0();
        return sprite.getU0() + sub * u;
    }

    public static float getV(TextureAtlasSprite sprite, float v) {
        float sub = sprite.getV1() - sprite.getV0();
        return sprite.getV0() + sub * v;
    }

    public static boolean isRenderTypeGlint(RenderType renderType) {
        return renderType.toString().contains("GlintType");
    }

    public static boolean isRenderTypeInstanced(RenderType renderType) {
        return renderType.toString().contains("Instanced");
    }

    public static boolean isRenderTypeLightingInstanced(RenderType renderType) {
        return renderType.toString().contains("LightingInstanced");
    }

    public static ResourceLocation getRenderTypeTexture(RenderType renderType) {
        RenderType.CompositeState state = ((AccessorRenderType)renderType).getState();
        RenderStateShard.EmptyTextureStateShard textureState = ((AccessorTextureState)(Object)state).getTextureState();
        if(!(textureState instanceof RenderStateShard.TextureStateShard))
            return null;
        Optional<ResourceLocation> texture = ((AccessorTexture)textureState).getTexture();
        return texture.orElse(null);
    }

    public static void setRenderTypeTexture(RenderType renderType, ResourceLocation tex) {
        RenderType.CompositeState state = ((AccessorRenderType)renderType).getState();
        RenderStateShard.EmptyTextureStateShard textureState = ((AccessorTextureState)(Object)state).getTextureState();
        if(!(textureState instanceof RenderStateShard.TextureStateShard))
            return;
        ((AccessorTexture)textureState).setTexture(tex);
    }

    public static ResourceLocation getRenderRealTexture(RenderType renderType) {
        RenderType.CompositeState state = ((AccessorRenderType)renderType).getState();
        RenderStateShard.EmptyTextureStateShard textureState = ((AccessorTextureState)(Object)state).getTextureState();
        if(!(textureState instanceof RenderStateShard.TextureStateShard))
            return null;
        Optional<ResourceLocation> texture = ((AccessorTexture)textureState).getRealTexture();
        return texture.orElse(null);
    }

    public static TextureAtlasSprite getRenderTypeSpirit(RenderType renderType) {
        ResourceLocation res = RenderHelper.isRenderTypeGlint(renderType)? getRenderTypeTexture(renderType):getRenderRealTexture(renderType);
        return TEXTURE_ATLAS.getSprite(TEXTURE_MAP.get(res));
    }

    public static void begin(BufferContext bufferContext) {
        if(!queueMode && !bufferContext.buffer.building()) {
            addTexture(getRenderTypeTexture(bufferContext.type));
            bufferContext.buffer.begin(bufferContext.type.mode(), bufferContext.type.format());
        }
    }

    public static void finish(BufferContext bufferContext) {
        BufferBuilder buffer = bufferContext.buffer;
        if(!queueMode && buffer.building())
            bufferContext.type.end(buffer, 0, 0, 0);
    }

    public static void setup(BufferContext bufferContext) {
        if(stopShader()) return;
        if(!queueMode && !bufferContext.renderShader.isEmpty()) {
            PostChain shader = ShaderEvent.getShaders(new ResourceLocation(bufferContext.renderShader.shaders[0]));
            if(shader != null) {
                RenderTarget framebuffer = shader.getTempTarget(
                        Objects.requireNonNull(ShaderEvent.getShaderFrameName(bufferContext.renderShader.shaders[0])));
                framebuffer.bindWrite(false);
                ShaderEvent.pushRender(bufferContext.renderShader.shaders[0]);
            }
        }
    }

    public static void end(BufferContext bufferContext) {
        if(stopShader()) return;
        if(!queueMode && !bufferContext.renderShader.isEmpty()) {
            PostChain shader = ShaderEvent.getShaders(new ResourceLocation(bufferContext.renderShader.shaders[0]));
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
        scale = 1d;
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

    public static void setViewMatrix(PoseStack matrixStack) {
        viewMatrix = matrixStack;
    }

    public static PoseStack getViewMatrix() {
        return viewMatrix;
    }

    public static void setModelMatrix(PoseStack matrixStack) {
        modelMatrix = matrixStack;
    }

    public static PoseStack getModelMatrix() {
        return modelMatrix;
    }

    public static void setVertexMatrix(PoseStack matrixStack) {
        vertexMatrix = matrixStack;
    }

    public static PoseStack getVertexMatrix() {
        return vertexMatrix;
    }

    public static void setProjectionMatrix4f(Matrix4f matrix4f) {
        projectionMatrix4f = matrix4f;
    }

    public static Matrix4f getProjectionMatrix4f() {
        return projectionMatrix4f;
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

    public static boolean gl33() {
        if(!ClientConfig.INSTANCED_RENDERING.get()) return false;
        if(RenderSystem.isOnRenderThread()) {
            if(gl33 < 0)
                gl33 = (GL.getCapabilities().OpenGL33 || Checks.checkFunctions(GL.getCapabilities().glVertexAttribDivisor)) ? 1 : 0;
        }

        return RenderHelper.gl33 == 1;
    }
    public static boolean stopShader() {
        return !ClientConfig.POST_PROCESSING_EFFECTS.get();
    }

    public static boolean enableColorLighting() {
        return ClientConfig.COLOR_LIGHTING_EFFECTS.get();
    }

    public static void setMagickCoreUniform(ShaderInstance shader) {
        IManaShader shaderInstance = (IManaShader) shader;
        if (shaderInstance.getViewMat() != null) {
            shaderInstance.getViewMat().set(RenderHelper.getViewMatrix().last().pose());
        }
        if (shaderInstance.getModelMat() != null) {
            shaderInstance.getModelMat().set(RenderHelper.getModelMatrix().last().pose());
        }
        if (shaderInstance.getIViewProjMat() != null) {
            Matrix4f view = RenderHelper.getViewMatrix().last().pose().copy();
            Matrix4f proj = RenderSystem.getProjectionMatrix().copy();
            proj.multiply(view);
            if(proj.invert())
                shaderInstance.getIViewProjMat().set(proj);
        }
        if (shaderInstance.getIViewMat() != null) {
            Matrix4f matrix4f = RenderHelper.getViewMatrix().last().pose().copy();
            if(matrix4f.invert())
                shaderInstance.getIViewMat().set(matrix4f);
        }
        if (shaderInstance.getIModelMat() != null) {
            Matrix4f modelMat = RenderHelper.getModelMatrix().last().pose().copy();
            if(modelMat.invert())
                shaderInstance.getIModelMat().set(modelMat);
        }
        if(shaderInstance.getPosScale() != null) {
            shaderInstance.getPosScale().set(RenderHelper.getPosScale());
        }
        if(shaderInstance.getCameraPos() != null) {
            Vec3 pos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            float[] array = new float[]{(float) pos.x, (float) pos.y, (float) pos.z};
            shaderInstance.getCameraPos().set(array);
        }
        if(shaderInstance.getCameraDirection() != null) {
            Vector3f pos = Minecraft.getInstance().gameRenderer.getMainCamera().getLookVector();
            float[] array = new float[]{pos.x(), pos.y(), pos.z()};
            shaderInstance.getCameraDirection().set(array);
        }
        if(shaderInstance.getCameraOrientation() != null) {
            Quaternion quat = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
            shaderInstance.getCameraOrientation().set(new Vector4f(quat.i(), quat.j(), quat.k(), -quat.r()));
        }

        if(shader == RenderHelper.getRendertypeEntityLightShader()) {
            Window window = Minecraft.getInstance().getWindow();
            Matrix4f proj = Matrix4f.orthographic(0.0F, (float)((double)window.getWidth() / window.getGuiScale()), 0.0F, (float)((double)window.getHeight() / window.getGuiScale()), 1000.0F, net.minecraftforge.client.ForgeHooksClient.getGuiFarPlane());
            Matrix4f view = new Matrix4f();
            view.setIdentity();
            view.multiplyWithTranslation(0, 0, 1000F-net.minecraftforge.client.ForgeHooksClient.getGuiFarPlane());

            if (shader.PROJECTION_MATRIX != null) {
                shader.PROJECTION_MATRIX.set(proj);
            }

            if (shaderInstance.getViewMat() != null) {
                shaderInstance.getViewMat().set(view);
            }

            if(shader.SCREEN_SIZE != null) {
                shader.SCREEN_SIZE.set(window.getWidth()/3f, window.getHeight()/3f);
            }
        }
    }

    public static void setMagickCoreUniform() {
        setMagickCoreUniform(RenderSystem.getShader());
    }

    private static boolean checkShader() {
        initOculusClazz();
        if(hasOculus()) {
            try {
                Field field = oculusShader.getDeclaredField("INSTANCE");
                field.setAccessible(true);
                Object loaded = field.get(null);
                if(loaded != null) {
                    Method method = loaded.getClass().getDeclaredMethod("isShaderPackInUse");
                    boolean using = (boolean) method.invoke(loaded);
                    if (using && !oculusShaderLoaded) {
                        MagickCore.LOGGER.info("Oculus shader loaded");
                        VERTEX_CACHE.clear();
                    } else if (!using && oculusShaderLoaded) {
                        MagickCore.LOGGER.info("Oculus shader unload");
                        VERTEX_CACHE.clear();
                    }
                    return using;
                } else {
                    return false;
                }
            } catch (InvocationTargetException | NoSuchMethodException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void checkRenderingShader() {
        oculusShaderLoaded = checkShader();
    }

    public static boolean isRenderingShader() {
        return oculusShaderLoaded;
    }

    public static boolean hasOculus() {
        return oculusShader != null;
    }

    public static void initOculusClazz() {
        if(!checkIris) {
            checkIris = true;
            try {
                oculusShader = Class.forName("net.coderbot.iris.apiimpl.IrisApiV0Impl");
            } catch (ClassNotFoundException e) {
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static Player getPlayer() {
        return Minecraft.getInstance().player;
    }

    public enum Noise {
        NOISE(new ResourceLocation(MagickCore.MOD_ID, "textures/noise.png")),
        LINE(new ResourceLocation(MagickCore.MOD_ID, "textures/line.png")),
        DISSOVE(new ResourceLocation(MagickCore.MOD_ID, "textures/dissove.png")),
        STRING(new ResourceLocation(MagickCore.MOD_ID, "textures/string.png"));

        private final ResourceLocation res;
        Noise(ResourceLocation res){
            this.res = res;
        }

        public ResourceLocation res() {
            return res;
        }
    }
}
