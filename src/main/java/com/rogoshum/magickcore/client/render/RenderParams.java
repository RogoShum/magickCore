package com.rogoshum.magickcore.client.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;

public class RenderParams {
    public PoseStack matrixStack;
    public BufferBuilder buffer;
    public float partialTicks;
    public RenderParams matrixStack(PoseStack matrixStack) {
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
