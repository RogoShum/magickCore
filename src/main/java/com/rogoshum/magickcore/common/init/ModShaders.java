package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.event.ShaderEvent;
import com.rogoshum.magickcore.common.lib.LibShaders;
import net.minecraft.util.ResourceLocation;

public class ModShaders {
    public static void init() {
        ShaderEvent.addShaders(new ResourceLocation(LibShaders.opacity), new ResourceLocation(MagickCore.MOD_ID, "opacity_final"));
        ShaderEvent.addShaders(new ResourceLocation(LibShaders.slime), new ResourceLocation(MagickCore.MOD_ID, "slime_final"));
        //ShaderEvent.addShaders(new ResourceLocation(LibShaders.light), new ResourceLocation(MagickCore.MOD_ID, "light_final"));
    }
}
