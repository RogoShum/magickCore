package com.rogoshum.magickcore.client.event;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.api.event.RenderWorldEvent;
import com.rogoshum.magickcore.api.extradata.item.ItemDimensionData;
import com.rogoshum.magickcore.api.item.IDimensionTooltip;
import com.rogoshum.magickcore.client.entity.easyrender.layer.WandSelectionRenderer;
import com.rogoshum.magickcore.client.gui.ElementShieldHUD;
import com.rogoshum.magickcore.client.gui.ManaBarHUD;
import com.rogoshum.magickcore.client.gui.ManaBuffHUD;
import com.rogoshum.magickcore.client.render.instanced.ModelInstanceRenderer;
import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.client.entity.easyrender.layer.ElementShieldRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.layer.ManaItemDurationBarRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.item.ElementContainerItem;
import com.rogoshum.magickcore.common.lib.LibElementTool;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import com.mojang.math.Matrix4f;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL20;

@OnlyIn(Dist.CLIENT)
public class RenderEvent {
    private static final ConcurrentHashMap<RenderMode, Queue<LitParticle>> PARTICLES = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<RenderMode, Queue<LitParticle>> SHADER_PARTICLES = new ConcurrentHashMap<>();
    private static RenderTarget LIGHT_TARGET;
    static {
        RenderSystem.recordRenderCall(() -> {
            LIGHT_TARGET = new TextureTarget(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight(), false, Minecraft.ON_OSX);
            LIGHT_TARGET.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        });
    }

