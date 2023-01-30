package com.rogoshum.magickcore.mixin.fabric.reflection;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(RenderStateShard.TextureStateShard.class)
public class MixinTextureStateShard implements ITexture {
    @Shadow
    @Final
    private Optional<ResourceLocation> texture;

    @Override
    public Optional<ResourceLocation> getTexture() {
        return this.texture;
    }
}
