package com.rogoshum.magickcore.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;

public class BufferContext {
    public RenderType type;
    public MatrixStack matrixStack;
    public BufferBuilder buffer;
    public String renderShader;

    private BufferContext(MatrixStack matrixStackIn, BufferBuilder bufferIn, RenderType type) {
        this.matrixStack = matrixStackIn;
        this.type = type;
        this.buffer = bufferIn;
    }

    public static BufferContext create(MatrixStack matrixStackIn, BufferBuilder bufferIn, RenderType type) {
        return new BufferContext(matrixStackIn, bufferIn, type);
    }

    public BufferContext useShader(String shader) {
        this.renderShader = shader;
        return this;
    }
}
