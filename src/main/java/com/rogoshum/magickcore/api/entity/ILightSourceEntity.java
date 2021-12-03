package com.rogoshum.magickcore.api.entity;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public interface ILightSourceEntity {
    public int getSourceLight();

    public boolean alive();

    public Vector3d positionVec();

    public World world();

    public float eyeHeight();

    public float[] getColor();
}
