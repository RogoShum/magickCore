package com.rogoshum.magickcore.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;

public class RenderParams {
    public MatrixStack matrixStack;
    public BufferBuilder buffer;
    public float partialTicks;
    public RenderParams matrixStack(MatrixStack matrixStack) {
        this.matrixStack = matrixStack;
        return this;
    }

    public RenderParams buffer(BufferBuilder buffer) {
        this.buffer = buffer;
        return this;
    }

    public RenderParams partialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
        return this;
    }
}
