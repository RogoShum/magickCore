package com.rogoshum.magickcore.client.init;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ClientConfig {
    public static ForgeConfigSpec CLIENT_CONFIG;
    public static ForgeConfigSpec.BooleanValue POST_PROCESSING_EFFECTS;

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON_BUILDER.comment("Post-processing settings");
        POST_PROCESSING_EFFECTS = COMMON_BUILDER.define("enabled", true);

        CLIENT_CONFIG = COMMON_BUILDER.build();
    }
}
