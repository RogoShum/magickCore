package com.rogoshum.magickcore.api.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IManaSupplierTile {
    public float supplyMana(float mana);

    public BlockPos getPos();
    public World getWorld();
    public boolean isRemoved();
    public void spawnLifeState();
    public boolean shouldSpawn(boolean powered);
}
