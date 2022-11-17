package com.rogoshum.magickcore.common.block;

import com.rogoshum.magickcore.common.tileentity.ElementWoolTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class ElementWoolBlock extends BaseBlock{
    public ElementWoolBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        super.tick(state, worldIn, pos, rand);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ElementWoolTileEntity();
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return 10;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (!player.isCreative() && tileentity instanceof ElementWoolTileEntity) {
            ElementWoolTileEntity tile = (ElementWoolTileEntity)tileentity;
            tile.dropItem();
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }
}