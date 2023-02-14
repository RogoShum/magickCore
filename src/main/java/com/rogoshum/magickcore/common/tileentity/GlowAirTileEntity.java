package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.common.block.FakeAirBlock;
import com.rogoshum.magickcore.common.block.FakeFluidBlock;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;

public class GlowAirTileEntity extends BlockEntity {
    private BlockState state;
    private ILightSourceEntity light;
    private int awayTick;

    public GlowAirTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.GLOW_AIR_TILE_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos p_155254_, BlockState p_155255_, GlowAirTileEntity me) {
        if(level.isClientSide) return;
        if(me.light != null && me.light.alive()) {
            BlockPos lightPos = EntityLightSourceManager.entityPos(me.light);
            if(!lightPos.equals(me.worldPosition)) {
                me.awayTick++;
                //EntityLightSourceHandler.tryAddLightSource(light, lightPos);
                //if(awayTick > 20)
                me.convertToDefault();
            }
        } else
            me.convertToDefault();
        //MagickCore.LOGGER.info(world.getBlockState(pos));
    }

    private void convertToDefault() {
        if(state != null)
            this.level.setBlockAndUpdate(worldPosition, state);
        else {
            BlockState posState = level.getBlockState(worldPosition);
            if(posState.getBlock() instanceof FakeAirBlock) {
                this.level.setBlockAndUpdate(worldPosition, ((FakeAirBlock) posState.getBlock()).getDefault());
            } else if (posState.getBlock() instanceof FakeFluidBlock){
                this.level.setBlockAndUpdate(worldPosition, Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, level.getBlockState(worldPosition).getValue(LiquidBlock.LEVEL)));
            } else
                this.level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
        }
    }

    public void setLight(ILightSourceEntity light) {
        this.light = light;
    }

    public void setState(BlockState state) {
        this.state = state;
    }

    public BlockState getState() {
        return state;
    }

    public ILightSourceEntity getLight() {
        return light;
    }
}
