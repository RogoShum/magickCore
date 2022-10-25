package com.rogoshum.magickcore.magick.materials;

import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.lib.LibMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class EnderDragonMaterial extends Material {
    @Override
    public String getName() {
        return LibMaterial.ENDER_DRAGON;
    }

    @Override
    public float getForce() {
        return 4f;
    }

    @Override
    public int getTick() {
        return 120;
    }

    @Override
    public float getRange() {
        return 1.5f;
    }

    @Override
    public int getMana() {
        return 5000;
    }

    @Override
    public Item getItem() {
        return ModItems.ender_dragon_material.orElse(Items.AIR);
    }
}
