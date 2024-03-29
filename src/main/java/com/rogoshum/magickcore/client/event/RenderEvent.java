package com.rogoshum.magickcore.client.event;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IEntityAdditionalSpawnData;
import com.rogoshum.magickcore.api.event.EntityEvent;
import com.rogoshum.magickcore.api.event.RenderGameOverlayEvent;
import com.rogoshum.magickcore.api.event.RenderLevelEvent;
import com.rogoshum.magickcore.api.event.living.LivingEvent;
import com.rogoshum.magickcore.api.itemstack.IManaData;
import com.rogoshum.magickcore.client.entity.easyrender.layer.WandSelectionRenderer;
import com.rogoshum.magickcore.client.gui.ElementShieldHUD;
import com.rogoshum.magickcore.client.gui.ManaBarHUD;
import com.rogoshum.magickcore.client.gui.ManaBuffHUD;
import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.client.entity.easyrender.layer.ElementShieldRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.layer.ManaItemDurationBarRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.event.SubscribeEvent;
import com.rogoshum.magickcore.common.lib.LibElementTool;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.lib.LibShaders;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class RenderEvent {
    private static final HashMap<RenderMode, Queue<LitParticle>> particles = new HashMap<>();

    public static void addMagickParticle(LitParticle par) {
        if(par.getTexture() == null) return;
        RenderMode mode = par.getRenderMode();
        if(!particles.containsKey(mode)) {
            particles.put(mode, Queues.newConcurrentLinkedQueue());
        }
        particles.get(mode).add(par);
        MagickCore.proxy.addRenderer(() -> par);

        //EntityLightSourceHandler.addLightSource(par);
    }

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent event) {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        EntityStateData state = ExtraDataUtil.entityStateData(Minecraft.getInstance().player);
        ManaBarHUD manaBarGUI = new ManaBarHUD(event.getPoseStack(), state);
        manaBarGUI.render();
        ManaBuffHUD manaBuffHUD = new ManaBuffHUD(event.getPoseStack(), state);
        manaBuffHUD.render();
        ElementShieldHUD elementShieldGUI = new ElementShieldHUD(state);
        elementShieldGUI.render();
    }

    @SubscribeEvent
    public void renderEntity(RenderLevelEvent.RenderMagickEvent event) {
        PoseStack matrixStackIn = event.getPoseStack();
        BufferBuilder builder = Tesselator.getInstance().getBuilder();

        HashMap<RenderMode, Queue<Consumer<RenderParams>>> renderer = MagickCore.proxy.getGlFunction();
        RenderParams renderParams = new RenderParams();
        renderParams.matrixStack(matrixStackIn);
        renderParams.buffer(builder);
        renderParams.partialTicks(event.getPartialTicks());
        for (RenderMode bufferMode : renderer.keySet()) {
            matrixStackIn.pushPose();
            if (bufferMode.originRender) {
                Queue<Consumer<RenderParams>> render = renderer.get(bufferMode);
                Iterator<Consumer<RenderParams>> it = render.iterator();
                while (it.hasNext()) {
                    Consumer<RenderParams> consumer = it.next();
                    matrixStackIn.pushPose();
                    consumer.accept(renderParams);
                    matrixStackIn.popPose();
                }
            } else {
                BufferContext context = BufferContext.create(matrixStackIn, builder, bufferMode.renderType);
                if (bufferMode.useShader != null && !bufferMode.useShader.isEmpty())
                    context.useShader(bufferMode.useShader);

                RenderHelper.setup(context);
                RenderHelper.begin(context);
                RenderHelper.queueMode = true;

                Queue<Consumer<RenderParams>> render = renderer.get(bufferMode);
                Iterator<Consumer<RenderParams>> it = render.iterator();
                while (it.hasNext()) {
                    Consumer<RenderParams> consumer = it.next();
                    matrixStackIn.pushPose();
                    consumer.accept(renderParams);
                    matrixStackIn.popPose();
                }

                RenderHelper.queueMode = false;
                RenderHelper.finish(context);
                RenderHelper.end(context);
            }
            matrixStackIn.popPose();
        }

        for (RenderMode bufferMode : particles.keySet()) {
            Queue<LitParticle> particleQueue = particles.get(bufferMode);
            BufferContext context = BufferContext.create(matrixStackIn, builder, bufferMode.renderType);
            if (bufferMode.useShader != null && !bufferMode.useShader.isEmpty())
                context.useShader(bufferMode.useShader);

            RenderHelper.setup(context);
            RenderHelper.begin(context);
            RenderHelper.queueMode = true;

            Iterator<LitParticle> particleIterator = particleQueue.iterator();
            while (particleIterator.hasNext()) {
                LitParticle particle = particleIterator.next();
                if(particle.render)
                    particle.render(renderParams);
            }

            RenderHelper.queueMode = false;
            RenderHelper.finish(context);
            RenderHelper.end(context);
        }

        Vec3 vec = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double d0 = vec.x();
        double d1 = vec.y();
        double d2 = vec.z();
        Matrix4f matrix4f = event.getPoseStack().last().pose();

        Frustum clippinghelper = new Frustum(matrix4f, event.getProjectionMatrix());
        clippinghelper.prepare(d0, d1, d2);
        MagickCore.proxy.setClippingHelper(clippinghelper);
        MagickCore.proxy.updateRenderer();
    }

    @SubscribeEvent
    public void addRenderer(EntityEvent.EntityAddedToLevelEvent event) {
        if(event.getEntity() instanceof LivingEntity)
            MagickCore.proxy.addRenderer(() -> new ElementShieldRenderer((LivingEntity) event.getEntity()));
        if(event.getEntity() instanceof ItemEntity && ((ItemEntity) event.getEntity()).getItem().getItem() instanceof IManaData)
            MagickCore.proxy.addRenderer(() -> new ManaItemDurationBarRenderer((ItemEntity) event.getEntity()));
        if(event.getEntity() == Minecraft.getInstance().player)
            MagickCore.proxy.addRenderer(() -> new WandSelectionRenderer(Minecraft.getInstance().player));
        if(event.getEntity() instanceof IEntityAdditionalSpawnData) {
            ((IEntityAdditionalSpawnData) event.getEntity()).onAddedToLevel();
        }
    }

    static {
        ItemTooltipCallback.EVENT.register(((stack, context, lines) -> {
            if(stack.hasTag() && stack.getTag().contains(LibElementTool.TOOL_ELEMENT)) {
                CompoundTag tag = NBTTagHelper.getToolElementTable(stack);
                if(tag.getAllKeys().size() > 0) {
                    lines.add((new TextComponent("")));
                    lines.add((new TranslatableComponent(LibElementTool.TOOL_DESCRIPTION)));
                }
                Iterator<String> keys = tag.getAllKeys().iterator();

                while (keys.hasNext()) {
                    String element = keys.next();
                    int duration = tag.getInt(element);
                    lines.add((new TranslatableComponent(LibElementTool.TOOL_ATTRIBUTE + element)).append(" ").append((new TranslatableComponent(LibElementTool.TOOL_DURATION).append(" " + Integer.toString(duration)))));
                }
            }
        }));
    }

    public static void tickParticle() {
        Vec3 vec = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        for (RenderMode res : particles.keySet()) {
            Queue<LitParticle> litParticles = particles.get(res);
            double scale = Math.max((litParticles.size() * 0.003d), 1d);
            for(LitParticle par : litParticles) {
                if(par != null) {
                    if(RenderHelper.isInRangeToRender3d(par, vec.x, vec.y, vec.z, scale))
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
        particles.values().forEach(list -> list.forEach(LitParticle::remove));
        particles.values().forEach(Collection::clear);
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

        float value = state.getElementShieldMana();
        if(value > 0) {
            float alpha = value / ((LivingEntity)entity).getMaxHealth();

            if(value > ((LivingEntity)entity).getMaxHealth())
                alpha = 1.0f;
            alpha *= 0.8f;
            LitParticle par = new LitParticle(entity.level, new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/ripple/ripple_" + Integer.toString(MagickCore.rand.nextInt(5)) + ".png")
                    , new Vec3(MagickCore.getNegativeToOne() * entity.getBbWidth() * 1.5f + entity.getX()
                    , MagickCore.getNegativeToOne() * entity.getBbHeight() * 1.2f + entity.getY() + entity.getBbHeight() * 0.5
                    , MagickCore.getNegativeToOne() * entity.getBbWidth() * 1.5f + entity.getZ())
                    , entity.getBbWidth() * 2, entity.getBbWidth() * 2, alpha, 60, state.getElement().getRenderer());
            par.setGlow();
            par.setParticleGravity(0);
            par.setShakeLimit(5f);
            par.useShader(LibShaders.OPACITY);
            par.setLimitScale();
            MagickCore.addMagickParticle(par);
        }
    }

    public static void applyBuffParticle(Entity entity, ElementRenderer render, Level world) {
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
        LitParticle par = new LitParticle(world, render.getMistTexture()
                , new Vec3(MagickCore.getNegativeToOne() * entity.getBbWidth() * 0.5f + entity.getX()
                , MagickCore.getNegativeToOne() * entity.getBbHeight() * 0.5f + entity.getY() + entity.getBbHeight() * 0.5f
                , MagickCore.getNegativeToOne() * entity.getBbWidth() * 0.5f + entity.getZ())
                , (entity.getBbWidth() + MagickCore.rand.nextFloat())/ 2, (entity.getBbWidth() + MagickCore.rand.nextFloat())/ 2, 0.5f * MagickCore.rand.nextFloat(), render.getParticleRenderTick(), render);
        par.setGlow();
        par.setParticleGravity(0f);
        par.setShakeLimit(15.0f);
        par.addMotion(MagickCore.getNegativeToOne() / 15, MagickCore.getNegativeToOne() / 15, MagickCore.getNegativeToOne() / 15);
        MagickCore.addMagickParticle(par);

        LitParticle litPar = new LitParticle(world, render.getParticleTexture()
                , new Vec3(MagickCore.getNegativeToOne() * entity.getBbWidth() * 0.5f + entity.getX()
                , MagickCore.getNegativeToOne() * entity.getBbHeight() * 0.5f + entity.getY() + entity.getBbHeight() * 0.5
                , MagickCore.getNegativeToOne() * entity.getBbWidth() * 0.5f + entity.getZ())
                , entity.getBbWidth() / 3f, entity.getBbWidth() / 3f, 0.8f * MagickCore.rand.nextFloat(), 20, render);
        litPar.setGlow();
        litPar.addMotion(MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10);
        MagickCore.addMagickParticle(litPar);
    }
}
