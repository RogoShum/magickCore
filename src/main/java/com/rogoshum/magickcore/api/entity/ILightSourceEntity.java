package com.rogoshum.magickcore.api.entity;

import com.rogoshum.magickcore.magick.Color;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public interface ILightSourceEntity extends IPositionEntity{
    public float getSourceLight();

    public boolean alive();

    public World world();

    public float eyeHeight();

    public Color getColor();

    public boolean spawnGlowBlock();
}
