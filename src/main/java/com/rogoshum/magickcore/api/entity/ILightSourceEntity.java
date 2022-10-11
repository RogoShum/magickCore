package com.rogoshum.magickcore.api.entity;

import com.rogoshum.magickcore.magick.Color;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public interface ILightSourceEntity {
    public float getSourceLight();

    public boolean alive();

    public Vector3d positionVec();

    public World world();

    public float eyeHeight();

    public Color getColor();
}
