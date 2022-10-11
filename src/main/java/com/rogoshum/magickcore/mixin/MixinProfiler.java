package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.api.event.ProfilerChangeEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.profiler.Profiler;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Callable;

@Mixin(Profiler.class)
public class MixinProfiler {

}
