package com.rogoshum.magickcore.block.tileentity;

import com.rogoshum.magickcore.api.block.ILifeStateTile;
import com.rogoshum.magickcore.entity.LifeStateEntity;
import com.rogoshum.magickcore.init.ModTileEntities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class VoidSphereTileEntity extends TileEntity implements ILifeStateTile {
    public VoidSphereTileEntity() {
        super(ModTileEntities.void_sphere_tileentity.get());
    }

    @Override
    public void touch(LifeStateEntity entity) {}
}
