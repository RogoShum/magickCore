package com.rogoshum.magickcore.magick;

import net.minecraft.util.math.vector.Vector3d;

import java.util.HashMap;
import java.util.HashSet;

public class MagickPoint<T> {
    public static final HashSet<MagickPoint<?>> points = new HashSet<>();
    private final Vector3d pos;
    private final String name;
    private final T type;
    private int life = 40;

    public MagickPoint(T type, String name, Vector3d pos) {
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

    public Vector3d getPos() {
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
