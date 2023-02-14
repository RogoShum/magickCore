package com.rogoshum.magickcore.common.magick;

import net.minecraft.world.phys.Vec3;

import java.util.HashSet;

public class MagickPoint<T> {
    public static final HashSet<MagickPoint<?>> points = new HashSet<>();
    private final Vec3 pos;
    private final String name;
    private final T type;
    private int life = 40;

    public MagickPoint(T type, String name, Vec3 pos) {
        this.name = name;
        this.pos = pos;
        this.type = type;
    }

    public T getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Vec3 getPos() {
        return pos;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public void tick() {
        life--;
        if(life < 0)
            points.remove(this);
    }

    public void spawn() {
        points.add(this);
    }
}
