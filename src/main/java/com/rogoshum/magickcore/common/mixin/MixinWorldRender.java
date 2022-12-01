package com.rogoshum.magickcore.common.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.api.event.RenderWorldEvent;
import net.minecraft.client.Minecraft;
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

@Mixin(WorldRenderer.class)
public class MixinWorldRender {

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "net/minecraft/client/shader/ShaderGroup.render (F)V", ordinal = 1))
    public void onFabulousRender(MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.PreRenderMagickEvent(Minecraft.getInstance().worldRenderer, matrixStackIn, partialTicks, projectionIn));
        MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.RenderMagickEvent(Minecraft.getInstance().worldRenderer, matrixStackIn, partialTicks, projectionIn));
        MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.PostRenderMagickEvent(Minecraft.getInstance().worldRenderer, matrixStackIn, partialTicks, projectionIn));
    }

    @Inject(method = "updateCameraAndRender", at = @At(value = "TAIL"))
    public void onNormalRender(MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci) {
        if (Minecraft.isFabulousGraphicsEnabled()) return;
        MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.PreRenderMagickEvent(Minecraft.getInstance().worldRenderer, matrixStackIn, partialTicks, projectionIn));
        MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.RenderMagickEvent(Minecraft.getInstance().worldRenderer, matrixStackIn, partialTicks, projectionIn));
        MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.PostRenderMagickEvent(Minecraft.getInstance().worldRenderer, matrixStackIn, partialTicks, projectionIn));
    }
}
