package com.rogoshum.magickcore.api.event;

import com.rogoshum.magickcore.api.IManaElement;

public interface IMagickMob {
    public IManaElement getElement();
    public void setElement(IManaElement manaElement);
    public void hitMixing(IMagickMob a);
}
