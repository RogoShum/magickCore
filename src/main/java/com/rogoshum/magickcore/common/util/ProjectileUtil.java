package com.rogoshum.magickcore.common.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class ProjectileUtil {
    public static HitResult canTouchVisibleBlock(Entity p_234618_0_, Predicate<Entity> p_234618_1_) {
        Vec3 vector3d = p_234618_0_.getDeltaMovement();
        Level world = p_234618_0_.level;
        Vec3 vector3d1 = p_234618_0_.position();
        Vec3 vector3d2 = vector3d1.add(vector3d);
        return world.clip(new ClipContext(vector3d1, vector3d2, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, p_234618_0_));
    }
}
