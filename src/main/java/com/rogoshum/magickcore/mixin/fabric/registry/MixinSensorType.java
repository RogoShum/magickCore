package com.rogoshum.magickcore.mixin.fabric.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Supplier;

@Mixin(SensorType.class)
public interface MixinSensorType {
    @Invoker("register")
    static <U extends Sensor<?>> SensorType<U> create(String string, Supplier<U> supplier) {
        throw new AssertionError("Untransformed Accessor!");
    }
}
