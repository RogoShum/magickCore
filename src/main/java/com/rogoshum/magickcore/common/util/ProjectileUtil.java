package com.rogoshum.magickcore.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class ProjectileUtil {
    public static RayTraceResult canTouchVisibleBlock(Entity p_234618_0_, Predicate<Entity> p_234618_1_) {
        Vector3d vector3d = p_234618_0_.getDeltaMovement();
        World world = p_234618_0_.level;
        Vector3d vector3d1 = p_234618_0_.position();
        Vector3d vector3d2 = vector3d1.add(vector3d);
        return world.clip(new RayTraceContext(vector3d1, vector3d2, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, p_234618_0_));
    }
}
