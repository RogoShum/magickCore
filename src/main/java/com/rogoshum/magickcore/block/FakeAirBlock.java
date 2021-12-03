package com.rogoshum.magickcore.block;

import com.rogoshum.magickcore.api.block.ILightingBlock;
import com.rogoshum.magickcore.tool.EntityLightSourceHandler;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class FakeAirBlock extends AirBlock implements ILightingBlock {
    private final BlockState defaultState;

    public FakeAirBlock(BlockState state, Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(LIGHT_LEVEL, 0));
        defaultState = state;
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (!EntityLightSourceHandler.isPosLighting(worldIn, pos))
            worldIn.setBlockState(pos, defaultState);
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
        return this.getDefaultState().with(LIGHT_LEVEL, level);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(LIGHT_LEVEL);
        builder.add(STATE);
    }
}
