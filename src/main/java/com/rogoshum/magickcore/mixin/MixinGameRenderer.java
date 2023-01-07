package com.rogoshum.magickcore.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.api.event.RenderWorldEvent;
import com.rogoshum.magickcore.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer{
    //, target = "net/minecraft/client/Minecraft.getMainWindow ()Lnet/minecraft/client/MainWindow;", ordinal = 6
    @Inject(method = "updateCameraAndRender", at = @At(value = "TAIL"))
    public void onFabulousRender(float partialTicks, long nanoTime, boolean renderWorldIn, CallbackInfo ci) {
        /*
        if (Minecraft.isFabulousGraphicsEnabled()) return;
        MatrixStack matrixStack = new MatrixStack();
        ActiveRenderInfo activerenderinfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
        net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup cameraSetup = net.minecraftforge.client.ForgeHooksClient.onCameraSetup((GameRenderer)(Object) this, activerenderinfo, partialTicks);
        activerenderinfo.setAnglesInternal(cameraSetup.getYaw(), cameraSetup.getPitch());
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(cameraSetup.getRoll()));

        matrixStack.rotate(Vector3f.XP.rotationDegrees(activerenderinfo.getPitch()));
        matrixStack.rotate(Vector3f.YP.rotationDegrees(activerenderinfo.getYaw() + 180.0F));
        MatrixStack matrix = new MatrixStack();
        matrix.getLast().getMatrix().mul(Minecraft.getInstance().gameRenderer.getProjectionMatrix(activerenderinfo, partialTicks, true));
        Matrix4f matrix4f = matrix.getLast().getMatrix();
        Minecraft.getInstance().gameRenderer.resetProjectionMatrix(matrix4f);
        MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.PreRenderMagickEvent(Minecraft.getInstance().worldRenderer, matrixStack, partialTicks, matrix4f));
        MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.RenderMagickEvent(Minecraft.getInstance().worldRenderer, matrixStack, partialTicks, matrix4f));
        MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.PostRenderMagickEvent(Minecraft.getInstance().worldRenderer, matrixStack, partialTicks, matrix4f));

         */
    }
}
