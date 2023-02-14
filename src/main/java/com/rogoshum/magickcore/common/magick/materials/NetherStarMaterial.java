package com.rogoshum.magickcore.common.magick.materials;

import com.rogoshum.magickcore.common.init.ModConfig;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.lib.LibMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class NetherStarMaterial extends Material {
    @Override
    public String getName() {
        return LibMaterial.NETHER_STAR;
    }

    public float getForce() {
        double force = ModConfig.NETHER_FORCE.get();
        return (float) force;
    }


    public int getTick() {
        return ModConfig.NETHER_TICK.get();
    }


    public float getRange() {
        double range = ModConfig.NETHER_RANGE.get();
        return (float) range;
    }

    @Override
    public int getMana() {
        return 5000;
    }

    @Override
    public Item getItem() {
        return ModItems.NETHER_STAR_MATERIAL.orElse(Items.AIR);
    }
}
