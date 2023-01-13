package com.rogoshum.magickcore.common.block;

import com.rogoshum.magickcore.api.block.ILightingBlock;
import net.minecraft.block.*;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import net.minecraft.block.AbstractBlock.Properties;

public class FakeFluidBlock extends FlowingFluidBlock implements ILightingBlock {

    public FakeFluidBlock(FlowingFluid fluidIn, Properties builder) {
        super(fluidIn, builder);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIGHT_LEVEL, 0));
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return getLight(state);
    }

    protected int getLight(BlockState state) {
        return state.getValue(LIGHT_LEVEL);
    }

    public void changeLight(ServerWorld worldIn, BlockPos pos, BlockState state, int level) {
        worldIn.setBlock(pos, withLightAndFluid(level, state.getValue(LEVEL)), 2);
    }

    public BlockState withLightAndFluid(int light, int fluid) {
        light = Math.min(Math.max(light, 0), 15);
        return this.defaultBlockState().setValue(LIGHT_LEVEL, light).setValue(LEVEL, fluid);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIGHT_LEVEL);
        builder.add(STATE);
    }
}
