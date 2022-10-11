package com.rogoshum.magickcore.block.tileentity;

import com.rogoshum.magickcore.api.block.ILifeStateTile;
import com.rogoshum.magickcore.entity.projectile.LifeStateEntity;
import com.rogoshum.magickcore.init.ModTileEntities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VoidSphereTileEntity extends TileEntity implements ILifeStateTile {
    public VoidSphereTileEntity() {
        super(ModTileEntities.void_sphere_tileentity.get());
    }

    @Override
    public void touch(LifeStateEntity entity) {}

    @Override
    public BlockPos pos() {
        return getPos();
    }

    @Override
    public World world() {
        return getWorld();
    }

    @Override
    public boolean removed() {
        return isRemoved();
    }
}
