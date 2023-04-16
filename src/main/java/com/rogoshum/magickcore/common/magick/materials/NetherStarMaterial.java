package com.rogoshum.magickcore.common.magick.materials;

import com.rogoshum.magickcore.common.init.CommonConfig;
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
        double force = CommonConfig.NETHER_FORCE.get();
        return (float) force;
    }


    public int getTick() {
        return CommonConfig.NETHER_TICK.get();
    }


    public float getRange() {
        double range = CommonConfig.NETHER_RANGE.get();
        return (float) range;
    }

    @Override
    public int getMana() {
        return 50000;
    }

    @Override
    public Item getItem() {
        return ModItems.NETHER_STAR_MATERIAL.orElse(Items.AIR);
    }
}
