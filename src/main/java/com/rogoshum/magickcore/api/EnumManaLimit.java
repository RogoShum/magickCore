package com.rogoshum.magickcore.api;

import com.rogoshum.magickcore.MagickCore;

public enum EnumManaLimit {
    FORCE(5),
    RANGE(16),
    TICK(200),
    MAX_MANA(5000);

    private int value;

    private EnumManaLimit(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public float limit(float value)
    {
        return Math.min(value, this.value);
    }
}
