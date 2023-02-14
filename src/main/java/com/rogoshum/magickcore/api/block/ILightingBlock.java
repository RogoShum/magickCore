package com.rogoshum.magickcore.api.block;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public interface ILightingBlock {
    BooleanProperty STATE = BooleanProperty.create("state_change");
    IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light_level", 0, 15);

    static BooleanProperty getState() {
        return STATE;
    }
}
