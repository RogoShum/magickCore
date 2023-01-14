package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.common.block.FakeAirBlock;
import com.rogoshum.magickcore.common.block.FakeFluidBlock;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GlowAirTileEntity extends BlockEntity implements TickableBlockEntity {
    private BlockState state;
    private ILightSourceEntity light;
    private int awayTick;

    public GlowAirTileEntity() {
        super(ModTileEntities.GLOW_AIR_TILE_ENTITY.get());
    }

    @Override
    public void tick() {
        if(level.isClientSide) return;
        if(light != null && light.alive()) {
            BlockPos lightPos = EntityLightSourceManager.entityPos(light);
            if(!lightPos.equals(worldPosition)) {
                awayTick++;
                //EntityLightSourceHandler.tryAddLightSource(light, lightPos);
                //if(awayTick > 20)
                    convertToDefault();
            }
        } else
            convertToDefault();
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
