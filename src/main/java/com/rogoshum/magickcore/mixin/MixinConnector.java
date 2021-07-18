package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.MagickCore;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector implements IMixinConnector {

    @Override
    public void connect() {
        Mixins.addConfigurations("mixins.magickcore.json");
        MagickCore.LOGGER.debug("IMixinConnector");
    }
}
