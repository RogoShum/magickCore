package com.rogoshum.magickcore.mixin.fabric.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Supplier;

public class PrivateUtil {
    public static <T extends Mob> void registerSpawnPlacements(EntityType<T> entityType, SpawnPlacements.Type type, Heightmap.Types types, SpawnPlacements.SpawnPredicate<T> spawnPredicate) {
        MixinSpawnPlacements.add(entityType, type, types, spawnPredicate);
    }

    public static void registerPotionBrewing(Potion potion, Item item, Potion potion2) {
        MixinPotionBrewing.add(potion, item, potion2);
    }

    public static PoiType registerPoiType(String string, Set<BlockState> set, int i, int j) {
        return MixinPoiType.create(string, set, i, j);
    }

    public static VillagerProfession registerVillagerProfession(String string, PoiType poiType, @Nullable SoundEvent soundEvent) {
        return MixinVillagerProfession.create(string, poiType, soundEvent);
    }

    public static <U extends Sensor<?>> SensorType<U> registerSensorType(String string, Supplier<U> supplier) {
        return MixinSensorType.create(string, supplier);
    }
}
