package com.rogoshum.magickcore.magick.materials;

import com.rogoshum.magickcore.api.IManaLimit;
import com.rogoshum.magickcore.lib.LibMaterial;

public class ThunderMaterial implements IManaLimit {
    @Override
    public String getName() {
        return LibMaterial.THUNDER;
    }

    @Override
    public float getForce() {
        return 4.5f;
    }

    @Override
    public int getTick() {
        return 150;
    }

    @Override
    public int getRange() {
        return 16;
    }

    @Override
    public int getMana() {
        return 7500;
    }
}
