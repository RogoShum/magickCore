package com.rogoshum.magickcore.api;

import com.rogoshum.magickcore.capability.IManaItemData;

public interface IManaMaterial {
    public int getManaNeed();

    public boolean upgradeManaItem(IManaItemData data);
}
