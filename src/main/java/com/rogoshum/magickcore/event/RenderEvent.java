package com.rogoshum.magickcore.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.api.event.RenderWorldEvent;
import com.rogoshum.magickcore.block.tileentity.CanSeeTileEntity;
import com.rogoshum.magickcore.buff.ManaBuff;
import com.rogoshum.magickcore.client.VertexShakerHelper;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.layer.EasyLayerRender;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.layer.ManaFreezeRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.layer.ManaTakenRenderer;
import com.rogoshum.magickcore.client.gui.ManaBarGUI;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.tileentity.easyrender.EasyTileRenderer;
import com.rogoshum.magickcore.lib.LibEntityData;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.magick.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.tool.EntityLightSourceHandler;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElementTool;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL20;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@OnlyIn(Dist.CLIENT)
public class RenderEvent {
    private static final List<LitParticle> particles = new ArrayList<>();
    private static final HashMap<Class<? extends Entity>, EasyRenderer<? extends Entity>> easyRenderer = new HashMap<Class<? extends Entity>, EasyRenderer<? extends Entity>>();
    private static final HashMap<Class<? extends Entity>, EasyLayerRender<? extends Entity>> layerRenderer = new HashMap<Class<? extends Entity>, EasyLayerRender<? extends Entity>>();
    private static final HashMap<Class<? extends Entity>, EasyRenderer<? extends Entity>> laserRenderer = new HashMap<Class<? extends Entity>, EasyRenderer<? extends Entity>>();
    private static final HashMap<Class<? extends TileEntity>, EasyTileRenderer<? extends TileEntity>> tileRenderer = new HashMap<Class<? extends TileEntity>, EasyTileRenderer<? extends TileEntity>>();

    public static void addMagickParticle(LitParticle par) {
        particles.add(par);
        EntityLightSourceHandler.addLightSource(par);
    }
    public static <T extends Entity> void registerEasyRender(Class<T> clas, EasyRenderer<T> renderer) {
        easyRenderer.put(clas, renderer);
    }

    public static <T extends LivingEntity> void registerLayerRender(Class<T> clas, EasyLayerRender<T> renderer) {
        layerRenderer.put(clas, renderer);
    }

    public static <T extends Entity> void registerLaserRender(Class<T> clas, EasyRenderer<T> renderer) {
        laserRenderer.put(clas, renderer);
    }

    public static <T extends TileEntity> void registerTileRender(Class<T> clas, EasyTileRenderer<T> renderer) {
        tileRenderer.put(clas, renderer);
    }

    @SuppressWarnings("unchecked")
    public static <T extends TileEntity> EasyTileRenderer<T> getTileRenderer(T tile){
        if(tileRenderer.containsKey(tile.getClass()))
            return (EasyTileRenderer<T>) tileRenderer.get(tile.getClass());
        return null;
    }

    private static final HashMap<CanSeeTileEntity, Integer> tileRenderTick = new HashMap<CanSeeTileEntity, Integer>();
    private static final ManaFreezeRenderer freezeRender = new ManaFreezeRenderer();
    private static final ManaTakenRenderer takenRenderer = new ManaTakenRenderer();

    public static void activeTileEntityRender(CanSeeTileEntity entity)
    {
        tileRenderTick.put(entity, 5);
    }

    public static boolean isTileEntityActivated(TileEntity entity)
    {
        return entity instanceof CanSeeTileEntity && tileRenderTick.containsKey(entity);
    }

    public static void renderEasyRenderer(Entity entity, MatrixStack matrixStack) {
        if(easyRenderer.containsKey(entity.getClass())) {
            EasyRenderer<Entity> renderer = (EasyRenderer<Entity>) easyRenderer.get(entity.getClass());
            matrixStack.push();
            renderer.render(entity, matrixStack, Tessellator.getInstance().getBuffer(), 0);
            matrixStack.pop();
        }
    }

    public void tickTileRender(CanSeeTileEntity entity)
    {
            tileRenderTick.put(entity, tileRenderTick.get(entity) - 1);
            if(tileRenderTick.get(entity) <= 0)
                tileRenderTick.remove(entity);
    }

