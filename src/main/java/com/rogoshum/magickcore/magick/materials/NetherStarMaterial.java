package com.rogoshum.magickcore.magick.materials;

import com.rogoshum.magickcore.api.IMaterialLimit;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.lib.LibMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class NetherStarMaterial extends Material {
    @Override
    public String getName() {
        return LibMaterial.NETHER_STAR;
    }

    @Override
    public float getForce() {
        return 3.5f;
    }

    @Override
    public int getTick() {
        return 150;
    }

    @Override
    public float getRange() {
        return 6f;
    }

    @Override
    public int getMana() {
        return 5000;
    }

    @Override
    public Item getItem() {
        return ModItems.nether_star_material.orElse(Items.AIR);
    }
}
