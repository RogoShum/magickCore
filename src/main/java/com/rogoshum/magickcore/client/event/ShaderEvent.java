package com.rogoshum.magickcore.client.event;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.RenderWorldEvent;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.client.init.ModShaders;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureManager;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
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
    private final static HashMap<String, PostChain> shaders = new HashMap<String, PostChain>();
    private final static HashMap<String, String> shadersResource = new HashMap<String, String>();
    private final static HashSet<String> renderList = new HashSet<String>();

    public void onModelRegistry(ModelRegistryEvent event) {
        ModShaders.init();
        RenderSystem.recordRenderCall(this::initShaderGroup);
    }

    public static void pushRender(String shader) {
        renderList.add(shader);
    }

    public static PostChain getShaders(ResourceLocation res) {
        if(shaders.containsKey(res.toString()))
            return shaders.get(res.toString());
        return null;
    }

    public static PostChain getShaders(String json) {
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
                RenderTarget mainFramebuffer = mc.getMainRenderTarget();
                TextureManager textureManager = mc.getTextureManager();
                ResourceManager resourceManager = mc.getResourceManager();
                shaders.put(resourceLocation.toString(), new PostChain(textureManager, resourceManager, mainFramebuffer, resourceLocation));
            } catch (IOException | JsonSyntaxException e) {
                MagickCore.LOGGER.warn("Failed to load shader: {}", resourceLocation, e);
            }
        });
        try {
            RenderHelper.setRendertypeEntityFogShader(new ShaderInstance(Minecraft.getInstance().getResourceManager(), MagickCore.fromId("rendertype_entity_translucent"), DefaultVertexFormat.POSITION_COLOR));
            RenderHelper.setRendertypeEntityTranslucentShader(new ShaderInstance(Minecraft.getInstance().getResourceManager(), MagickCore.fromId("rendertype_entity_translucent_dist"), DefaultVertexFormat.NEW_ENTITY));
            RenderHelper.setPositionTextureShader(new ShaderInstance(Minecraft.getInstance().getResourceManager(), MagickCore.fromId("position_tex"), DefaultVertexFormat.POSITION_TEX));
            RenderHelper.setRendertypeEntityTranslucentNoiseShader(new ShaderInstance(Minecraft.getInstance().getResourceManager(), MagickCore.fromId("rendertype_entity_translucent_noise"), DefaultVertexFormat.NEW_ENTITY));
            RenderHelper.setPositionColorTexLightmapShader(new ShaderInstance(Minecraft.getInstance().getResourceManager(), MagickCore.fromId("position_color_tex_lightmap"), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP));
            RenderHelper.setPositionColorTexLightmapDistShader(new ShaderInstance(Minecraft.getInstance().getResourceManager(), MagickCore.fromId("position_color_tex_lightmap_dist"), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP));
            RenderHelper.setRendertypeEntityQuadrantShader(new ShaderInstance(Minecraft.getInstance().getResourceManager(), MagickCore.fromId("rendertype_entity_quadrant"), DefaultVertexFormat.NEW_ENTITY));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onSetupShaders(RenderWorldEvent.PreRenderMagickEvent event) {
        RenderHelper.setProjectionMatrix4f(event.getProjectionMatrix());
        PoseStack myPoseStack = new PoseStack();
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        myPoseStack.mulPoseMatrix(event.getMatrixStack().last().pose());
        myPoseStack.translate((float) -camera.x, (float) -camera.y, (float) -camera.z);
        RenderHelper.setViewMatrix(myPoseStack);
        RenderHelper.setModelMatrix(new PoseStack());
        if(Minecraft.useShaderTransparency()) {
            PoseStack poseStack = RenderSystem.getModelViewStack();
            poseStack.popPose();
            RenderSystem.applyModelViewMatrix();
        }
        RenderHelper.checkRenderingShader();
        if(RenderHelper.stopShader()) return;
        Minecraft mc = Minecraft.getInstance();

        Window mainWindow = mc.getWindow();
        int width = mainWindow.getWidth();
        int height = mainWindow.getHeight();
        if (width != this.framebufferWidth || height != this.framebufferHeight) {
            this.framebufferWidth = width;
            this.framebufferHeight = height;
            shaders.values().forEach(shaderGroup -> shaderGroup.resize(width, height));
        }

        shaders.forEach( (shader, shaderGroup) -> {
            RenderTarget framebuffer = shaderGroup.getTempTarget(Objects.requireNonNull(getShaderFrameName(shader)));
            framebuffer.clear(Minecraft.ON_OSX);
            framebuffer.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
        });
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        RenderSystem.setShaderTexture(4, Minecraft.getInstance().getMainRenderTarget().getDepthTextureId());
        RenderSystem.setShaderTexture(5, Minecraft.getInstance().getMainRenderTarget().getColorTextureId());
    }

    @SubscribeEvent
    public void onRenderShaders(RenderWorldEvent.PostRenderMagickEvent event) {
        if(RenderHelper.stopShader()) return;
        List<RenderTarget> renderFrame = new ArrayList<>();
        renderList.forEach( shader -> {
            PostChain shaderGroup = shaders.get(shader);
            RenderTarget framebuffer = shaderGroup.getTempTarget(Objects.requireNonNull(getShaderFrameName(shader)));
            framebuffer.bindWrite(false);
            shaderGroup.process(event.getPartialTicks());
            renderFrame.add(framebuffer);
        });
        renderList.clear();

        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        Window mainWindow = Minecraft.getInstance().getWindow();
        PoseStack poseStack = RenderSystem.getModelViewStack();
        renderFrame.forEach( framebuffer -> {
            poseStack.pushPose();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            framebuffer.blitToScreen(mainWindow.getWidth(), mainWindow.getHeight(), false);
            RenderSystem.disableBlend();
            poseStack.popPose();
        });
        if(Minecraft.useShaderTransparency()) {
            poseStack.pushPose();
            poseStack.mulPoseMatrix(event.getMatrixStack().last().pose());
            RenderSystem.applyModelViewMatrix();
        }
    }
}
