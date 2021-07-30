package com.rogoshum.magickcore.block.tileentity;

public interface IPanelTileEntity {
    public IPanelTileEntity getInputFirst();
    public IPanelTileEntity getInputSecond();

    public IPanelTileEntity getOutputFirst();
    public IPanelTileEntity getOutputSecond();

    public boolean isClosed();
}
