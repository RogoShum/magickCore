package com.rogoshum.magickcore.common.tileentity;

public interface IPanelTileEntity {
    public IPanelTileEntity getOutputFirst();
    public IPanelTileEntity getOutputSecond();

    public boolean isClosed();
}
