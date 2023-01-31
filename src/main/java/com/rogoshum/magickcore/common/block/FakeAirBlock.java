package com.rogoshum.magickcore.common.block;

import com.rogoshum.magickcore.api.block.ILightingBlock;
import com.rogoshum.magickcore.common.tileentity.GlowAirTileEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class FakeAirBlock extends AirBlock implements ILightingBlock, EntityBlock {
    private final BlockState defaultState;

    public FakeAirBlock(BlockState state, BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIGHT_LEVEL, 0));
        defaultState = state;
    }

    public BlockState getDefault() {
        return defaultState;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockGetter world) {
        return new GlowAirTileEntity();
    }

    @Override
    public int getLightBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return getLight(blockState);
    }

    protected int getLight(BlockState state) {
        return state.getValue(LIGHT_LEVEL);
    }

    public void changeLight(ServerLevel worldIn, BlockPos pos, int level) {
        worldIn.setBlock(pos, withLight(level), 2);
    }

    public BlockState withLight(int level) {
        level = Math.min(Math.max(level, 0), 15);
        return this.defaultBlockState().setValue(LIGHT_LEVEL, level);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIGHT_LEVEL);
        builder.add(STATE);
    }
}
