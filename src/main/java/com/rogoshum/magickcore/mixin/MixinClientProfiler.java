package com.rogoshum.magickcore.mixin;

import net.minecraft.util.profiling.InactiveProfiler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InactiveProfiler.class)
public class MixinClientProfiler {
    /*
    @Inject(method = "popPush(Ljava/lang/String;)V",
            at = @At(
                    value = "TAIL")
    )
    private void onTerrainSection(String p_219895_1_, CallbackInfo ci) {
        MagickCore.EVENT_BUS.post(new ProfilerChangeEvent(p_219895_1_));
    }
     */
}
