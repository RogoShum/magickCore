package com.rogoshum.magickcore.mixin;

import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.renderer.RenderType$CompositeRenderType")
public interface AccessorRenderType {
    @Accessor("state")
    RenderType.CompositeState getState();
}
