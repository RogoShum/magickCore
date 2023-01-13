package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.api.event.ProfilerChangeEvent;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EmptyProfiler.class)
public class MixinClientProfiler {
    /*
    @Inject(method = "popPush(Ljava/lang/String;)V",
            at = @At(
                    value = "TAIL")
    )
    private void onTerrainSection(String p_219895_1_, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new ProfilerChangeEvent(p_219895_1_));
    }
     */
}
