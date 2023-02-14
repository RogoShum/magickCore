package com.rogoshum.magickcore.client.shader;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.MagickCore;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL20;

import java.io.Closeable;
import java.io.IOException;

public class ShaderInstance implements Effect, AutoCloseable {
    private final AbstractUniform shaderDefault = new AbstractUniform();
    private final Program vertex;
    private final Program fragment;
    private final int program;
    private final Object2IntArrayMap<String> uniformCache = new Object2IntArrayMap();
    private boolean active;

    public ShaderInstance(String vertex, String fragment) throws IOException {
        this.vertex = createLoader(Program.Type.VERTEX, vertex);
        this.fragment = createLoader(Program.Type.FRAGMENT, fragment);
        this.program = ProgramManager.createProgram();
        ProgramManager.linkShader(this);
        int i = GlStateManager.glGetProgrami(program, 35714);
        if (i == 0) {
            MagickCore.LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", this.vertex.getName(), this.fragment.getName());
            MagickCore.LOGGER.warn(GlStateManager.glGetProgramInfoLog(program, 32768));
            throw new IOException("Link Shader Error.");
        }
    }

    public static Program createLoader(Program.Type shaderType, String path) throws IOException {
        Program shaderloader = shaderType.getPrograms().get(path);
        if (shaderloader == null) {
            ResourceLocation rl = ResourceLocation.tryParse(path);
            ResourceLocation resourcelocation = new ResourceLocation(rl.getNamespace(), "shaders/program/" + rl.getPath() + shaderType.getExtension());
            Resource iresource = Minecraft.getInstance().getResourceManager().getResource(resourcelocation);

            try {
                shaderloader = EffectProgram.compileShader(shaderType, path, iresource.getInputStream(), iresource.getSourceName());
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
    public int getId() {
        return program;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public Program getVertexProgram() {
        return vertex;
    }

    @Override
    public Program getFragmentProgram() {
        return fragment;
    }

    @Override
    public void attachToProgram() {

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
