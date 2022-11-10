package com.rogoshum.magickcore.common.block;

import com.rogoshum.magickcore.common.api.block.ILightingBlock;
import com.rogoshum.magickcore.common.tileentity.GlowAirTileEntity;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class FakeAirBlock extends AirBlock implements ILightingBlock {
    private final BlockState defaultState;

    public FakeAirBlock(BlockState state, Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(LIGHT_LEVEL, 0));
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
        return state.get(LIGHT_LEVEL);
    }

    public void changeLight(ServerWorld worldIn, BlockPos pos, int level) {
        worldIn.setBlockState(pos, withLight(level), 2);
    }

    public BlockState withLight(int level) {
        level = Math.min(Math.max(level, 0), 15);
        return this.getDefaultState().with(LIGHT_LEVEL, level);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(LIGHT_LEVEL);
        builder.add(STATE);
    }
}
