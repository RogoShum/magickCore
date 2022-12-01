package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.common.block.FakeAirBlock;
import com.rogoshum.magickcore.common.block.FakeFluidBlock;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class GlowAirTileEntity extends TileEntity implements ITickableTileEntity {
    private BlockState state;
    private ILightSourceEntity light;
    private int awayTick;

    public GlowAirTileEntity() {
        super(ModTileEntities.GLOW_AIR_TILE_ENTITY.get());
    }

    @Override
    public void tick() {
        if(world.isRemote) return;
        if(light != null && light.alive()) {
            BlockPos lightPos = EntityLightSourceManager.entityPos(light);
            if(!lightPos.equals(pos)) {
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
            this.world.setBlockState(pos, state);
        else {
            BlockState posState = world.getBlockState(pos);
            if(posState.getBlock() instanceof FakeAirBlock) {
                this.world.setBlockState(pos, ((FakeAirBlock) posState.getBlock()).getDefault());
            } else if (posState.getBlock() instanceof FakeFluidBlock){
                this.world.setBlockState(pos, Blocks.WATER.getDefaultState().with(FlowingFluidBlock.LEVEL, world.getBlockState(pos).get(FlowingFluidBlock.LEVEL)));
            } else
                this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
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
