package com.rogoshum.magickcore.magick;

import net.minecraft.entity.Entity;

import java.util.function.Function;

public class ReleaseAttribute{
    public Entity entity, projectile, victim;
    public int tick;
    public float force;

    public ReleaseAttribute(Entity entity, Entity projectile, Entity victim, int tick, float force) {
        this.entity = entity;
        this.projectile = projectile;
        this.victim = victim;
        this.tick = tick;
        this.force = force;
    }
}