    @SubscribeEvent
    public void onOverlayRender(RenderGameOverlayEvent event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if (Minecraft.getInstance().player == null) {
            return;
        }

        ExtraDataHelper.entityData(Minecraft.getInstance().player).<EntityStateData>execute(LibEntityData.ENTITY_STATE, (data) -> {
            ManaBarGUI manaBarGUI = new ManaBarGUI(event.getMatrixStack(), data);
            manaBarGUI.render();
        });
    }

    @SubscribeEvent
    public void renderMagick(RenderWorldLastEvent event) {
        MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.PreRenderMagickEvent(event.getContext(), event.getMatrixStack(), event.getPartialTicks(), event.getProjectionMatrix()));
        MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.RenderMagickEvent(event.getContext(), event.getMatrixStack(), event.getPartialTicks(), event.getProjectionMatrix()));
        MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.PostRenderMagickEvent(event.getContext(), event.getMatrixStack(), event.getPartialTicks(), event.getProjectionMatrix()));
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void renderTileEntity(RenderWorldEvent.RenderMagickEvent event) {
        MatrixStack matrixStackIn = event.getMatrixStack();
        IRenderTypeBuffer.Impl bufferIn = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        for(TileEntity entity : Minecraft.getInstance().world.loadedTileEntityList) {
            if(isTileEntityActivated(entity) && tileRenderer.containsKey(entity.getClass())) {
                EasyTileRenderer<TileEntity> renderer = (EasyTileRenderer<TileEntity>) tileRenderer.get(entity.getClass());
                renderer.preRender(entity, matrixStackIn, bufferIn, event.getPartialTicks());
                tickTileRender((CanSeeTileEntity) entity);
            }
        }
    }

    @SubscribeEvent
    public void renderParticle(RenderWorldEvent.RenderMagickEvent event) {
        Vector3d vec = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        BufferBuilder bufferIn = Tessellator.getInstance().getBuffer();

        Vector3d vector3d = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double d0 = vector3d.getX();
        double d1 = vector3d.getY();
        double d2 = vector3d.getZ();
        Matrix4f matrix4f = event.getMatrixStack().getLast().getMatrix();

        ClippingHelper clippinghelper = new ClippingHelper(matrix4f, event.getProjectionMatrix());
        clippinghelper.setCameraPosition(d0, d1, d2);

        for (int i = 0; i < particles.size(); ++i) {
            LitParticle particle = particles.get(i);
            if(particle == null || particle.isDead()) continue;
            if (isInRangeToRender3d(particles.get(i), vec.x, vec.y, vec.z) && particle.shouldRender(clippinghelper))
                particle.render(event.getMatrixStack(), bufferIn);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isInRangeToRender3d(LitParticle par, double x, double y, double z) {
        double d0 = par.getPosX() - x;
        double d1 = par.getPosY() - y;
        double d2 = par.getPosZ() - z;
        double d3 = d0 * d0 + d1 * d1 + d2 * d2;
        return isInRangeToRenderDist(par, d3);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isInRangeToRenderDist(LitParticle par, double distance) {
        double d0 = par.getBoundingBox().getAverageEdgeLength();
        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }
        if(d0 < 0.7)
            d0 = 0.7;
        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }

    @SubscribeEvent
    public void cancelFreezeEntity(RenderLivingEvent.Pre event) {
        AtomicBoolean hasTaken = new AtomicBoolean(false);
        ExtraDataHelper.entityData(event.getEntity()).<EntityStateData>execute(LibEntityData.ENTITY_STATE, (data) -> {
            if(data.getBuffList().containsKey(LibBuff.FREEZE)) {
                event.setCanceled(true);
                freezeRender.render(event.getEntity(), event.getRenderer(), event.getMatrixStack(), event.getBuffers(), 0);
            }
            if(data.getBuffList().containsKey(LibBuff.TAKEN))
                hasTaken.set(true);
        });

        ExtraDataHelper.entityData(event.getEntity()).<TakenEntityData>execute(LibEntityData.TAKEN_ENTITY, (data) -> {
            if(hasTaken.get() && !data.getOwnerUUID().equals(MagickCore.emptyUUID)
                    && !data.getOwnerUUID().equals(event.getEntity().getUniqueID())) {
                event.setCanceled(true);
                takenRenderer.render(event.getEntity(), event.getRenderer(), event.getMatrixStack(), event.getBuffers(), event.getPartialRenderTick());
            }
        });
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void renderEntityLayer(RenderLivingEvent.Post event)
    {
        if(layerRenderer.containsKey(event.getEntity().getClass())) {
            easyRender(event.getEntity(), layerRenderer, event.getMatrixStack(), event.getPartialRenderTick());
        }
        EasyLayerRender<LivingEntity>  renderer = (EasyLayerRender<LivingEntity>) layerRenderer.get(LivingEntity.class);
        renderer.preRender(event.getEntity(), event.getRenderer(), event.getMatrixStack(), event.getBuffers(), event.getPartialRenderTick());
    }

    @SuppressWarnings("unchecked")
    public static <R extends EasyRenderer<? extends Entity>> void easyRender(Entity entity, HashMap<Class<? extends Entity>, R> rendererHashMap, MatrixStack matrixStackIn, float partialTicks) {
        EasyRenderer<Entity> renderer = (EasyRenderer<Entity>) rendererHashMap.get(entity.getClass());
        renderer.preRender(entity, matrixStackIn, Tessellator.getInstance().getBuffer(), partialTicks);
    }

    @SubscribeEvent
    public void renderEntity(RenderWorldEvent.RenderMagickEvent event) {
        MatrixStack matrixStackIn = event.getMatrixStack();

        for(Entity entity : Minecraft.getInstance().world.getAllEntities()) {
            if(entity instanceof IManaEntity) {
                ((IManaEntity) entity).renderFrame(event.getPartialTicks());
            }
            if(easyRenderer.containsKey(entity.getClass())) {
                easyRender(entity, easyRenderer, matrixStackIn, event.getPartialTicks());
            }

            if(laserRenderer.containsKey(entity.getClass())) {
                easyRender(entity, laserRenderer, matrixStackIn, event.getPartialTicks());
            }
        }
    }

    @SubscribeEvent
    public void onItemDescription(ItemTooltipEvent event)
    {
        if(event.getItemStack().hasTag() && event.getItemStack().getTag().contains(LibElementTool.TOOL_ELEMENT))
        {
            CompoundNBT tag = NBTTagHelper.getToolElementTable(event.getItemStack());
            if(tag.keySet().size() > 0) {
                event.getToolTip().add((new StringTextComponent("")));
                event.getToolTip().add((new TranslationTextComponent(LibElementTool.TOOL_DESCRIPTION)));
            }
            Iterator<String> keys = tag.keySet().iterator();

            while (keys.hasNext())
            {
                String element = keys.next();
                int duration = tag.getInt(element);
                event.getToolTip().add((new TranslationTextComponent(LibElementTool.TOOL_ATTRIBUTE + element)).appendString(" ").append((new TranslationTextComponent(LibElementTool.TOOL_DURATION).appendString(" " + Integer.toString(duration)))));
            }
        }
    }

    public static void tickParticle() {
        Vector3d vec = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        VertexShakerHelper.tickGroup();
        for(int i = 0; i < particles.size(); ++i) {
            LitParticle par = particles.get(i);
            if(par != null) {
                if(isInRangeToRender3d(par, vec.x, vec.y, vec.z))
                    par.tick();
                else
                    par.easyTick();
                if (par.isDead())
                    particles.remove(par);
            }
            else
                particles.remove(i);
        }
    }

    @SubscribeEvent
    public void onClientEntityUpdate(EntityEvents.EntityUpdateEvent event) {
        Entity entity = event.getEntity();
        ExtraDataHelper.entityData(entity).<EntityStateData>execute(LibEntityData.ENTITY_STATE, (data) -> {
            for (ManaBuff buff : data.getBuffList().values()) {
                if(buff.isBeneficial())
                    applybuffParticle(entity, MagickCore.proxy.getElementRender(buff.getElement()), Minecraft.getInstance().world);
                else
                    applydeBuffParticle(entity, MagickCore.proxy.getElementRender(buff.getElement()), Minecraft.getInstance().world);
            }
            if(entity instanceof AnimalEntity && data.getElement().type() != LibElements.ORIGIN) {
                applyAnimalParticle(entity, data.getElement().getRenderer(), Minecraft.getInstance().world);
            }
        });
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
        for(int i = 0; i < 2; ++i) {
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