    public static void addMagickParticle(LitParticle par) {
        if(par.getTexture() == null) return;

        RenderMode mode = par.getRenderMode();
        if(!mode.useShader.isEmpty()) {
            if(!SHADER_PARTICLES.containsKey(mode)) {
                SHADER_PARTICLES.put(mode, Queues.newConcurrentLinkedQueue());
            }
            SHADER_PARTICLES.get(mode).add(par);
        } else {
            if(!PARTICLES.containsKey(mode)) {
                PARTICLES.put(mode, Queues.newConcurrentLinkedQueue());
            }
            PARTICLES.get(mode).add(par);
        }
        MagickCore.proxy.addRenderer(() -> par);

        //EntityLightSourceHandler.addLightSource(par);
    }

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent.PostLayer event) {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        EntityStateData state = ExtraDataUtil.entityStateData(Minecraft.getInstance().player);
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            ManaBarHUD manaBarGUI = new ManaBarHUD(event.getMatrixStack(), state);
            manaBarGUI.render();
        } else if(event.getOverlay() == ForgeIngameGui.HELMET_ELEMENT) {
            ElementShieldHUD elementShieldGUI = new ElementShieldHUD(state);
            elementShieldGUI.render();
            ManaBuffHUD manaBuffHUD = new ManaBuffHUD(event.getMatrixStack(), state);
            manaBuffHUD.render();
        }
    }

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent.Post event) {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        EntityStateData state = ExtraDataUtil.entityStateData(Minecraft.getInstance().player);
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            ManaBarHUD manaBarGUI = new ManaBarHUD(event.getMatrixStack(), state);
            manaBarGUI.render();
        }
    }

    @SubscribeEvent
    public void renderEntity(RenderWorldEvent.RenderMagickEvent event) {
        PoseStack matrixStackIn = event.getMatrixStack();
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        RenderSystem.setShaderLights(Vector3f.YP, Vector3f.YN);
        Queue<Consumer<RenderParams>> originalRenderer = MagickCore.proxy.getOriginalGlFunction();
        HashMap<RenderMode, Queue<Consumer<RenderParams>>> solidRenderer = MagickCore.proxy.getSolidGlFunction();
        HashMap<RenderMode, Queue<Consumer<RenderParams>>> renderer = MagickCore.proxy.getGlFunction();
        HashMap<RenderMode, Queue<Consumer<RenderParams>>> shaderRenderer = MagickCore.proxy.getShaderGlFunction();
        Queue<Vector4f> lights = MagickCore.proxy.getColorLightFunction();

        RenderParams renderParams = new RenderParams();
        renderParams.matrixStack(matrixStackIn);
        renderParams.buffer(builder);
        renderParams.partialTicks(event.getPartialTicks());

        if(lights.size() > 0)
            renderLights(lights, RenderHelper.getRendertypeEntityLightShader());

        renderParticle(matrixStackIn, builder, renderParams, PARTICLES);
        renderParticle(matrixStackIn, builder, renderParams, SHADER_PARTICLES);

        //renderer use minecraft render function
        matrixStackIn.pushPose();
        Iterator<Consumer<RenderParams>> it = originalRenderer.iterator();
        while (it.hasNext()) {
            Consumer<RenderParams> consumer = it.next();
            matrixStackIn.pushPose();
            consumer.accept(renderParams);
            matrixStackIn.popPose();
        }
        matrixStackIn.popPose();

        //renderer use minecraft render function
        callGLFunction(matrixStackIn, builder, renderParams, solidRenderer);
        callGLFunction(matrixStackIn, builder, renderParams, renderer);
        callGLFunction(matrixStackIn, builder, renderParams, shaderRenderer);

        Vec3 vec = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double d0 = vec.x();
        double d1 = vec.y();
        double d2 = vec.z();
        Matrix4f matrix4f = event.getMatrixStack().last().pose();

        Frustum clippinghelper = new Frustum(matrix4f, event.getProjectionMatrix());
        clippinghelper.prepare(d0, d1, d2);
        if (Minecraft.getInstance().level.effects().constantAmbientLight()) {
            Lighting.setupNetherLevel(matrixStackIn.last().pose());
        } else {
            Lighting.setupLevel(matrixStackIn.last().pose());
        }

        MagickCore.proxy.setClippingHelper(clippinghelper);
        MagickCore.proxy.updateRenderer();
    }

    public static Vector4f unpackVec4(float packed) {
        int ip = (int) packed;
        int rr = ip / 1000000;
        float gg = ip - 1000000 * (int)Math.floor(ip/1000000) / 10000.0f;
        float bb = (ip % 10000) / 100.0f;
        float aa = ip % 100;

        return new Vector4f(rr / 100.0f, gg / 100.0f, bb / 100.0f, aa);
    }


    public void renderLights(Queue<Vector4f> lights, ShaderInstance shader) {
        ProgramManager.glUseProgram(shader.getId());

        int minCount = Math.min(lights.size(), 1000);
        int i = 0;
        for(Vector4f light : lights) {
            int color = GL20.glGetUniformLocation(shader.getId(), "lights["+i+"]");
            GL20.glUniform4f(color, light.x(), light.y(), light.z(), light.w());
            i++;
        }
        int color = GL20.glGetUniformLocation(shader.getId(), "lightCount");
        GL20.glUniform1i(color, minCount);

        Window window = Minecraft.getInstance().getWindow();
        int width = window.getWidth();
        int height = window.getHeight();

        RenderSystem.setShader(()->shader);

        if(LIGHT_TARGET.width != width/3 || LIGHT_TARGET.height != height/3) {
            LIGHT_TARGET.resize(width/3, height/3, Minecraft.ON_OSX);
        }
        LIGHT_TARGET.clear(Minecraft.ON_OSX);
        LIGHT_TARGET.bindWrite(false);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.depthMask(false);

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(0.0D, (double)height, -50.0D).uv(0, 1).color(1, 1, 1, 1f).endVertex();
        bufferbuilder.vertex((double)width, (double)height, -50.0D).uv(1, 1).color(1, 1, 1, 1).endVertex();
        bufferbuilder.vertex((double)width, 0.0D, -50.0D).uv(1, 0).color(1, 1, 1, 1f).endVertex();
        bufferbuilder.vertex(0.0D, 0.0D, -50.0D).uv(0, 0).color(1, 1, 1, 1).endVertex();
        tessellator.end();

        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        LIGHT_TARGET.blitToScreen(width, height, false);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setProjectionMatrix(RenderHelper.getProjectionMatrix4f());
    }

    public void callGLFunction(PoseStack poseStack, BufferBuilder builder, RenderParams renderParams, HashMap<RenderMode, Queue<Consumer<RenderParams>>> renderer) {
        for (RenderMode bufferMode : renderer.keySet()) {
            Queue<Consumer<RenderParams>> render = renderer.get(bufferMode);
            if(render.isEmpty()) return;
            poseStack.pushPose();
            BufferContext context = BufferContext.create(poseStack, builder, bufferMode.renderType);
            if (!bufferMode.useShader.isEmpty())
                context.useShader(bufferMode.useShader);
            boolean instancingRender = RenderHelper.isRenderTypeInstanced(bufferMode.renderType) && RenderHelper.gl33();

            RenderHelper.setup(context);
            if(!instancingRender) {
                RenderHelper.begin(context);
            }

            RenderHelper.queueMode = true;
            if(bufferMode.renderType != null && !RenderHelper.isRenderTypeGlint(bufferMode.renderType))
                RenderHelper.GLOBAL_TEXTURE = RenderHelper.TEXTURE;

            Iterator<Consumer<RenderParams>> it = render.iterator();
            while (it.hasNext()) {
                Consumer<RenderParams> consumer = it.next();
                poseStack.pushPose();
                consumer.accept(renderParams);
                poseStack.popPose();
            }

            RenderHelper.queueMode = false;
            if(!instancingRender)
                RenderHelper.finish(context);
            else if (RenderHelper.isRenderTypeLightingInstanced(bufferMode.renderType)) {
                context.type.setupRenderState();
                RenderHelper.LIGHTING_INSTANCE_RENDERER.drawWithShader(RenderHelper.getRendertypeEntityLightInstanceShader());
                context.type.clearRenderState();
            } else {
                context.type.setupRenderState();
                for (ModelInstanceRenderer instance : RenderHelper.UPDATE_INSTANCE)
                    instance.drawWithShader(RenderHelper.getRendertypeEntityTranslucentInstanceShader());
                context.type.clearRenderState();
            }
            RenderHelper.UPDATE_INSTANCE.clear();
            RenderHelper.GLOBAL_TEXTURE = null;
            RenderHelper.end(context);
            poseStack.popPose();
        }
    }

    public void renderParticle(PoseStack poseStack, BufferBuilder builder, RenderParams renderParams, ConcurrentHashMap<RenderMode, Queue<LitParticle>> particles) {
        for (RenderMode bufferMode : particles.keySet()) {
            Queue<LitParticle> particleQueue = particles.get(bufferMode);
            if(particleQueue.isEmpty()) continue;
            BufferContext context = BufferContext.create(poseStack, builder, bufferMode.renderType);
            if (!bufferMode.useShader.isEmpty())
                context.useShader(bufferMode.useShader);
            boolean instancingRender = RenderHelper.gl33();

            RenderHelper.setup(context);
            if(!instancingRender && !context.buffer.building()) {
                context.buffer.begin(context.type.mode(), context.type.format());
            }

            RenderHelper.queueMode = true;

            Iterator<LitParticle> particleIterator = particleQueue.iterator();
            while (particleIterator.hasNext()) {
                LitParticle particle = particleIterator.next();
                if(particle.render)
                    particle.render(renderParams);
            }

            RenderSystem.setShaderTexture(3, RenderHelper.RED_AND_BLUE_AND_GREEN);
            float scale = 0.1f;
            Matrix4f matrix4f = Matrix4f.createScaleMatrix(scale, scale, scale);
            RenderSystem.setTextureMatrix(matrix4f);

            RenderHelper.queueMode = false;

            if(!instancingRender) {
                RenderHelper.finish(context);
            } else {
                context.type.setupRenderState();
                LitParticle.PARTICLE_INSTANCE_RENDERER.drawWithShader(RenderHelper.getPositionColorTexLightmapDistInstanceShader());
                context.type.clearRenderState();
            }

            RenderHelper.end(context);
            RenderSystem.resetTextureMatrix();
        }
    }

    @SubscribeEvent
    public void addRenderer(EntityEvents.EntityAddedToWorldEvent event) {
        if(event.getEntity() instanceof LivingEntity)
            MagickCore.proxy.addRenderer(() -> new ElementShieldRenderer((LivingEntity) event.getEntity()));
        if(event.getEntity() instanceof ItemEntity)
            MagickCore.proxy.addRenderer(() -> new ManaItemDurationBarRenderer((ItemEntity) event.getEntity()));
        if(event.getEntity() == Minecraft.getInstance().player)
            MagickCore.proxy.addRenderer(() -> new WandSelectionRenderer(Minecraft.getInstance().player));
    }

    @SubscribeEvent
    public void onItemDescription(ItemTooltipEvent event) {
        if(event.getItemStack().hasTag() && event.getItemStack().getTag().contains(LibElementTool.TOOL_ELEMENT)) {
            CompoundTag tag = NBTTagHelper.getToolElementTable(event.getItemStack());
            if(tag.getAllKeys().size() > 0) {
                event.getToolTip().add((new TextComponent("")));
                event.getToolTip().add((new TranslatableComponent(LibElementTool.TOOL_DESCRIPTION)));
            }
            Iterator<String> keys = tag.getAllKeys().iterator();

            while (keys.hasNext()) {
                String element = keys.next();
                int duration = tag.getInt(element);
                event.getToolTip().add(((MutableComponent)ElementContainerItem.withElementColor(new TranslatableComponent(LibElementTool.TOOL_ATTRIBUTE + element), element)).append(" ").append((new TranslatableComponent(LibElementTool.TOOL_DURATION).append(" " + Integer.toString(duration)))));
            }
        }

        if(event.getItemStack().hasTag() && event.getItemStack().getTag().contains(ItemDimensionData.DIMENSION_ITEM)) {
            ItemDimensionData dimensionData = ExtraDataUtil.itemDimensionData(event.getItemStack());
            List<ItemStack> slots = dimensionData.getSlots();
            if(slots.size() > 0) {
                event.getToolTip().add((new TextComponent("")));
                event.getToolTip().add((new TranslatableComponent(LibElementTool.DIMENSION_DESCRIPTION)));
            }

            for (ItemStack stack : slots) {
                if(!stack.isEmpty()) {
                    Component name = stack.getDisplayName();
                    if(name instanceof MutableComponent mutableName) {
                        if(stack.getCount() > 1) {
                            mutableName.append(new TextComponent(" x ")).append(String.valueOf(stack.getCount()));
                        }
                        if(name.getStyle().getColor() == null || name.getStyle().getColor().toString().equals("white")) {
                            if(NBTTagHelper.hasElement(stack))
                                name = ElementContainerItem.withElementColor(mutableName, stack);
                        }
                    }

                    event.getToolTip().add(name);
                    if(stack.getItem() instanceof IDimensionTooltip) {
                        List<Component> itemTooltip = ((IDimensionTooltip) stack.getItem()).dimensionToolTip(stack);
                        for (Component component : itemTooltip) {
                            event.getToolTip().add(new TextComponent("  ").append(component));
                        }
                    }
                }
            }
        }
    }

    public static void tickParticle() {
        Vec3 vec = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        for (RenderMode res : PARTICLES.keySet()) {
            Queue<LitParticle> litParticles = PARTICLES.get(res);
            double scale = Math.max((litParticles.size() * 0.003d), 1d);
            for(LitParticle par : litParticles) {
                if(par != null) {
                    if(RenderHelper.isInRangeToRender3d(par, vec.x, vec.y, vec.z, scale) && vec.distanceToSqr(par.centerVec()) < 2048)
                        par.tick();
                    else
                        par.easyTick();
                    if (par.isDead())
                        litParticles.remove(par);
                }
            }
        }

        for (RenderMode res : SHADER_PARTICLES.keySet()) {
            Queue<LitParticle> litParticles = SHADER_PARTICLES.get(res);
            double scale = Math.max((litParticles.size() * 0.003d), 1d);
            for(LitParticle par : litParticles) {
                if(par != null) {
                    if(RenderHelper.isInRangeToRender3d(par, vec.x, vec.y, vec.z, scale) && vec.distanceToSqr(par.centerVec()) < 2048)
                        par.tick();
                    else
                        par.easyTick();
                    if (par.isDead())
                        litParticles.remove(par);
                }
            }
        }
    }

    public static void clearParticle() {
        PARTICLES.values().forEach(list -> list.forEach(LitParticle::remove));
        PARTICLES.values().forEach(Collection::clear);
        PARTICLES.clear();
        SHADER_PARTICLES.values().forEach(list -> list.forEach(LitParticle::remove));
        SHADER_PARTICLES.values().forEach(Collection::clear);
        SHADER_PARTICLES.clear();
    }

    @SubscribeEvent
    public void onClientEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        Entity entity = event.getEntity();
        if(!entity.level.isClientSide) return;
        EntityStateData state = ExtraDataUtil.entityStateData(entity);
        if(state == null) return;
        for (ManaBuff buff : state.getBuffList().values()) {
            if(buff.isBeneficial())
                applyBuffParticle(entity, MagickCore.proxy.getElementRender(buff.getElement()), Minecraft.getInstance().level);
            else
                applyDeBuffParticle(entity, MagickCore.proxy.getElementRender(buff.getElement()), Minecraft.getInstance().level);
        }
        if(!(entity instanceof Player) && !Objects.equals(state.getElement().type(), LibElements.ORIGIN)) {
            applyAnimalParticle(entity, state.getElement().getRenderer(), Minecraft.getInstance().level);
        }
    }

    public static void applyBuffParticle(Entity entity, ElementRenderer render, Level world) {
        if(entity.tickCount % 5 == 0) {
            LitParticle litPar = new LitParticle(world, render.getParticleTexture()
                    , new Vec3(MagickCore.getNegativeToOne() * entity.getBbWidth() * 2 + entity.getX()
                    , MagickCore.getNegativeToOne() * entity.getBbHeight() * 2 + entity.getY() + entity.getBbHeight() * 0.5
                    , MagickCore.getNegativeToOne() * entity.getBbWidth()  * 2 + entity.getZ())
                    , entity.getBbWidth() / 3.5f * MagickCore.rand.nextFloat(), entity.getBbWidth() / 3.5f * MagickCore.rand.nextFloat(), 0.7f + (MagickCore.rand.nextFloat() * 0.3f), 20, render);
            litPar.setGlow();
            litPar.setParticleGravity(0f);
            litPar.addMotion(MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10);
            litPar.setTraceTarget(entity);
            MagickCore.addMagickParticle(litPar);
        }
    }

    public static void applyAnimalParticle(Entity entity, ElementRenderer render, Level world) {
        for(int i = 0; i < 4; ++i) {
            LitParticle litPar = new LitParticle(world, render.getParticleTexture()
                    , new Vec3(MagickCore.getNegativeToOne() * entity.getBbWidth() * 0.5f + entity.getX()
                    , MagickCore.getNegativeToOne() * entity.getBbHeight() * 0.5f + entity.getY() + entity.getBbHeight() * 0.5
                    , MagickCore.getNegativeToOne() * entity.getBbWidth() * 0.5f + entity.getZ())
                    , entity.getBbWidth() / 8f * MagickCore.rand.nextFloat(), entity.getBbWidth() / 8f * MagickCore.rand.nextFloat(), 0.8f * MagickCore.rand.nextFloat(), 20, render);
            litPar.setGlow();
            litPar.addMotion(MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10);
            litPar.setTraceTarget(entity);
            MagickCore.addMagickParticle(litPar);
        }
    }

    public static void applyDeBuffParticle(Entity entity, ElementRenderer render, Level world) {
        if(entity.tickCount % 5 == 0) {
            LitParticle par = new LitParticle(world, render.getMistTexture()
                    , new Vec3(MagickCore.getNegativeToOne() * entity.getBbWidth() * 0.5f + entity.getX()
                    , MagickCore.getNegativeToOne() * entity.getBbHeight() * 0.5f + entity.getY() + entity.getBbHeight() * 0.5f
                    , MagickCore.getNegativeToOne() * entity.getBbWidth() * 0.5f + entity.getZ())
                    , (entity.getBbWidth() + MagickCore.rand.nextFloat())/ 2, (entity.getBbWidth() + MagickCore.rand.nextFloat())/ 2, 0.5f * MagickCore.rand.nextFloat(), render.getParticleRenderTick(), render);
            par.setGlow();
            par.setParticleGravity(0f);
            par.setShakeLimit(5.0f);
            par.addMotion(MagickCore.getNegativeToOne() / 15, MagickCore.getNegativeToOne() / 15, MagickCore.getNegativeToOne() / 15);
            par.setColor(render.getPrimaryColor());
            MagickCore.addMagickParticle(par);

            LitParticle litPar = new LitParticle(world, render.getParticleTexture()
                    , new Vec3(MagickCore.getNegativeToOne() * entity.getBbWidth() * 0.5f + entity.getX()
                    , MagickCore.getNegativeToOne() * entity.getBbHeight() * 0.5f + entity.getY() + entity.getBbHeight() * 0.5
                    , MagickCore.getNegativeToOne() * entity.getBbWidth() * 0.5f + entity.getZ())
                    , entity.getBbWidth() / 3f, entity.getBbWidth() / 3f, 0.8f * MagickCore.rand.nextFloat(), 20, render);
            litPar.setGlow();
            litPar.addMotion(MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10);
            litPar.setColor(render.getPrimaryColor());
            MagickCore.addMagickParticle(litPar);
        }
    }
}
