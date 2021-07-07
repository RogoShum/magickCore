package com.rogoshum.magickcore.api;

import com.rogoshum.magickcore.MagickCore;

public enum EnumManaLimit {
    FORCE(7),
    RANGE(16),
    TICK(1500),
    MAX_MANA(5000);

    private int value;

    private EnumManaLimit(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public int limit(float value)
    {
        return (int) Math.min(value, this.value);
    }
}
