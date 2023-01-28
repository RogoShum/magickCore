package com.rogoshum.magickcore.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.RenderLevelEvent;
import com.rogoshum.magickcore.client.RenderHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRender {

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/PostChain.process (F)V", ordinal = 1))
    public void onFabulousRender(PoseStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, Camera activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci) {
        RenderHelper.setRenderingLevel(true);
        MagickCore.EVENT_BUS.post(new RenderLevelEvent.PreRenderMagickEvent(Minecraft.getInstance().levelRenderer, matrixStackIn, partialTicks, projectionIn));
        MagickCore.EVENT_BUS.post(new RenderLevelEvent.RenderMagickEvent(Minecraft.getInstance().levelRenderer, matrixStackIn, partialTicks, projectionIn));
        MagickCore.EVENT_BUS.post(new RenderLevelEvent.PostRenderMagickEvent(Minecraft.getInstance().levelRenderer, matrixStackIn, partialTicks, projectionIn));
        RenderHelper.setRenderingLevel(false);
    }

    @Inject(method = "renderLevel", at = @At(value = "TAIL"))
    public void onNormalRender(PoseStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, Camera activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn, CallbackInfo ci) {
        if (Minecraft.useShaderTransparency()) return;
        RenderHelper.setRenderingLevel(true);
        MagickCore.EVENT_BUS.post(new RenderLevelEvent.PreRenderMagickEvent(Minecraft.getInstance().levelRenderer, matrixStackIn, partialTicks, projectionIn));
        MagickCore.EVENT_BUS.post(new RenderLevelEvent.RenderMagickEvent(Minecraft.getInstance().levelRenderer, matrixStackIn, partialTicks, projectionIn));
        MagickCore.EVENT_BUS.post(new RenderLevelEvent.PostRenderMagickEvent(Minecraft.getInstance().levelRenderer, matrixStackIn, partialTicks, projectionIn));
        RenderHelper.setRenderingLevel(false);
    }
}
