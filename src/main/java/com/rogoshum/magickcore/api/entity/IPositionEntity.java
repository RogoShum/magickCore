package com.rogoshum.magickcore.api.entity;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface IPositionEntity {
    Vec3 positionVec();

    AABB boundingBox();
}
