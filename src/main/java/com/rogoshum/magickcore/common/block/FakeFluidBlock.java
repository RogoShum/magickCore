package com.rogoshum.magickcore.common.block;

import com.rogoshum.magickcore.api.block.ILightingBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FakeFluidBlock extends LiquidBlock implements ILightingBlock {

    public FakeFluidBlock(FlowingFluid fluidIn, Properties builder) {
        super(fluidIn, builder);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIGHT_LEVEL, 0));
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        return getLight(state);
    }

    protected int getLight(BlockState state) {
        return state.getValue(LIGHT_LEVEL);
    }

    public void changeLight(ServerLevel worldIn, BlockPos pos, BlockState state, int level) {
        worldIn.setBlock(pos, withLightAndFluid(level, state.getValue(LEVEL)), 2);
    }

    public BlockState withLightAndFluid(int light, int fluid) {
        light = Math.min(Math.max(light, 0), 15);
        return this.defaultBlockState().setValue(LIGHT_LEVEL, light).setValue(LEVEL, fluid);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIGHT_LEVEL);
        builder.add(STATE);
    }
}
