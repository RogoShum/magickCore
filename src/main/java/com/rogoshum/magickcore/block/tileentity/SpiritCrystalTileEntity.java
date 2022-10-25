package com.rogoshum.magickcore.block.tileentity;

import com.rogoshum.magickcore.init.ModBlocks;
import com.rogoshum.magickcore.init.ModRecipes;
import com.rogoshum.magickcore.init.ModTileEntities;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class SpiritCrystalTileEntity extends TileEntity{

    public SpiritCrystalTileEntity() {
        super(ModTileEntities.spirit_crystal_tileentity.get());
    }
}
