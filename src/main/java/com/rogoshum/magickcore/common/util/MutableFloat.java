package com.rogoshum.magickcore.common.util;

import java.util.Objects;

public class MutableFloat {
    private float value;

    public MutableFloat(float value) {
        this.value = value;
    }

    public MutableFloat() {
        this.value = 0;
    }

    public float get() {
        return value;
    }

    public void set(float value) {
        this.value = value;
    }

    public void add() {
        value+=1;
    }

    public void add(float value) {
        this.value+=value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MutableFloat that)) return false;
        return Float.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

