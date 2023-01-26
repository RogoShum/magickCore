package com.rogoshum.magickcore.mixin.fabric.registry;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IRedStoneEntity;
import com.rogoshum.magickcore.api.event.EntityEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(SpawnPlacements.class)
public abstract class MixinSpawnPlacements {
    @Shadow
    private static <T extends Mob> void register(EntityType<T> entityType, SpawnPlacements.Type type, Heightmap.Types types, SpawnPlacements.SpawnPredicate<T> spawnPredicate) {
    }

    protected static <T extends Mob> void add(EntityType<T> entityType, SpawnPlacements.Type type, Heightmap.Types types, SpawnPlacements.SpawnPredicate<T> spawnPredicate) {
        register(entityType, type, types, spawnPredicate);
    }
}
