package com.rogoshum.magickcore.api.render.easyrender;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.RenderType;

public class BufferContext {
    public RenderType type;
    public PoseStack matrixStack;
    public BufferBuilder buffer;
    public RenderMode.ShaderList renderShader = RenderMode.ShaderList.EMPTY;

    private BufferContext(PoseStack matrixStackIn, BufferBuilder bufferIn, RenderType type) {
        this.matrixStack = matrixStackIn;
        this.type = type;
        this.buffer = bufferIn;
    }

    public static BufferContext create(PoseStack matrixStackIn, BufferBuilder bufferIn, RenderType type) {
        return new BufferContext(matrixStackIn, bufferIn, type);
    }

    public BufferContext useShader(RenderMode.ShaderList shader) {
        this.renderShader = shader;
        return this;
    }
}
