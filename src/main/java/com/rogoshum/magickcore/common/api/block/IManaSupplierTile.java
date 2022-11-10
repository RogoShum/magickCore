package com.rogoshum.magickcore.common.api.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IManaSupplierTile {
    public float supplyMana(float mana);

    public BlockPos pos();
    public World world();
    public boolean removed();
    public void spawnLifeState();
    public boolean shouldSpawn(boolean powered);
}
