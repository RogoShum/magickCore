package com.rogoshum.magickcore.mixin;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.rogoshum.magickcore.api.render.IManaShader;
import com.rogoshum.magickcore.api.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;

@Mixin(ShaderInstance.class)
public abstract class MixinShaderInstance implements IManaShader {
    @Shadow
    @Nullable
    public Uniform getUniform(String p_173349_) {
        return null;
    }

    @Shadow @Final private Map<String, Object> samplerMap;

    @Shadow public abstract void markDirty();

    @Nullable
    public Uniform I_VIEW_PROJ_MAT;
    @Nullable
    public Uniform I_VIEW_MAT;
    @Nullable
    public Uniform VIEW_MAT;
    @Nullable
    public Uniform MODEL_MAT;
    @Nullable
    public Uniform I_MODEL_MAT;
    @Nullable
    public Uniform POS_SCALE;
    @Nullable
    public Uniform CAMERA_POS;
    @Nullable
    public Uniform CAMERA_DIR;
    @Nullable
    public Uniform CAMERA_ORIENT;

    @Inject(at = @At("TAIL"), method = "setSampler")
    public void construct(String samplerName, Object id, CallbackInfo ci) {
        if(Objects.equals(samplerName, "Sampler0") && RenderHelper.GLOBAL_TEXTURE != null) {
            TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
            AbstractTexture abstracttexture = texturemanager.getTexture(RenderHelper.GLOBAL_TEXTURE);
            this.samplerMap.put(samplerName, abstracttexture.getId());
            this.markDirty();
        }
    }

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/VertexFormat;)V")
    public void construct(ResourceProvider p_173336_, ResourceLocation shaderLocation, VertexFormat p_173338_, CallbackInfo ci) {
        this.MODEL_MAT = this.getUniform("ModelMat");
        this.VIEW_MAT = this.getUniform("ViewMat");
        this.I_MODEL_MAT = this.getUniform("IModelMat");
        this.I_VIEW_PROJ_MAT = this.getUniform("IViewProjMat");
        this.I_VIEW_MAT = this.getUniform("IViewMat");
        this.POS_SCALE = this.getUniform("PosScale");
        this.CAMERA_POS = this.getUniform("CameraPos");
        this.CAMERA_DIR = this.getUniform("CameraDir");
        this.CAMERA_ORIENT = this.getUniform("CameraOrientation");
    }

    @Override
    public Uniform getIViewProjMat() {
        return I_VIEW_PROJ_MAT;
    }

    @Override
    public Uniform getViewMat() {
        return VIEW_MAT;
    }

    @Override
    public Uniform getModelMat() {
        return MODEL_MAT;
    }

    @Override
    public Uniform getIViewMat() {
        return I_VIEW_MAT;
    }

    @Override
    public Uniform getIModelMat() {
        return I_MODEL_MAT;
    }

    @Override
    public Uniform getPosScale() {
        return POS_SCALE;
    }

    @Override
    public Uniform getCameraPos() {
        return CAMERA_POS;
    }
    @Override
    public Uniform getCameraDirection() {
        return CAMERA_DIR;
    }

    @Override
    public Uniform getCameraOrientation() {
        return CAMERA_ORIENT;
    }

    /*
        @Mixin(LevelRenderer.class)
    public static class MixinLevelRenderer {
        @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setupShaderLights(Lnet/minecraft/client/renderer/ShaderInstance;)V"), method = "renderChunkLayer")
        public void shader(RenderType p_172994_, PoseStack p_172995_, double p_172996_, double p_172997_, double p_172998_, Matrix4f p_172999_, CallbackInfo ci) {
            RenderHelper.setMagickCoreUniform();
        }
    }
     */

    @Mixin(BufferUploader.class)
    public static class MixinBufferUploader {
        @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setupShaderLights(Lnet/minecraft/client/renderer/ShaderInstance;)V"), method = "_end")
        private static void shader(ByteBuffer p_166839_, VertexFormat.Mode p_166840_, VertexFormat p_166841_, int p_166842_, VertexFormat.IndexType p_166843_, int p_166844_, boolean p_166845_, CallbackInfo ci) {
            RenderHelper.setMagickCoreUniform();
        }
    }

    /*
        @Mixin(VertexBuffer.class)
    public static class MixinVertexBuffer {
        @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setupShaderLights(Lnet/minecraft/client/renderer/ShaderInstance;)V"), method = "_drawWithShader")
        public void shader(Matrix4f p_166877_, Matrix4f p_166878_, ShaderInstance p_166879_, CallbackInfo ci) {
            RenderHelper.setMagickCoreUniform();
        }
    }
     */
}
