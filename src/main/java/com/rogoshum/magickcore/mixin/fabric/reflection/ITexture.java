package com.rogoshum.magickcore.mixin.fabric.reflection;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface ITexture {
    Optional<ResourceLocation> getTexture();
}
