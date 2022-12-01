package com.rogoshum.magickcore.client.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.api.event.PreRenderChunkEvent;
import com.rogoshum.magickcore.api.event.ProfilerChangeEvent;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class LightShaderManager {
    private static ShaderInstance shader;
    boolean postedLights = false;
    private boolean isGui;

    public static void init() throws IOException {
        shader = new ShaderInstance("magickcore:terrain", "magickcore:terrain");
    }

    @SubscribeEvent
    public void preRenderChunk(PreRenderChunkEvent e) {
        //Minecraft.getInstance().gameRenderer.getLightTexture().disableLightmap();
        //RenderSystem.activeTexture(33984);
        //RenderSystem.bindTexture(5);
        //RenderSystem.enableTexture();
        if(true) return;
        if(RenderHelper.stopShader()) return;
        BlockPos pos = e.getRenderPosition();
        if(shader.isActive())
            setChunk(pos.getX(), pos.getY(), pos.getZ());
    }

    @SubscribeEvent
    public void onProfilerChange(ProfilerChangeEvent event) {
        if(true) return;
        if(RenderHelper.stopShader()) return;
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        PlayerEntity player = Minecraft.getInstance().player;

        if(event.getName().equals("terrain")) {
            shader.useShader();
            shader.setUniform("sampler", 33984 - '\u84c0');
            shader.setUniform("lightmap", 33986 - '\u84c0');

            if(!postedLights) {
                postedLights = true;
                setLightSource(Util.make(new ArrayList<>(), list -> list.addAll(EntityLightSourceManager.getLightList())));
            }
        } else if(event.getName().equals("translucent")) {
            shader.useShader();
            //shader.setUniform("sampler", 0);
            //shader.setUniform("lightmap", 0);
            shader.setUniform("sampler", 33984 - '\u84c0');
            shader.setUniform("lightmap", 33986 - '\u84c0');

            if(!postedLights) {
                postedLights = true;
                setLightSource(Util.make(new ArrayList<>(), list -> list.addAll(EntityLightSourceManager.getLightList())));
            }
        } else {
            shader.stopShader();
            return;
        }

        if(event.getName().equals("litParticles")) {
            shader.useShader();
            shader.setUniform("sampler", 0);
            shader.setUniform("lightmap", 0);
            shader.setUniform("chunkX", 0);
            shader.setUniform("chunkY", 0);
            shader.setUniform("chunkZ", 0);
        }

        if(event.getName().equals("sky"))
        {
            shader.stopShader();
        }
        if(event.getName().equals("particles"))
        {
            shader.stopShader();
        }
        if(event.getName().equals("weather"))
        {
            shader.stopShader();
        }
        if(event.getName().equals("entities"))
        {
            shader.stopShader();
        }
        if(event.getName().equals("blockEntities"))
        {
            shader.stopShader();
        }
        if(event.getName().equals("outline"))
        {
            shader.stopShader();
        }
        if(event.getName().equals("aboveClouds"))
        {
            shader.stopShader();
        }
        if(event.getName().equals("destroyProgress"))
        {
            shader.stopShader();
        }
        if(event.getName().equals("hand"))
        {
            shader.stopShader();
            //precedesEntities = true;
        }
        if(event.getName().equals("gui"))
        {
            isGui = true;
            shader.stopShader();
        }
    }

    @SubscribeEvent
    public void renderLast(RenderWorldLastEvent e) {
        if(true) return;
        if(RenderHelper.stopShader()) return;
        postedLights = false;
        GlStateManager.disableLighting();
        shader.stopShader();
    }

    public void setChunk(int x, int y, int z) {
        shader.setUniform("chunkX", x);
        shader.setUniform("chunkY", y);
        shader.setUniform("chunkZ", z);
    }

    public void setLightSource(List<ILightSourceEntity> lightSource) {
        int minCount = Math.min(lightSource.size(), 256);
        for(int i = 0; i < minCount; i++) {
            ILightSourceEntity light = lightSource.get(i);
            if(light == null) continue;
            float r = light.getColor().r() * 0.75f;
            float g = light.getColor().g() * 0.65f;
            float b = light.getColor().b();
            float alpha = (r + g + b) / 3f;
            alpha = 1f - alpha;
            alpha *= 2f;
            float scale = (Math.max(15f, light.getSourceLight()) / 15f);
            r = r*r * scale + (1-scale) * r;
            g = g*g * scale + (1-scale) * g;
            b = b*b* scale + (1-scale) * b;

            //alpha += 1 - (Math.max(15f, light.getSourceLight()) / 30f);
            int pos = GL20.glGetUniformLocation(shader.getProgram(), "lights["+i+"].position");
            GL20.glUniform3f(pos, (float)light.positionVec().x, (float)light.positionVec().y, (float)light.positionVec().z);
            int color = GL20.glGetUniformLocation(shader.getProgram(), "lights["+i+"].color");
            GL20.glUniform4f(color, r, g, b, alpha);
            int radius = GL20.glGetUniformLocation(shader.getProgram(), "lights["+i+"].radius");
            GL20.glUniform1f(radius, light.getSourceLight());
        }

        if(minCount < 256) {
            for(int i = minCount; i < 256; i++) {
                int pos = GL20.glGetUniformLocation(shader.getProgram(), "lights["+i+"].position");
                GL20.glUniform3f(pos, 0f, 0f, 0f);
                int color = GL20.glGetUniformLocation(shader.getProgram(), "lights["+i+"].color");
                GL20.glUniform4f(color, 0f, 0f, 0f, 0f);
                int radius = GL20.glGetUniformLocation(shader.getProgram(), "lights["+i+"].radius");
                GL20.glUniform1f(radius, 0f);
            }
        }
        shader.setUniform("lightCount", minCount);
        shader.setUniform("vanillaTracing", 1);
        shader.setUniform("colMix", 1);
        shader.setUniform("negative", 1f);
    }
}
