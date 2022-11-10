package com.rogoshum.magickcore.common.api.block;

import com.rogoshum.magickcore.common.entity.projectile.LifeStateEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ILifeStateTile {
    public void touch(LifeStateEntity entity);

    public BlockPos pos();
    public World world();
    public boolean removed();
}
