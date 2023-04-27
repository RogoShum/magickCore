package com.rogoshum.magickcore.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(BufferBuilder.class)
public interface AccessorBufferBuilder {
    @Accessor("mode")
    VertexFormat.Mode getMode();

    @Accessor("drawStates")
    List<BufferBuilder.DrawState> getDrawStates();
}
