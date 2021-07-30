package com.rogoshum.magickcore.block.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public abstract class CanSeeTileEntity extends TileEntity {
    public boolean cansee;
    public int ticksExisted;
    public CanSeeTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
}
