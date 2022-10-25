package com.rogoshum.magickcore.api.mana;

import net.minecraft.entity.Entity;

import java.util.function.Consumer;

public interface IManaBuff {
    public void effectEntity(Entity entity);

    public String getName();
    public int getTick();
    public int getForce();
    public boolean canAddState();

    public void setTick(int tick);
    public void setForce(int force);
}
