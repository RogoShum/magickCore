package com.rogoshum.magickcore.common.api.event;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.eventbus.api.Event;

public class ProfilerChangeEvent extends Event {
    private final String name;

    public ProfilerChangeEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
