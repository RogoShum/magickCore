package com.rogoshum.magickcore.magick.materials;

import com.rogoshum.magickcore.lib.LibMaterial;

public class ThunderMaterial extends Material {
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
    public float getRange() {
        return 3;
    }

    @Override
    public int getMana() {
        return 7500;
    }
}
