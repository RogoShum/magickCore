package com.rogoshum.magickcore.api.entity;

import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.world.level.Level;

public interface ILightSourceEntity extends IPositionEntity{
    public float getSourceLight();

    public boolean alive();

    public Level world();

    public float eyeHeight();

    public Color getColor();

    public boolean spawnGlowBlock();
}
