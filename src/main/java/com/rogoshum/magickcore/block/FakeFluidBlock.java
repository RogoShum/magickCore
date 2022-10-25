package com.rogoshum.magickcore.block;

import com.rogoshum.magickcore.api.block.ILightingBlock;
import com.rogoshum.magickcore.tool.EntityLightSourceHandler;
import net.minecraft.block.*;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class FakeFluidBlock extends FlowingFluidBlock implements ILightingBlock {

    public FakeFluidBlock(FlowingFluid fluidIn, Properties builder) {
        super(fluidIn, builder);
        this.setDefaultState(this.stateContainer.getBaseState().with(LIGHT_LEVEL, 0));
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return getLight(state);
    }

    protected int getLight(BlockState state) {
        return state.get(LIGHT_LEVEL);
    }

    public void changeLight(ServerWorld worldIn, BlockPos pos, BlockState state, int level) {
        worldIn.setBlockState(pos, withLightAndFluid(level, state.get(LEVEL)), 2);
    }

    public BlockState withLightAndFluid(int light, int fluid) {
        light = Math.min(Math.max(light, 0), 15);
        return this.getDefaultState().with(LIGHT_LEVEL, light).with(LEVEL, fluid);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(LIGHT_LEVEL);
        builder.add(STATE);
    }
}
