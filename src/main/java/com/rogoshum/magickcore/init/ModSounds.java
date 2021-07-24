package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MagickCore.MOD_ID);
    public static final RegistryObject<SoundEvent> chaos_spawn = SOUNDS.register("chaos_spawn", () -> new SoundEvent(new ResourceLocation(MagickCore.MOD_ID, "chaos_spawn")));
    public static final RegistryObject<SoundEvent> chaos_ambience = SOUNDS.register("chaos_ambience", () -> new SoundEvent(new ResourceLocation(MagickCore.MOD_ID, "chaos_ambience")));
    public static final RegistryObject<SoundEvent> dawnward_spawn = SOUNDS.register("dawnward_spawn", () -> new SoundEvent(new ResourceLocation(MagickCore.MOD_ID, "dawnward_spawn")));
    public static final RegistryObject<SoundEvent> chaos_attak = SOUNDS.register("chaos_attak", () -> new SoundEvent(new ResourceLocation(MagickCore.MOD_ID, "chaos_attak")));
    public static final RegistryObject<SoundEvent> shpere_dissipate = SOUNDS.register("shpere_dissipate", () -> new SoundEvent(new ResourceLocation(MagickCore.MOD_ID, "shpere_dissipate")));
    public static final RegistryObject<SoundEvent> sphere_ambience = SOUNDS.register("sphere_ambience", () -> new SoundEvent(new ResourceLocation(MagickCore.MOD_ID, "sphere_ambience")));
    public static final RegistryObject<SoundEvent> sphere_spawn = SOUNDS.register("sphere_spawn", () -> new SoundEvent(new ResourceLocation(MagickCore.MOD_ID, "sphere_spawn")));
    public static final RegistryObject<SoundEvent> squal_ambience = SOUNDS.register("squal_ambience", () -> new SoundEvent(new ResourceLocation(MagickCore.MOD_ID, "squal_ambience")));
    public static final RegistryObject<SoundEvent> squal_spawn = SOUNDS.register("squal_spawn", () -> new SoundEvent(new ResourceLocation(MagickCore.MOD_ID, "squal_spawn")));
    public static final RegistryObject<SoundEvent> wall_ambience = SOUNDS.register("wall_ambience", () -> new SoundEvent(new ResourceLocation(MagickCore.MOD_ID, "wall_ambience")));
    public static final RegistryObject<SoundEvent> wall_dissipate = SOUNDS.register("wall_dissipate", () -> new SoundEvent(new ResourceLocation(MagickCore.MOD_ID, "wall_dissipate")));
    public static final RegistryObject<SoundEvent> wall_spawn = SOUNDS.register("wall_spawn", () -> new SoundEvent(new ResourceLocation(MagickCore.MOD_ID, "wall_spawn")));
    public static final RegistryObject<SoundEvent> wither_ambience = SOUNDS.register("wither_ambience", () -> new SoundEvent(new ResourceLocation(MagickCore.MOD_ID, "wither_ambience")));
    public static final RegistryObject<SoundEvent> wither_spawn = SOUNDS.register("wither_spawn", () -> new SoundEvent(new ResourceLocation(MagickCore.MOD_ID, "wither_spawn")));
}
