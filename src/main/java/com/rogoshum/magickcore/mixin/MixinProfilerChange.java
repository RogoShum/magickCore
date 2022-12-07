package com.rogoshum.magickcore.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.api.event.ProfilerChangeEvent;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldRenderer.class)
public class MixinProfilerChange{

    @Inject(method = "updateCameraAndRender",
            at = @At(
            value = "INVOKE",
            target = "net/minecraft/profiler/IProfiler.endStartSection (Ljava/lang/String;)V",
            ordinal = 8)
            )
    private void onTerrainSection(MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci1) {
        MinecraftForge.EVENT_BUS.post(new ProfilerChangeEvent("terrain"));
    }

    @Inject(method = "updateCameraAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/profiler/IProfiler.endStartSection (Ljava/lang/String;)V",
                    ordinal = 9)
    )
    private void onEntitiesSection(MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci1) {
        MinecraftForge.EVENT_BUS.post(new ProfilerChangeEvent("entities"));
    }

    @Inject(method = "updateCameraAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/profiler/IProfiler.endStartSection (Ljava/lang/String;)V",
                    ordinal = 10)
    )
    private void onBlockEntitiesSection(MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci1) {
        MinecraftForge.EVENT_BUS.post(new ProfilerChangeEvent("blockentities"));
    }

    @Inject(method = "updateCameraAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/profiler/IProfiler.endStartSection (Ljava/lang/String;)V",
                    ordinal = 4)
    )
    private void onSkySection(MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci1) {
        MinecraftForge.EVENT_BUS.post(new ProfilerChangeEvent("sky"));
    }

    @Inject(method = "updateCameraAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/profiler/IProfiler.endStartSection (Ljava/lang/String;)V",
                    ordinal = 5)
    )
    private void onFogSection(MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci1) {
        MinecraftForge.EVENT_BUS.post(new ProfilerChangeEvent("fog"));
    }

    @Inject(method = "updateCameraAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/profiler/IProfiler.endStartSection (Ljava/lang/String;)V",
                    ordinal = 11)
    )
    private void onDestroyProgressSection(MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci1) {
        MinecraftForge.EVENT_BUS.post(new ProfilerChangeEvent("destroyProgress"));
    }

    @Inject(method = "updateCameraAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/profiler/IProfiler.endStartSection (Ljava/lang/String;)V",
                    ordinal = 13)
    )
    private void onTranslucentSection(MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci1) {
        MinecraftForge.EVENT_BUS.post(new ProfilerChangeEvent("translucent"));
    }

    @Inject(method = "updateCameraAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/profiler/IProfiler.endStartSection (Ljava/lang/String;)V",
                    ordinal = 14)
    )
    private void onStringSection(MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci1) {
        MinecraftForge.EVENT_BUS.post(new ProfilerChangeEvent("string"));
    }

    @Inject(method = "updateCameraAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/profiler/IProfiler.endStartSection (Ljava/lang/String;)V",
                    ordinal = 12)
    )
    private void onOutlineSection(MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci1) {
        MinecraftForge.EVENT_BUS.post(new ProfilerChangeEvent("outline"));
    }

    @Inject(method = "updateCameraAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/profiler/IProfiler.endStartSection (Ljava/lang/String;)V",
                    ordinal = 16)
    )
    private void onTranslucentSection1(MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci1) {
        MinecraftForge.EVENT_BUS.post(new ProfilerChangeEvent("translucent"));
    }

    @Inject(method = "updateCameraAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/profiler/IProfiler.endStartSection (Ljava/lang/String;)V",
                    ordinal = 17)
    )
    private void onStringSection1(MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci1) {
        MinecraftForge.EVENT_BUS.post(new ProfilerChangeEvent("string"));
    }

    @Inject(method = "updateCameraAndRender",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/profiler/IProfiler.endStartSection (Ljava/lang/String;)V",
                    ordinal = 15)
    )
    private void onParticlesSection(MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci1) {
        MinecraftForge.EVENT_BUS.post(new ProfilerChangeEvent("particles"));
    }
}
