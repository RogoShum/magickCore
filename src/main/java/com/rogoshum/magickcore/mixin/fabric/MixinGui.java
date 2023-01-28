package com.rogoshum.magickcore.mixin.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.RenderGameOverlayEvent;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MixinGui {

    @Inject(method = "render", at = @At("RETURN"))
    private void render(PoseStack poseStack, float f, CallbackInfo ci) {
        MagickCore.EVENT_BUS.post(new RenderGameOverlayEvent(poseStack, f));
    }
}
