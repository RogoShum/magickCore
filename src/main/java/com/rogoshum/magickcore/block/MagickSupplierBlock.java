package com.rogoshum.magickcore.block;

import com.rogoshum.magickcore.block.tileentity.MagickSupplierTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class MagickSupplierBlock extends BaseBlock{
    public MagickSupplierBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new MagickSupplierTileEntity();
    }
}
