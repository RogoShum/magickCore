package com.rogoshum.magickcore.mixin.fabric.reflection;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderType.CompositeState.class)
public class MixinCompositeState implements ITextureState {
    @Shadow
    @Final
    private RenderStateShard.TextureStateShard textureState;

    @Override
    public RenderStateShard.TextureStateShard getTextureState() {
        return this.textureState;
    }
}
