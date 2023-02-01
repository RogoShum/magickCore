package com.rogoshum.magickcore.mixin.fabric.accessor;

import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockPlaceContext.class)
public interface MixinReplaceClicked {
    @Accessor("replaceClicked")
    void setReplaceClicked(boolean replaceClicked);
}
