package com.rogoshum.magickcore.api.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public interface IRedStoneEntity {
    int getPower();

    default void onRemoveRedStone(BlockPos blockPos, Level world) {
        if(blockPos != null)
            world.updateNeighborsAt(blockPos, world.getBlockState(blockPos).getBlock());
    }

    default BlockPos onTickRedStone(Vec3 vector3d, BlockPos blockPos, Level world) {
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
