package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.common.init.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SpiritCrystalTileEntity extends BlockEntity{

    public SpiritCrystalTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.SPIRIT_CRYSTAL_TILE_ENTITY.get(), pos, state);
    }
}
