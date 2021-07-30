package com.rogoshum.magickcore.magick.materials;

import com.rogoshum.magickcore.api.IManaLimit;
import com.rogoshum.magickcore.lib.LibMaterial;

public class NetherStarMaterial implements IManaLimit {
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
    public int getRange() {
        return 32;
    }

    @Override
    public int getMana() {
        return 5000;
    }
}
