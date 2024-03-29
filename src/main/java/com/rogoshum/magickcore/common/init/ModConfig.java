package com.rogoshum.magickcore.common.init;

import com.google.common.util.concurrent.AtomicDouble;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ModConfig {
    public static AtomicDouble ORIGIN_FORCE = new AtomicDouble(5);
    public static AtomicDouble ORIGIN_RANGE = new AtomicDouble(3);
    public static AtomicInteger ORIGIN_TICK = new AtomicInteger(120);

    public static AtomicDouble ENDER_FORCE = new AtomicDouble(7);
    public static AtomicDouble ENDER_RANGE = new AtomicDouble(5);
    public static AtomicInteger ENDER_TICK = new AtomicInteger(140);

    public static AtomicDouble NETHER_FORCE = new AtomicDouble(6);
    public static AtomicDouble NETHER_RANGE = new AtomicDouble(7);
    public static AtomicInteger NETHER_TICK = new AtomicInteger(300);

    public static AtomicReference<List<? extends String>> ELEMENT_BAN = new AtomicReference<>(new ArrayList<>());
    public static AtomicReference<List<? extends String>> FORM_BAN = new AtomicReference<>(new ArrayList<>());
    /*
    public static ForgeConfigSpec COMMON_CONFIG;
    public static AtomicDouble ORIGIN_FORCE;
    public static AtomicDouble ORIGIN_RANGE;
    public static AtomicInteger ORIGIN_TICK;

    public static AtomicDouble ENDER_FORCE;
    public static AtomicDouble ENDER_RANGE;
    public static AtomicInteger ENDER_TICK;

    public static AtomicDouble NETHER_FORCE;
    public static AtomicDouble NETHER_RANGE;
    public static AtomicInteger NETHER_TICK;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ELEMENT_BAN;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> FORM_BAN;

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

        COMMON_BUILDER.comment("Mana Material settings").push("Origin Material");
        ORIGIN_FORCE = COMMON_BUILDER.comment("Origin material force limit").defineInRange("force", 5, 0, 15d);
        ORIGIN_RANGE = COMMON_BUILDER.comment("Origin material range limit").defineInRange("range", 3, 0, 10d);
        ORIGIN_TICK = COMMON_BUILDER.comment("Origin material tick limit").defineInRange("tick", 120, 0, 1200);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.push("Ender Dragon Material");
        ENDER_FORCE = COMMON_BUILDER.comment("Ender Dragon material force limit").defineInRange("force", 7, 0, 15d);
        ENDER_RANGE = COMMON_BUILDER.comment("Ender Dragon material range limit").defineInRange("range", 5, 0, 10d);
        ENDER_TICK = COMMON_BUILDER.comment("Ender Dragon material tick limit").defineInRange("tick", 140, 0, 1200);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.push("Nether Star Material");
        NETHER_FORCE = COMMON_BUILDER.comment("Nether Star material force limit").defineInRange("force", 6, 0, 15d);
        NETHER_RANGE = COMMON_BUILDER.comment("Nether Star material range limit").defineInRange("range", 7, 0, 10d);
        NETHER_TICK = COMMON_BUILDER.comment("Nether Star material tick limit").defineInRange("tick", 300, 0, 1200);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Element function Ban list").push("Element function");
        ELEMENT_BAN = COMMON_BUILDER.comment("Element's ApplyType that disabled, example: \n" +
                "[\"solar_attack\", \"block_void_agglomerate\"]").defineList("list", new ArrayList<>(), (o -> o instanceof String));
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Spell Form Ban list").push("Spell form");
        FORM_BAN = COMMON_BUILDER.comment("Spell form that disabled, example: \n" +
                "[\"magickcore:charge\", \"magickcore:multi_release\"]").defineList("list", new ArrayList<>(), (o -> o instanceof String));
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }
     */
}


