package com.rogoshum.magickcore.common.magick.materials;

import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.lib.LibMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class EnderDragonMaterial extends Material {
    @Override
    public String getName() {
        return LibMaterial.ENDER_DRAGON;
    }

    @Override
    public float getForce() {
        return 7f;
    }

    @Override
    public int getTick() {
        return 140;
    }

    @Override
    public float getRange() {
        return 5f;
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
