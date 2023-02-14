package com.rogoshum.magickcore.common.recipe;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class SpawnContext {
    public final LivingEntity living;
    public final Vec3 vec;

    private SpawnContext(LivingEntity living, Vec3 vec) {
        this.living = living;
        this.vec = vec;
    }

    public static SpawnContext create(LivingEntity living, Vec3 vec) {
         return new SpawnContext(living, vec);
    }
}
