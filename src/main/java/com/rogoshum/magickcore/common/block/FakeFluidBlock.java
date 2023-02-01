package com.rogoshum.magickcore.common.block;

import com.rogoshum.magickcore.api.block.ILightingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;

public class FakeFluidBlock extends LiquidBlock implements ILightingBlock {

    public FakeFluidBlock(FlowingFluid fluidIn, BlockBehaviour.Properties builder) {
        super(fluidIn, builder);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIGHT_LEVEL, 0));
    }

    @Override
    public int getLightBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return getLight(blockState);
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
