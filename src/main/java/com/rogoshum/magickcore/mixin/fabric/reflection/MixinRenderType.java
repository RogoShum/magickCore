package com.rogoshum.magickcore.mixin.fabric.reflection;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(targets = "net.minecraft.client.renderer.RenderType$CompositeRenderType")
public class MixinRenderType implements ICompositeType{

    @Shadow @Final private RenderType.CompositeState state;

    @Override
    public RenderType.CompositeState getState() {
        return this.state;
    }

    @Mixin(targets = "net.minecraft.client.renderer.RenderType$CompositeState")
    public static class MixinCompositeState implements ITextureState{
        @Shadow @Final private RenderStateShard.TextureStateShard textureState;

        @Override
        public RenderStateShard.TextureStateShard getTextureState() {
            return this.textureState;
        }
    }

    @Mixin(targets = "net.minecraft.client.renderer.RenderStateShard$TextureStateShard")
    public static class MixinTextureStateShard implements ITexture{
        @Shadow @Final private Optional<ResourceLocation> texture;

        @Override
        public Optional<ResourceLocation> getTexture() {
            return this.texture;
        }
    }
}
