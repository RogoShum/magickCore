package com.rogoshum.magickcore.api.event;

import com.rogoshum.magickcore.common.magick.MagickElement;

public interface IMagickMob {
    public MagickElement getElement();
    public void setElement(MagickElement manaElement);
    public void hitMixing(IMagickMob a);
}
