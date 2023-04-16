package com.rogoshum.magickcore.common.util;

public class MutableInteger {
    private int value;

    public MutableInteger(int value) {
        this.value = value;
    }

    public MutableInteger() {
        this.value = 0;
    }

    public int get() {
        return value;
    }

    public void set(int value) {
        this.value = value;
    }

    public void add() {
        value+=1;
    }

    public void add(int value) {
        this.value+=value;
    }
}

