package com.rogoshum.magickcore.magick.materials;

import com.rogoshum.magickcore.api.IMaterialLimit;
import com.rogoshum.magickcore.api.event.ExtraDataEvent;
import com.rogoshum.magickcore.lib.LibMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class OriginMaterial extends Material{
    public String getName() {
        return LibMaterial.ORIGIN;
    }


    public float getForce() {
        return 3;
    }


    public int getTick() {
        return 100;
    }


    public float getRange() {
        return 1.5f;
    }


    public int getMana() {
        return 5000;
    }

    public Item getItem() {
        return Items.AIR;
    }
}
