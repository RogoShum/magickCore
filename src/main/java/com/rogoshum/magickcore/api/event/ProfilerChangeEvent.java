package com.rogoshum.magickcore.api.event;

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
