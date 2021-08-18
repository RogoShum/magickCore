package com.rogoshum.magickcore.block.tileentity;

public interface IPanelTileEntity {
    public IPanelTileEntity getOutputFirst();
    public IPanelTileEntity getOutputSecond();

    public boolean isClosed();
}
