package com.rogoshum.magickcore.api.render;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

public interface AccessorTexture {

    Optional<ResourceLocation> getTexture();

    void setTexture(ResourceLocation res);

    Optional<ResourceLocation> getRealTexture();
}
