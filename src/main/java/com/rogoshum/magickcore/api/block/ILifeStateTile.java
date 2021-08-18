package com.rogoshum.magickcore.api.block;

import com.rogoshum.magickcore.entity.LifeStateEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ILifeStateTile {
    public void touch(LifeStateEntity entity);

    public BlockPos getPos();
    public World getWorld();
    public boolean isRemoved();
}
