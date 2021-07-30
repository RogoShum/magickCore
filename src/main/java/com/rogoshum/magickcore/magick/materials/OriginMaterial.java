package com.rogoshum.magickcore.magick.materials;

import com.rogoshum.magickcore.api.IManaLimit;
import com.rogoshum.magickcore.api.IManaMaterial;
import com.rogoshum.magickcore.capability.IManaItemData;
import com.rogoshum.magickcore.lib.LibMaterial;

public class OriginMaterial implements IManaLimit {
    @Override
    public String getName() {
        return LibMaterial.ORIGIN;
    }

    @Override
    public float getForce() {
        return 3;
    }

    @Override
    public int getTick() {
        return 100;
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
