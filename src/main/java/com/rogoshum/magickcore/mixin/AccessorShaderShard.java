package com.rogoshum.magickcore.mixin;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;
import java.util.function.Supplier;

@Mixin(RenderStateShard.ShaderStateShard.class)
public interface AccessorShaderShard {
    @Accessor("shader")
    Optional<Supplier<ShaderInstance>> getShader();
}
