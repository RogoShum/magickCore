package com.rogoshum.magickcore.mixin;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameRenderer.class)
public class MixinGameRenderer{
    //, target = "net/minecraft/client/Minecraft.getMainWindow ()Lnet/minecraft/client/MainWindow;", ordinal = 6
    /*
    @Inject(method = "renderLevel", at = @At(value = "TAIL"))
    public void onFabulousRender(float p_228378_1_, long p_228378_2_, MatrixStack p_228378_4_, CallbackInfo ci) {
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
        MagickCore.EVENT_BUS.post(new RenderLevelEvent.PreRenderMagickEvent(Minecraft.getInstance().worldRenderer, matrixStack, partialTicks, matrix4f));
        MagickCore.EVENT_BUS.post(new RenderLevelEvent.RenderMagickEvent(Minecraft.getInstance().worldRenderer, matrixStack, partialTicks, matrix4f));
        MagickCore.EVENT_BUS.post(new RenderLevelEvent.PostRenderMagickEvent(Minecraft.getInstance().worldRenderer, matrixStack, partialTicks, matrix4f));

         
    }
    */
}
