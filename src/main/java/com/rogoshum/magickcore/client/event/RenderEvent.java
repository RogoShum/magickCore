package com.rogoshum.magickcore.client.event;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.api.event.RenderWorldEvent;
import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.client.entity.easyrender.layer.ElementShieldRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.layer.ManaItemDurationBarRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.gui.ManaBarGUI;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.lib.LibElementTool;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.common.lib.LibShaders;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class RenderEvent {
    private static final HashMap<ResourceLocation, Queue<LitParticle>> particles = new HashMap<>();

    public static void addMagickParticle(LitParticle par) {
        if(par.getTexture() == null) return;
        if(!particles.containsKey(par.getTexture())) {
            particles.put(par.getTexture(), Queues.newConcurrentLinkedQueue());
        }
        particles.get(par.getTexture()).add(par);
        MagickCore.proxy.addRenderer(() -> par);

        //EntityLightSourceHandler.addLightSource(par);
    }

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if (Minecraft.getInstance().player == null) {
            return;
        }

        ExtraDataUtil.entityData(Minecraft.getInstance().player).<EntityStateData>execute(LibEntityData.ENTITY_STATE, (data) -> {
            ManaBarGUI manaBarGUI = new ManaBarGUI(event.getMatrixStack(), data);
            manaBarGUI.render();
        });
    }

    @SubscribeEvent
    public void renderEntity(RenderWorldEvent.RenderMagickEvent event) {
        MatrixStack matrixStackIn = event.getMatrixStack();
        BufferBuilder builder = Tessellator.getInstance().getBuffer();

        HashMap<RenderMode, Queue<Consumer<RenderParams>>> renderer = MagickCore.proxy.getGlFunction();
        RenderParams renderParams = new RenderParams();
        renderParams.matrixStack(matrixStackIn);
        renderParams.buffer(builder);
        renderParams.partialTicks(event.getPartialTicks());
        for (RenderMode bufferMode : renderer.keySet()) {
            matrixStackIn.push();
            if (bufferMode.originRender) {
                Queue<Consumer<RenderParams>> render = renderer.get(bufferMode);
                Iterator<Consumer<RenderParams>> it = render.iterator();
                while (it.hasNext()) {
                    Consumer<RenderParams> consumer = it.next();
                    matrixStackIn.push();
                    consumer.accept(renderParams);
                    matrixStackIn.pop();
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
                    matrixStackIn.push();
                    consumer.accept(renderParams);
                    matrixStackIn.pop();
                }

                RenderHelper.queueMode = false;
                RenderHelper.finish(context);
                RenderHelper.end(context);
            }
            matrixStackIn.pop();
        }

        Vector3d vec = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double d0 = vec.getX();
        double d1 = vec.getY();
        double d2 = vec.getZ();
        Matrix4f matrix4f = event.getMatrixStack().getLast().getMatrix();

        ClippingHelper clippinghelper = new ClippingHelper(matrix4f, event.getProjectionMatrix());
        clippinghelper.setCameraPosition(d0, d1, d2);
        MagickCore.proxy.setClippingHelper(clippinghelper);
        MagickCore.proxy.updateRenderer();
    }

    @SubscribeEvent
    public void addRenderer(EntityEvents.EntityAddedToWorldEvent event) {
        if(event.getEntity() instanceof LivingEntity)
            MagickCore.proxy.addRenderer(() -> new ElementShieldRenderer((LivingEntity) event.getEntity()));
        if(event.getEntity() instanceof ItemEntity)
            MagickCore.proxy.addRenderer(() -> new ManaItemDurationBarRenderer((ItemEntity) event.getEntity()));
    }

    @SubscribeEvent
    public void onItemDescription(ItemTooltipEvent event) {
        if(event.getItemStack().hasTag() && event.getItemStack().getTag().contains(LibElementTool.TOOL_ELEMENT)) {
            CompoundNBT tag = NBTTagHelper.getToolElementTable(event.getItemStack());
            if(tag.keySet().size() > 0) {
                event.getToolTip().add((new StringTextComponent("")));
                event.getToolTip().add((new TranslationTextComponent(LibElementTool.TOOL_DESCRIPTION)));
            }
            Iterator<String> keys = tag.keySet().iterator();

            while (keys.hasNext()) {
                String element = keys.next();
                int duration = tag.getInt(element);
                event.getToolTip().add((new TranslationTextComponent(LibElementTool.TOOL_ATTRIBUTE + element)).appendString(" ").append((new TranslationTextComponent(LibElementTool.TOOL_DURATION).appendString(" " + Integer.toString(duration)))));
            }
        }
    }

    public static void tickParticle() {
        Vector3d vec = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        for (ResourceLocation res : particles.keySet()) {
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
        if(!entity.world.isRemote) return;
        EntityStateData state = ExtraDataUtil.entityStateData(entity);
        if(state == null) return;
        for (ManaBuff buff : state.getBuffList().values()) {
            if(buff.isBeneficial())
                applybuffParticle(entity, MagickCore.proxy.getElementRender(buff.getElement()), Minecraft.getInstance().world);
            else
                applydeBuffParticle(entity, MagickCore.proxy.getElementRender(buff.getElement()), Minecraft.getInstance().world);
        }
        if(!(entity instanceof PlayerEntity) && !Objects.equals(state.getElement().type(), LibElements.ORIGIN)) {
            applyAnimalParticle(entity, state.getElement().getRenderer(), Minecraft.getInstance().world);
        }

        float value = state.getElementShieldMana();
        if(value > 0) {
            float alpha = value / ((LivingEntity)entity).getMaxHealth();

            if(value > ((LivingEntity)entity).getMaxHealth())
                alpha = 1.0f;
            alpha *= 0.8f;
            LitParticle par = new LitParticle(entity.world, new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/ripple/ripple_" + Integer.toString(MagickCore.rand.nextInt(5)) + ".png")
                    , new Vector3d(MagickCore.getNegativeToOne() * entity.getWidth() * 1.5f + entity.getPosX()
                    , MagickCore.getNegativeToOne() * entity.getHeight() * 1.2f + entity.getPosY() + entity.getHeight() / 2
                    , MagickCore.getNegativeToOne() * entity.getWidth() * 1.5f + entity.getPosZ())
                    , entity.getWidth() * 2, entity.getWidth() * 2, alpha, 60, state.getElement().getRenderer());
            par.setGlow();
            par.setParticleGravity(0);
            par.setShakeLimit(5f);
            par.useShader(LibShaders.OPACITY);
            par.setLimitScale();
            MagickCore.addMagickParticle(par);
        }
    }

    public static void applybuffParticle(Entity entity, ElementRenderer render, World world) {
        for(int i = 0; i < 3; ++i) {
            LitParticle litPar = new LitParticle(world, render.getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * entity.getWidth() * 2 + entity.getPosX()
                    , MagickCore.getNegativeToOne() * entity.getHeight() * 2 + entity.getPosY() + entity.getHeight() / 2
                    , MagickCore.getNegativeToOne() * entity.getWidth()  * 2 + entity.getPosZ())
                    , entity.getWidth() / 3.5f * MagickCore.rand.nextFloat(), entity.getWidth() / 3.5f * MagickCore.rand.nextFloat(), 0.7f + (MagickCore.rand.nextFloat() * 0.3f), 40, render);
            litPar.setGlow();
            litPar.setParticleGravity(0f);
            litPar.addMotion(MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10);
            litPar.setTraceTarget(entity);
            MagickCore.addMagickParticle(litPar);
        }
    }

    public static void applyAnimalParticle(Entity entity, ElementRenderer render, World world) {
        for(int i = 0; i < 4; ++i) {
            LitParticle litPar = new LitParticle(world, render.getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * entity.getWidth() / 2f + entity.getPosX()
                    , MagickCore.getNegativeToOne() * entity.getHeight() / 2f + entity.getPosY() + entity.getHeight() / 2
                    , MagickCore.getNegativeToOne() * entity.getWidth() / 2f + entity.getPosZ())
                    , entity.getWidth() / 8f * MagickCore.rand.nextFloat(), entity.getWidth() / 8f * MagickCore.rand.nextFloat(), 0.8f * MagickCore.rand.nextFloat(), 20, render);
            litPar.setGlow();
            litPar.addMotion(MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10);
            litPar.setTraceTarget(entity);
            MagickCore.addMagickParticle(litPar);
        }
    }

    public static void applydeBuffParticle(Entity entity, ElementRenderer render, World world) {
        LitParticle par = new LitParticle(world, render.getMistTexture()
                , new Vector3d(MagickCore.getNegativeToOne() * entity.getWidth() / 2f + entity.getPosX()
                , MagickCore.getNegativeToOne() * entity.getHeight() / 2f + entity.getPosY() + entity.getHeight() / 2f
                , MagickCore.getNegativeToOne() * entity.getWidth() / 2f + entity.getPosZ())
                , (entity.getWidth() + MagickCore.rand.nextFloat())/ 2, (entity.getWidth() + MagickCore.rand.nextFloat())/ 2, 0.5f * MagickCore.rand.nextFloat(), render.getParticleRenderTick(), render);
        par.setGlow();
        par.setParticleGravity(0f);
        par.setShakeLimit(15.0f);
        par.addMotion(MagickCore.getNegativeToOne() / 15, MagickCore.getNegativeToOne() / 15, MagickCore.getNegativeToOne() / 15);
        MagickCore.addMagickParticle(par);

        for(int i = 0; i < 2; ++i) {
            LitParticle litPar = new LitParticle(world, render.getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * entity.getWidth() / 2f + entity.getPosX()
                    , MagickCore.getNegativeToOne() * entity.getHeight() / 2f + entity.getPosY() + entity.getHeight() / 2
                    , MagickCore.getNegativeToOne() * entity.getWidth() / 2f + entity.getPosZ())
                    , entity.getWidth() / 3f, entity.getWidth() / 3f, 0.8f * MagickCore.rand.nextFloat(), 20, render);
            litPar.setGlow();
            litPar.addMotion(MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10);
            MagickCore.addMagickParticle(litPar);
        }
    }
}
