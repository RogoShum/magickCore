package com.rogoshum.magickcore.event;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.AllEntity;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.VertexShakerHelper;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.layer.EasyLayerRender;
import com.rogoshum.magickcore.client.entity.easyrender.outline.EasyOutlineRender;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RenderOutlineEvent {
    private static final HashMap<Class, EasyRenderer> outlineRenderer = new HashMap<Class, EasyRenderer>();
    public static void putOutlineRender(Class clas, EasyRenderer renderer) { outlineRenderer.put(clas, renderer); }

    private int framebufferWidth = -1;
    private int framebufferHeight = -1;
    private final IRenderTypeBuffer.Impl outlineBuffer = IRenderTypeBuffer.getImpl(new BufferBuilder(256));
    private ShaderGroup shaders = null;

    public RenderOutlineEvent(){}

    public void onModelRegistry(ModelRegistryEvent event) {
        //RenderSystem.recordRenderCall(this::initShaderGroup);
    }

    public void initShaderGroup()
    {
        if (this.shaders != null) this.shaders.close();
        this.framebufferWidth = this.framebufferHeight = -1;

        ResourceLocation resourceLocation = new ResourceLocation(MagickCore.MOD_ID, "shaders/post/outline.json");

        try {
            Minecraft mc = Minecraft.getInstance();
            Framebuffer mainFramebuffer = mc.getFramebuffer();
            TextureManager textureManager = mc.getTextureManager();
            IResourceManager resourceManager = mc.getResourceManager();
            this.shaders = new ShaderGroup(textureManager, resourceManager, mainFramebuffer, resourceLocation);
        } catch (IOException | JsonSyntaxException e) {
            MagickCore.LOGGER.warn("Failed to load shader: {}", resourceLocation, e);
            this.shaders = null;
        }
    }

    public void onRenderWorldLast(RenderWorldLastEvent event) {
        //MagickCore.LOGGER.debug("qwq " + this.shaders);
        if (this.shaders == null) return;

        // step 1: collect furnaces
        Minecraft mc = Minecraft.getInstance();
        // step 2: resize our framebuffer
        MainWindow mainWindow = mc.getMainWindow();
        int width = mainWindow.getFramebufferWidth();
        int height = mainWindow.getFramebufferHeight();
        if (width != this.framebufferWidth || height != this.framebufferHeight) {
            this.framebufferWidth = width;
            this.framebufferHeight = height;
            this.shaders.createBindFramebuffers(width, height);
        }

        // step 3: prepare block faces
        MatrixStack matrixStackIn = event.getMatrixStack();
        //this.bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP);

        for(Entity entity : Minecraft.getInstance().world.getAllEntities())
        {
            Iterator<Class> iter = outlineRenderer.keySet().iterator();
            while (iter.hasNext()) {
                Class clas = iter.next();
                if(entity.getClass() == clas || clas == AllEntity.class) {
                    EasyRenderer renderer = outlineRenderer.get(clas);
                    renderer.preRender(entity, matrixStackIn, outlineBuffer, event.getPartialTicks());
                }
            }
        }
        //this.bufferBuilder.finishDrawing();

        // step 4: bind our framebuffer
        Framebuffer framebuffer = this.shaders.getFramebufferRaw(MagickCore.MOD_ID + ":final");
        framebuffer.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
        framebuffer.bindFramebuffer(/*set viewport*/false);

        // step 5: render block faces to our framebuffer
        RenderSystem.disableBlend();
        RenderSystem.disableTexture();
        RenderSystem.disableAlphaTest();
        RenderSystem.depthMask(/*flag*/false);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        WorldVertexBufferUploader.draw((BufferBuilder) outlineBuffer.getBuffer(RenderHelper.ORB));
        //bufferIn.finish(RenderHelper.OUTLINE);

        // step 6: apply shaders
        this.shaders.render(event.getPartialTicks());

        // step 7: bind main framebuffer
        mc.getFramebuffer().bindFramebuffer(/*set viewport*/false);

        // step 8: render our framebuffer to main framebuffer
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        framebuffer.framebufferRenderExt(width, height, /*replacement*/false);

        // step 9: clean up
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.depthMask(/*flag*/true);
    }
}
