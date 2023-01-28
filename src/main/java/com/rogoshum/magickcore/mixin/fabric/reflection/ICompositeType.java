package com.rogoshum.magickcore.mixin.fabric.reflection;

import net.minecraft.client.renderer.RenderType;

public interface ICompositeType {
    RenderType.CompositeState getState();
}
