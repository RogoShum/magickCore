package com.rogoshum.magickcore.recipes;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

public class SpawnContext {
    public final LivingEntity living;
    public final Vector3d vec;

    private SpawnContext(LivingEntity living, Vector3d vec) {
        this.living = living;
        this.vec = vec;
    }

    public static SpawnContext create(LivingEntity living, Vector3d vec) {
         return new SpawnContext(living, vec);
    }
}
