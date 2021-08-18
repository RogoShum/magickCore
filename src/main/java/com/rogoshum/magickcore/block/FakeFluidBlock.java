package com.rogoshum.magickcore.block;

import com.rogoshum.magickcore.tool.EntityLightSourceHandler;
import net.minecraft.block.*;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class FakeFluidBlock extends FlowingFluidBlock {
    public static final IntegerProperty LEVEL = IntegerProperty.create("light_level", 0, 15);

    public FakeFluidBlock(FlowingFluid fluidIn, Properties builder) {
        super(fluidIn, builder);
        this.setDefaultState(this.stateContainer.getBaseState().with(LEVEL, 0));
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (!EntityLightSourceHandler.isPosLighting(pos))
            worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
    }


    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return getLight(state);
    }

    protected int getLight(BlockState state) {
        return state.get(LEVEL);
    }

    public void changeLight(ServerWorld worldIn, BlockPos pos, int level) {
        worldIn.setBlockState(pos, withLight(level), 2);
    }

    public BlockState withLight(int level) {
        return this.getDefaultState().with(LEVEL, level);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(LEVEL);
    }
}
