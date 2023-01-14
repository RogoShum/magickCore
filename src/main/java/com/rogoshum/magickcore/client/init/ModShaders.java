package com.rogoshum.magickcore.client.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.event.ShaderEvent;
import com.rogoshum.magickcore.common.lib.LibShaders;
import net.minecraft.resources.ResourceLocation;

public class ModShaders {
    public static void init() {
        ShaderEvent.addShaders(new ResourceLocation(LibShaders.OPACITY), new ResourceLocation(MagickCore.MOD_ID, "opacity_final"));
        ShaderEvent.addShaders(new ResourceLocation(LibShaders.SLIME), new ResourceLocation(MagickCore.MOD_ID, "slime_final"));
        ShaderEvent.addShaders(new ResourceLocation(LibShaders.DISTORTION), new ResourceLocation(MagickCore.MOD_ID, "distortion_final"));
        ShaderEvent.addShaders(new ResourceLocation(LibShaders.DISTORTION_MID), new ResourceLocation(MagickCore.MOD_ID, "distortion_final"));
        ShaderEvent.addShaders(new ResourceLocation(LibShaders.DISTORTION_SMALL), new ResourceLocation(MagickCore.MOD_ID, "distortion_final"));
    }
}
