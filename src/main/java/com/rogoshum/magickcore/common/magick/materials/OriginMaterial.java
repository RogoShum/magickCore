package com.rogoshum.magickcore.common.magick.materials;

import com.rogoshum.magickcore.common.init.CommonConfig;
import com.rogoshum.magickcore.common.lib.LibMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class OriginMaterial extends Material{
    public String getName() {
        return LibMaterial.ORIGIN;
    }


    public float getForce() {
        double force = CommonConfig.ORIGIN_FORCE.get();
        return (float) force;
    }


    public int getTick() {
        return CommonConfig.ORIGIN_TICK.get();
    }


    public float getRange() {
        double range = CommonConfig.ORIGIN_RANGE.get();
        return (float) range;
    }


    public int getMana() {
        return 50000;
    }

    public Item getItem() {
        return Items.AIR;
    }
}
