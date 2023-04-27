package com.rogoshum.magickcore.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.api.event.RenderWorldEvent;
import com.rogoshum.magickcore.api.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.LevelRenderer;
import com.mojang.math.Matrix4f;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinWorldRender {

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/PostChain.process (F)V", ordinal = 1))
    public void onFabulousRender(PoseStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, Camera activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci) {
        RenderHelper.setRenderingWorld(true);
        MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.PreRenderMagickEvent(Minecraft.getInstance().levelRenderer, matrixStackIn, partialTicks, projectionIn));
        MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.RenderMagickEvent(Minecraft.getInstance().levelRenderer, matrixStackIn, partialTicks, projectionIn));
        MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.PostRenderMagickEvent(Minecraft.getInstance().levelRenderer, matrixStackIn, partialTicks, projectionIn));
        RenderHelper.setRenderingWorld(false);
    }

    @Inject(method = "renderLevel", at = @At(value = "TAIL"))
    public void onNormalRender(PoseStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, Camera activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci) {
        if (Minecraft.useShaderTransparency()) return;
        RenderHelper.setRenderingWorld(true);
        MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.PreRenderMagickEvent(Minecraft.getInstance().levelRenderer, matrixStackIn, partialTicks, projectionIn));
        MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.RenderMagickEvent(Minecraft.getInstance().levelRenderer, matrixStackIn, partialTicks, projectionIn));
        MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.PostRenderMagickEvent(Minecraft.getInstance().levelRenderer, matrixStackIn, partialTicks, projectionIn));
        RenderHelper.setRenderingWorld(false);
    }
}
