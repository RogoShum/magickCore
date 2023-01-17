package com.rogoshum.magickcore.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IManaSupplierTile {
    public float supplyMana(float mana);

    public BlockPos pos();
    public Level world();
    public boolean removed();
    public void spawnLifeState();
    public boolean shouldSpawn(boolean powered);
}
