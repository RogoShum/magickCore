package com.rogoshum.magickcore.api.render.easyrender;

import com.rogoshum.magickcore.common.lib.LibShaders;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.VertexFormat;

import java.util.Arrays;
import java.util.Objects;

public class RenderMode {
    public static final RenderMode ORIGIN_RENDER = new RenderMode();
    public final RenderType renderType;
    public final ShaderList useShader;
    public final boolean originRender;
    public final int hashCode;

    public RenderMode(RenderType renderType) {
        this.renderType = renderType;
        this.useShader = ShaderList.create();
        this.originRender = false;
        hashCode = Objects.hash(renderType.toString().hashCode(), useShader.hashCode(), false);
    }

    private RenderMode() {
        this.renderType = null;
        this.useShader = ShaderList.create();
        this.originRender = true;
        hashCode = Objects.hash(0, useShader.hashCode(), true);
    }

    public RenderMode(RenderType renderType, ShaderList shader) {
        this.renderType = renderType;
        this.useShader = shader;
        this.originRender = false;
        hashCode = Objects.hash(renderType.toString().hashCode(), useShader.hashCode(), false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RenderMode)) return false;
        RenderMode that = (RenderMode) o;
        return originRender == that.originRender && renderType.toString().equals(that.renderType.toString()) && useShader.equals(that.useShader);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public static class DrawMode {
        public final int drawMode;
        public final VertexFormat vertexFormat;

        public DrawMode(int drawMode, VertexFormat vertexFormat) {
            this.drawMode = drawMode;
            this.vertexFormat = vertexFormat;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DrawMode)) return false;
            DrawMode drawMode1 = (DrawMode) o;
            return drawMode == drawMode1.drawMode && vertexFormat.equals(drawMode1.vertexFormat);
        }

        @Override
        public int hashCode() {
            return Objects.hash(drawMode, vertexFormat);
        }
    }

    public static class ShaderList {
        public static final ShaderList EMPTY = RenderMode.ShaderList.create();
        public static final ShaderList BITS_SHADER = RenderMode.ShaderList.create().addShader(LibShaders.BITS);
        public static final ShaderList BITS_SMALL_SHADER = RenderMode.ShaderList.create().addShader(LibShaders.BITS_SMALL);
        public static final ShaderList BLOOM_SHADER = RenderMode.ShaderList.create().addShader(LibShaders.BLOOM);
        public static final ShaderList MELT_SHADER = RenderMode.ShaderList.create().addShader(LibShaders.MELT);
        public static final ShaderList EDGE_SHADER = RenderMode.ShaderList.create().addShader(LibShaders.EDGE);
        public static final ShaderList COLOR_SHADER = RenderMode.ShaderList.create().addShader(LibShaders.COLOR);
        public static final ShaderList BLIT_SHADER = RenderMode.ShaderList.create().addShader(LibShaders.BLIT);
        public static final ShaderList SLIME_SHADER = RenderMode.ShaderList.create().addShader(LibShaders.SLIME);
        public static final ShaderList SLIME_SMALL_SHADER = RenderMode.ShaderList.create().addShader(LibShaders.SLIME_SMALL);
        public static final ShaderList OPACITY_SHADER = RenderMode.ShaderList.create().addShader(LibShaders.OPACITY);
        public static final ShaderList DISTORTION_SHADER = RenderMode.ShaderList.create().addShader(LibShaders.DISTORTION);
        public static final ShaderList DISTORTION_MID_SHADER = RenderMode.ShaderList.create().addShader(LibShaders.DISTORTION_MID);
        public static final ShaderList DISTORTION_SMALL_SHADER = RenderMode.ShaderList.create().addShader(LibShaders.DISTORTION_SMALL);

        public String[] shaders = new String[0];

        public static ShaderList create() {
            return new ShaderList();
        }
        public ShaderList addShader(String s) {
            String[] shaders = new String[this.shaders.length+1];
            shaders[this.shaders.length] = s;
            this.shaders = shaders;
            return this;
        }

        public boolean isEmpty() {
            return shaders.length < 1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ShaderList)) return false;
            ShaderList that = (ShaderList) o;
            return Arrays.equals(shaders, that.shaders);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(shaders);
        }
    }
}
