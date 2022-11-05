package com.rogoshum.magickcore.buff;

import net.minecraft.entity.Entity;

public abstract class ManaBuff implements Cloneable {
    private final String type;
    private final String element;
    private int tick;
    private float force;
    private boolean beneficial;
    public ManaBuff(String type, String element)
    {
        this.type = type;
        this.element = element;
    }

    public ManaBuff beneficial(){
        beneficial = true;
        return this;
    }

    public boolean isBeneficial(){
        return beneficial;
    }

    public abstract void effectEntity(Entity entity);

    public String getType() { return type; }

    public String getElement() { return element; }

    public int getTick() { return tick; }

    public float getForce() {
        return force;
    }

    public abstract boolean canRefreshBuff();

    public ManaBuff setTick(int tick) {
        this.tick = tick;
        return this;
    }

    public ManaBuff setForce(float force) {
        this.force = force;
        return this;
    }

    @Override
    public ManaBuff clone() throws CloneNotSupportedException {
        return (ManaBuff) super.clone();
    }
}
