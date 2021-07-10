package com.rogoshum.magickcore.buff;

import com.rogoshum.magickcore.api.IManaBuff;
import net.minecraft.entity.Entity;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public abstract class ManaBuff implements Cloneable {
    private final String type;
    private int tick;
    private float force;

    public ManaBuff(String type)
    {
        this.type = type;
    }

    public abstract void effectEntity(Entity entity);

    public String getType() { return type; }

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
