package com.rogoshum.magickcore.magick.materials;

import com.rogoshum.magickcore.api.IManaLimit;
import com.rogoshum.magickcore.lib.LibMaterial;

public class EnderDragonMaterial implements IManaLimit {
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
    public int getRange() {
        return 8;
    }

    @Override
    public int getMana() {
        return 5000;
    }
}
