package com.rogoshum.magickcore.api.entity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public interface IRedStoneEntity {
    int getPower();

    default void onRemoveRedStone(BlockPos blockPos, World world) {
        if(blockPos != null)
            world.updateNeighborsAt(blockPos, world.getBlockState(blockPos).getBlock());
    }

    default BlockPos onTickRedStone(Vector3d vector3d, BlockPos blockPos, World world) {
        BlockPos pos = new BlockPos(vector3d);
        if(!pos.equals(blockPos)) {
            BlockPos prePos = blockPos;
            blockPos = pos;
            world.updateNeighborsAt(pos, world.getBlockState(pos).getBlock());
            if(prePos != null)
                world.updateNeighborsAt(prePos, world.getBlockState(prePos).getBlock());
        }
        return blockPos;
    }
}
