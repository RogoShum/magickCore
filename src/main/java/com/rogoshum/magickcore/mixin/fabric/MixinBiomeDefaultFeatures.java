package com.rogoshum.magickcore.mixin.fabric;

import com.rogoshum.magickcore.common.event.magickevent.RegisterEvent;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeDefaultFeatures.class)
public class MixinBiomeDefaultFeatures {
    @Inject(method = "addDefaultOres", at = @At("TAIL"))
    private static void addDefaultOres(BiomeGenerationSettings.Builder builder, CallbackInfo ci) {
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, RegisterEvent.SPIRIT_ORE);
    }
}
