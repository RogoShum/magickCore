package com.rogoshum.magickcore.common.api.block;

import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;

public interface ILightingBlock {
    BooleanProperty STATE = BooleanProperty.create("state_change");
    IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light_level", 0, 15);

    static BooleanProperty getState() {
        return STATE;
    }
}
