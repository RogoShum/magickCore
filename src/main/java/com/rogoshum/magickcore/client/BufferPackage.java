package com.rogoshum.magickcore.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;

public class BufferPackage {
    public RenderType type;
    public MatrixStack matrixStack;
    public BufferBuilder buffer;
    public String renderShader;

    private BufferPackage(MatrixStack matrixStackIn, BufferBuilder bufferIn, RenderType type) {
        this.matrixStack = matrixStackIn;
        this.type = type;
        this.buffer = bufferIn;
    }

    public static BufferPackage create(MatrixStack matrixStackIn, BufferBuilder bufferIn, RenderType type) {
        return new BufferPackage(matrixStackIn, bufferIn, type);
    }

    public BufferPackage useShader(String shader) {
        this.renderShader = shader;
        return this;
    }
}
