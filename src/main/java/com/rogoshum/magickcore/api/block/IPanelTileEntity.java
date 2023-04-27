package com.rogoshum.magickcore.api.block;

public interface IPanelTileEntity {
    public IPanelTileEntity getOutputFirst();
    public IPanelTileEntity getOutputSecond();

    public boolean isClosed();
}
