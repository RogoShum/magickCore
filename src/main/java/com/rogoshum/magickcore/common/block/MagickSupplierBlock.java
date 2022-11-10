package com.rogoshum.magickcore.common.block;

import com.google.common.collect.Lists;
import com.rogoshum.magickcore.common.api.block.IManaSupplierTile;
import com.rogoshum.magickcore.common.tileentity.MagickSupplierTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

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

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> list = Lists.newArrayList();
        //list.add(new ItemStack(ModItems.magick_supplier.get()));
        return list;
    }
}
