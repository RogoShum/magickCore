package com.rogoshum.magickcore.common.block;

import com.rogoshum.magickcore.api.block.ILightingBlock;
import com.rogoshum.magickcore.common.tileentity.GlowAirTileEntity;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

public class FakeAirBlock extends AirBlock implements ILightingBlock {
    private final BlockState defaultState;

    public FakeAirBlock(BlockState state, BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIGHT_LEVEL, 0));
        defaultState = state;
    }

    public BlockState getDefault() {
        return defaultState;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new GlowAirTileEntity();
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return getLight(state);
    }

    protected int getLight(BlockState state) {
        return state.getValue(LIGHT_LEVEL);
    }

    public void changeLight(ServerWorld worldIn, BlockPos pos, int level) {
        worldIn.setBlock(pos, withLight(level), 2);
    }

    public BlockState withLight(int level) {
        level = Math.min(Math.max(level, 0), 15);
        return this.defaultBlockState().setValue(LIGHT_LEVEL, level);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIGHT_LEVEL);
        builder.add(STATE);
    }
}
