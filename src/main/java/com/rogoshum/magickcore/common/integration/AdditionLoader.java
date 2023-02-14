package com.rogoshum.magickcore.common.integration;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

public abstract class AdditionLoader {
    public void onLoad(IEventBus eventBus) {}

    public void setup(final FMLCommonSetupEvent event) {}

    public void inter(final InterModEnqueueEvent event) {}

    public void doClientStuff(final FMLClientSetupEvent event) {}

    public void generate(final FMLLoadCompleteEvent event) {}
}
