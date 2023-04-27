package com.rogoshum.magickcore.client.init;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ClientConfig {
    public static ForgeConfigSpec CLIENT_CONFIG;
    public static ForgeConfigSpec.BooleanValue POST_PROCESSING_EFFECTS;
    public static ForgeConfigSpec.BooleanValue COLOR_LIGHTING_EFFECTS;

    static {
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        CLIENT_BUILDER.comment("Post-processing settings");
        POST_PROCESSING_EFFECTS = CLIENT_BUILDER.define("enabled", true);

        CLIENT_BUILDER.comment("Color lighting settings");
        COLOR_LIGHTING_EFFECTS = CLIENT_BUILDER.define("enabled", true);
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
}
