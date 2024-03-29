package com.rogoshum.magickcore.common.magick.materials;

import com.rogoshum.magickcore.common.init.ModConfig;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.lib.LibMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class EnderDragonMaterial extends Material {
    @Override
    public String getName() {
        return LibMaterial.ENDER_DRAGON;
    }

    public float getForce() {
        double force = ModConfig.ENDER_FORCE.get();
        return (float) force;
    }


    public int getTick() {
        return ModConfig.ENDER_TICK.get();
    }


    public float getRange() {
        double range = ModConfig.ENDER_RANGE.get();
        return (float) range;
    }

    @Override
    public int getMana() {
        return 5000;
    }

    @Override
    public Item getItem() {
        return ModItems.ENDER_DRAGON_MATERIAL.orElse(Items.AIR);
    }
}
