package com.rogoshum.magickcore.client.init;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ClientConfig {
    public static ForgeConfigSpec CLIENT_CONFIG;
    public static ForgeConfigSpec.BooleanValue POST_PROCESSING_EFFECTS;
    public static ForgeConfigSpec.BooleanValue COLOR_LIGHTING_EFFECTS;
    public static ForgeConfigSpec.BooleanValue INSTANCED_RENDERING;

    static {
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        CLIENT_BUILDER.push("Post-processing settings");
        POST_PROCESSING_EFFECTS = CLIENT_BUILDER.define("enabled", true);
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.push("Color lighting settings");
        COLOR_LIGHTING_EFFECTS = CLIENT_BUILDER.define("enabled", true);
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.push("Instanced rendering settings");
        INSTANCED_RENDERING = CLIENT_BUILDER.define("enabled", true);
        CLIENT_BUILDER.pop();

        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
}
