package com.rogoshum.magickcore.api.entity;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public interface IPositionEntity {
    Vector3d positionVec();

    AxisAlignedBB boundingBox();
}
