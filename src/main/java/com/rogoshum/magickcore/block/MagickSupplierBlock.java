package com.rogoshum.magickcore.block;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.block.IManaSupplierTile;
import com.rogoshum.magickcore.block.tileentity.MagickSupplierTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

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

    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        TileEntity tile = worldIn.getTileEntity(pos);
        boolean powered = worldIn.isBlockPowered(pos);
        if (tile instanceof IManaSupplierTile && ((IManaSupplierTile) tile).shouldSpawn(powered)) {
            ((IManaSupplierTile) tile).spawnLifeState();
        }
    }

    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        TileEntity tile = worldIn.getTileEntity(pos);
        boolean powered = worldIn.isBlockPowered(pos);
        if (tile instanceof IManaSupplierTile && ((IManaSupplierTile) tile).shouldSpawn(powered)) {
            ((IManaSupplierTile) tile).spawnLifeState();
        }
    }
}
