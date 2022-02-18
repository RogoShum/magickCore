package com.rogoshum.magickcore.client.shader;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.MagickCore;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.*;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL20;

import java.io.Closeable;
import java.io.IOException;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;

public class ShaderInstance implements IShaderManager, AutoCloseable {
    private final ShaderDefault shaderDefault = new ShaderDefault();
    private final ShaderLoader vertex;
    private final ShaderLoader fragment;
    private final int program;
    private final Object2IntArrayMap<String> uniformCache = new Object2IntArrayMap();
    private boolean active;

    public ShaderInstance(String vertex, String fragment) throws IOException {
        this.vertex = createLoader(ShaderLoader.ShaderType.VERTEX, vertex);
        this.fragment = createLoader(ShaderLoader.ShaderType.FRAGMENT, fragment);
        this.program = ShaderLinkHelper.createProgram();
        ShaderLinkHelper.linkProgram(this);
        int i = GlStateManager.getProgram(program, 35714);
        if (i == 0) {
            MagickCore.LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", this.vertex.getShaderFilename(), this.fragment.getShaderFilename());
            MagickCore.LOGGER.warn(GlStateManager.getProgramInfoLog(program, 32768));
            throw new IOException("Link Shader Error.");
        }
    }

    public static ShaderLoader createLoader(ShaderLoader.ShaderType shaderType, String path) throws IOException {
        ShaderLoader shaderloader = shaderType.getLoadedShaders().get(path);
        if (shaderloader == null) {
            ResourceLocation rl = ResourceLocation.tryCreate(path);
            ResourceLocation resourcelocation = new ResourceLocation(rl.getNamespace(), "shaders/program/" + rl.getPath() + shaderType.getShaderExtension());
            IResource iresource = Minecraft.getInstance().getResourceManager().getResource(resourcelocation);

            try {
                shaderloader = ShaderLoader.func_216534_a(shaderType, path, iresource.getInputStream(), iresource.getPackName());
            } finally {
                IOUtils.closeQuietly((Closeable)iresource);
            }
        }

        return shaderloader;
    }

    @Override
    public void close() throws Exception {

    }

    public void useShader() {
        GL20.glUseProgram(this.program);
        active = true;
    }

    public void stopShader() {
        GL20.glUseProgram(0);
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public int getProgram() {
        return program;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public ShaderLoader getVertexShaderLoader() {
        return vertex;
    }

    @Override
    public ShaderLoader getFragmentShaderLoader() {
        return fragment;
    }

    public int getUniformLocation(String location) {
        if (this.program == 0)
            return program;
        if (!this.uniformCache.containsKey(location)) {
            int loc = GL20.glGetUniformLocation(this.program, location);
            if (loc == -1) {
                try {
                    throw new Exception("shader中不存在名为" + location + "的uniform!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            this.uniformCache.put(location, loc);
        }
        return this.uniformCache.getInt(location);
    }

    public void setUniform(String uniform, int value) {
        GL20.glUniform1i(getUniformLocation(uniform), value);
    }

    public void setUniform(String uniform, boolean value) {
        GL20.glUniform1i(getUniformLocation(uniform), value ? 1 : 0);
    }

    public void setUniform(String uniform, float value) {
        GL20.glUniform1f(getUniformLocation(uniform), value);
    }

    public void setUniform(String uniform, float[] value) {
        GL20.glUniform1fv(getUniformLocation(uniform), value);
    }

    public void setUniform(String uniform, int v1, int v2) {
        GL20.glUniform2i(getUniformLocation(uniform), v1, v2);
    }

    public void setUniform(String uniform, int v1, int v2, int v3) {
        GL20.glUniform3i(getUniformLocation(uniform), v1, v2, v3);
    }

    public void setUniform(String uniform, float v1, float v2) {
        GL20.glUniform2f(getUniformLocation(uniform), v1, v2);
    }

    public void setUniform(String uniform, float v1, float v2, float v3) {
        GL20.glUniform3f(getUniformLocation(uniform), v1, v2, v3);
    }

    public void setUniform(String uniform, float v1, float v2, float v3, float v4) {
        GL20.glUniform4f(getUniformLocation(uniform), v1, v2, v3, v4);
    }
}
