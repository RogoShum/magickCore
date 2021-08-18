package com.rogoshum.magickcore.api.entity;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public interface ILightSourceEntity {
    public int getSourceLight();

    public boolean isAlive();

    public Vector3d getPositionVec();

    public World getEntityWorld();

    public float getEyeHeight();
}
