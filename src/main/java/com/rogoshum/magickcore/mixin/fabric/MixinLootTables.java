package com.rogoshum.magickcore.mixin.fabric;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.rogoshum.magickcore.api.IIDLootTable;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(LootTables.class)
public class MixinLootTables {
    @Shadow
    private Map<ResourceLocation, LootTable> tables;

    @Inject(method = "apply", at = @At("RETURN"))
    private void apply(Map<ResourceLocation, JsonObject> objectMap, ResourceManager manager, ProfilerFiller profiler, CallbackInfo info) {
        tables.forEach((id, supplier) -> {
            ((IIDLootTable)supplier).setID(id);
        });
    }
}
