package com.rogoshum.magickcore.client.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.event.ShaderEvent;
import com.rogoshum.magickcore.common.lib.LibShaders;
import net.minecraft.resources.ResourceLocation;

public class ModShaders {
    public static void init() {
        ShaderEvent.addShaders(new ResourceLocation(LibShaders.OPACITY), MagickCore.fromId("opacity_final"));
        ShaderEvent.addShaders(new ResourceLocation(LibShaders.SLIME), MagickCore.fromId("slime_final"));
        ShaderEvent.addShaders(new ResourceLocation(LibShaders.DISTORTION), MagickCore.fromId("distortion_final"));
        ShaderEvent.addShaders(new ResourceLocation(LibShaders.DISTORTION_MID), MagickCore.fromId("distortion_final"));
        ShaderEvent.addShaders(new ResourceLocation(LibShaders.DISTORTION_SMALL), MagickCore.fromId("distortion_final"));
    }
}
