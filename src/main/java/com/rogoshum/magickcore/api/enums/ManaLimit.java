package com.rogoshum.magickcore.api.enums;

public enum ManaLimit {
    FORCE(5),
    MAX_MANA(5000);

    private final int value;

    private ManaLimit(int value) {
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
