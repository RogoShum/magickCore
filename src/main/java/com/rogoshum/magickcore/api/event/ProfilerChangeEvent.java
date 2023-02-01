package com.rogoshum.magickcore.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ProfilerChangeEvent extends com.rogoshum.magickcore.api.event.Event implements FabricEvent{
    public static final Event<ProfilerChangeEvent.Fabric> EVENT = EventFactory.createArrayBacked(ProfilerChangeEvent.Fabric.class,
            (listeners) -> (string) -> {
                for (ProfilerChangeEvent.Fabric event : listeners) {
                    InteractionResult result = event.interact(string);

                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            }
    );
    private final String name;

    public ProfilerChangeEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public InteractionResult interact() {
        return EVENT.invoker().interact(getName());
    }

    public interface Fabric {
        public InteractionResult interact(String string);
    }
}
