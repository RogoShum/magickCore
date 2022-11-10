package com.rogoshum.magickcore.common.magick.materials;

import com.rogoshum.magickcore.common.lib.LibMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class OriginMaterial extends Material{
    public String getName() {
        return LibMaterial.ORIGIN;
    }


    public float getForce() {
        return 5;
    }


    public int getTick() {
        return 120;
    }


    public float getRange() {
        return 3f;
    }


    public int getMana() {
        return 5000;
    }

    public Item getItem() {
        return Items.AIR;
    }
}
