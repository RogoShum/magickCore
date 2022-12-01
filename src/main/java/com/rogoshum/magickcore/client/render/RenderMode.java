package com.rogoshum.magickcore.client.render;

import net.minecraft.client.renderer.RenderType;

import java.util.Objects;

public class RenderMode {
    public static final RenderMode ORIGIN_RENDER = new RenderMode();
    public final RenderType renderType;
    public final String useShader;
    public final boolean originRender;

    public RenderMode(RenderType renderType) {
        this.renderType = renderType;
        this.useShader = "";
        this.originRender = false;
    }

    private RenderMode() {
        this.renderType = null;
        this.useShader = "";
        this.originRender = true;
    }

    public RenderMode(RenderType renderType, String shader) {
        this.renderType = renderType;
        this.useShader = shader;
        this.originRender = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RenderMode)) return false;
        RenderMode that = (RenderMode) o;
        return originRender == that.originRender && Objects.equals(renderType, that.renderType) && useShader.equals(that.useShader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(renderType, useShader, originRender);
    }
}
