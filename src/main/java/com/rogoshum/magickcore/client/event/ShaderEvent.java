package com.rogoshum.magickcore.client.event;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.RenderWorldEvent;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.shader.LightShaderManager;
import com.rogoshum.magickcore.client.init.ModShaders;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class ShaderEvent {
    private int framebufferWidth = -1;
    private int framebufferHeight = -1;
    private final static HashMap<String, ShaderGroup> shaders = new HashMap<String, ShaderGroup>();
    private final static HashMap<String, String> shadersResource = new HashMap<String, String>();
    private final static HashSet<String> renderList = new HashSet<String>();

    public void onModelRegistry(ModelRegistryEvent event) {
        ModShaders.init();
        RenderSystem.recordRenderCall(this::initShaderGroup);
    }

    public static void pushRender(String shader) {
        renderList.add(shader);
    }

    public static ShaderGroup getShaders(ResourceLocation res) {
        if(shaders.containsKey(res.toString()))
            return shaders.get(res.toString());
        return null;
    }

    public static ShaderGroup getShaders(String json) {
        if(shaders.containsKey(json))
            return shaders.get(json);
        return null;
    }

    public static void addShaders(ResourceLocation res, ResourceLocation shaderFrameName) {
        shadersResource.put(res.toString(), shaderFrameName.toString());
    }

    public static String getShaderFrameName(String shader) {
        if(shadersResource.containsKey(shader))
            return shadersResource.get(shader);
        return null;
    }

    public static String getShaderFrameName(ResourceLocation shader) {
        if(shadersResource.containsKey(shader.toString()))
            return shadersResource.get(shader.toString());
        return null;
    }

    public void initShaderGroup() {
        this.framebufferWidth = this.framebufferHeight = -1;
        shadersResource.forEach( (s, s2) -> {
            ResourceLocation resourceLocation = new ResourceLocation(s);
            try {
                Minecraft mc = Minecraft.getInstance();
                Framebuffer mainFramebuffer = mc.getMainRenderTarget();
                TextureManager textureManager = mc.getTextureManager();
                IResourceManager resourceManager = mc.getResourceManager();
                shaders.put(resourceLocation.toString(), new ShaderGroup(textureManager, resourceManager, mainFramebuffer, resourceLocation));
            } catch (IOException | JsonSyntaxException e) {
                MagickCore.LOGGER.warn("Failed to load shader: {}", resourceLocation, e);
            }
        });

        try {
            LightShaderManager.init();
        } catch (IOException | JsonSyntaxException e) {
            MagickCore.LOGGER.warn("Failed to load shader: {}", "LightShaderManager", e);
        }
    }

    @SubscribeEvent
    public void onSetupShaders(RenderWorldEvent.PreRenderMagickEvent event) {
        if(Minecraft.useShaderTransparency()) {
            RenderSystem.popMatrix();
        }
        if(RenderHelper.stopShader()) return;
        Minecraft mc = Minecraft.getInstance();

        MainWindow mainWindow = mc.getWindow();
        int width = mainWindow.getWidth();
        int height = mainWindow.getHeight();
        if (width != this.framebufferWidth || height != this.framebufferHeight) {
            this.framebufferWidth = width;
            this.framebufferHeight = height;
            shaders.values().forEach(shaderGroup -> shaderGroup.resize(width, height));
        }

        shaders.forEach( (shader, shaderGroup) -> {
            Framebuffer framebuffer = shaderGroup.getTempTarget(Objects.requireNonNull(getShaderFrameName(shader)));
            framebuffer.clear(Minecraft.ON_OSX);
        });
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }

    @SubscribeEvent
    public void onRenderShaders(RenderWorldEvent.PostRenderMagickEvent event) {
        if(RenderHelper.stopShader()) return;
        List<Framebuffer> renderFrame = new ArrayList<>();
        renderList.forEach( shader -> {
            ShaderGroup shaderGroup = shaders.get(shader);
            Framebuffer framebuffer = shaderGroup.getTempTarget(Objects.requireNonNull(getShaderFrameName(shader)));
            framebuffer.bindWrite(false);
            shaderGroup.process(event.getPartialTicks());
            renderFrame.add(framebuffer);
        });
        renderList.clear();

        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        MainWindow mainWindow = Minecraft.getInstance().getWindow();

        renderFrame.forEach( framebuffer -> {
            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            framebuffer.blitToScreen(mainWindow.getWidth(), mainWindow.getHeight(), false);
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
        });
        if(Minecraft.useShaderTransparency()) {
            RenderSystem.pushMatrix();
            RenderSystem.multMatrix(event.getMatrixStack().last().pose());
        }
    }
}
