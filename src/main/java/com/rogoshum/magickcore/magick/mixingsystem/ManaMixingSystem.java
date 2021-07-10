package com.rogoshum.magickcore.magick.mixingsystem;

import com.rogoshum.magickcore.api.IMagickElementObject;
import com.rogoshum.magickcore.api.IManaMixing;

public class ManaMixingSystem implements IManaMixing {
    @Override
    public void htiMixing(IMagickElementObject manaObject, IMagickElementObject manaObjectB)
    {
        manaObject.hitMixing(manaObjectB);
    }
}
