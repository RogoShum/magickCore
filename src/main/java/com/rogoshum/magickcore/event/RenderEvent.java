package com.rogoshum.magickcore.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.AllEntity;
import com.rogoshum.magickcore.api.event.EntityEvents;
import com.rogoshum.magickcore.block.tileentity.CanSeeTileEntity;
import com.rogoshum.magickcore.capability.IElementAnimalState;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.client.VertexShakerHelper;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.layer.EasyLayerRender;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.layer.ManaFreezeRenderer;
import com.rogoshum.magickcore.client.gui.ManaBarGUI;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.tileentity.easyrender.EasyTileRenderer;
import com.rogoshum.magickcore.helper.NBTTagHelper;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElementTool;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.lib.LibItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class RenderEvent {
    private static final List<LitParticle> particles = new ArrayList<LitParticle>();
    private static final HashMap<Class, EasyRenderer> easyRenderer = new HashMap<Class, EasyRenderer>();
    private static final HashMap<Class, EasyLayerRender> layerRenderer = new HashMap<Class, EasyLayerRender>();
    private static final HashMap<Class, EasyRenderer> laserRenderer = new HashMap<Class, EasyRenderer>();
    private static final HashMap<Class, EasyTileRenderer> tileRenderer = new HashMap<Class, EasyTileRenderer>();

    public static void addMagickParticle(LitParticle par)
    {
        particles.add(par);
    }
    public static void putEasyRender(Class clas, EasyRenderer renderer)
    {
        easyRenderer.put(clas, renderer);
    }
    public static void putLayerRender(Class clas, EasyLayerRender renderer) { layerRenderer.put(clas, renderer); }
    public static void putLaserRender(Class clas, EasyRenderer renderer) { laserRenderer.put(clas, renderer); }
    public static void putTileRender(Class clas, EasyTileRenderer renderer) { tileRenderer.put(clas, renderer); }

    private static final HashMap<CanSeeTileEntity, Integer> tileRenderTick = new HashMap<CanSeeTileEntity, Integer>();
    private static final ManaFreezeRenderer freezeRender = new ManaFreezeRenderer();

    public static void activeTileEntityRender(CanSeeTileEntity entity)
    {
        tileRenderTick.put(entity, 5);
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
        IEntityState state = Minecraft.getInstance().player.getCapability(MagickCore.entityState).orElse(null);
        if(state != null) {
            ManaBarGUI manaBarGUI = new ManaBarGUI(event.getMatrixStack(), state);
            manaBarGUI.render();
        }
    }

    @SubscribeEvent
    public void renderTileEntity(RenderWorldLastEvent event)
    {
        MatrixStack matrixStackIn = event.getMatrixStack();
        IRenderTypeBuffer.Impl bufferIn = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        for(TileEntity entity : Minecraft.getInstance().world.loadedTileEntityList)
        {
            if(tileRenderTick.containsKey(entity) && tileRenderer.containsKey(entity.getClass())) {
                tileRenderer.get(entity.getClass()).preRender(entity, matrixStackIn, bufferIn, event.getPartialTicks());
                tickTileRender((CanSeeTileEntity) entity);
            }
        }
    }

    @SubscribeEvent
    public void renderParticle(RenderWorldLastEvent event)
    {
        Vector3d vec = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        IRenderTypeBuffer.Impl bufferIn = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());

        Vector3d vector3d = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double d0 = vector3d.getX();
        double d1 = vector3d.getY();
        double d2 = vector3d.getZ();
        Matrix4f matrix4f = event.getMatrixStack().getLast().getMatrix();

        ClippingHelper clippinghelper = new ClippingHelper(matrix4f, event.getProjectionMatrix());
        clippinghelper.setCameraPosition(d0, d1, d2);

        for(int i = 0; i < particles.size(); ++i)
        {
            if(isInRangeToRender3d(particles.get(i), vec.x, vec.y, vec.z) && shouldRender(clippinghelper, particles.get(i)))
                particles.get(i).render(event.getMatrixStack(), bufferIn);
        }
    }

    public boolean shouldRender(ClippingHelper clippinghelper, LitParticle par)
    {
        AxisAlignedBB axisalignedbb = par.getBoundingBox().grow(0.5D);
        if (axisalignedbb.hasNaN() || axisalignedbb.getAverageEdgeLength() == 0.0D) {
            axisalignedbb = new AxisAlignedBB(par.getPosX() - 2.0D, par.getPosY() - 2.0D, par.getPosZ() - 2.0D, par.getPosX() + 2.0D, par.getPosY() + 2.0D, par.getPosZ() + 2.0D);
        }
        return clippinghelper.isBoundingBoxInFrustum(axisalignedbb);
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
        d0 = d0 * 64.0D * 2.0;
        return distance < d0 * d0;
    }

    @SubscribeEvent
    public void cancelFreezeEntity(RenderLivingEvent.Pre event) {
        IEntityState state = event.getEntity().getCapability(MagickCore.entityState).orElse(null);
        if (state != null && state.getBuffList().containsKey(LibBuff.FREEZE)) {
            event.setCanceled(true);
            freezeRender.render(event.getEntity(), event.getRenderer(), event.getMatrixStack(), event.getBuffers(), 0);
        }
    }

    @SubscribeEvent
    public void renderEntityLayer(RenderLivingEvent.Post event)
    {
        if(layerRenderer.containsKey(event.getEntity().getClass())) {
            EasyLayerRender renderer = layerRenderer.get(event.getEntity().getClass());
            renderer.preRender(event.getEntity(), event.getRenderer(), event.getMatrixStack(), event.getBuffers(), event.getPartialRenderTick());
        }

        Iterator<Class> iter = layerRenderer.keySet().iterator();
        while (iter.hasNext()) {
            Class clas = iter.next();
            if (clas == AllEntity.class) {
                EasyLayerRender renderer = layerRenderer.get(clas);
                renderer.preRender(event.getEntity(), event.getRenderer(), event.getMatrixStack(), event.getBuffers(), event.getPartialRenderTick());
            }
        }
    }

    @SubscribeEvent
    public void renderEntity(RenderWorldLastEvent event)
    {
        MatrixStack matrixStackIn = event.getMatrixStack();
        IRenderTypeBuffer.Impl bufferIn = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());

        for(Entity entity : Minecraft.getInstance().world.getAllEntities())
        {
            Iterator<Class> iter = easyRenderer.keySet().iterator();
            while (iter.hasNext()) {
                Class clas = iter.next();
                if(entity.getClass() == clas || clas == AllEntity.class) {
                        EasyRenderer renderer = easyRenderer.get(clas);
                        renderer.preRender(entity, matrixStackIn, bufferIn, event.getPartialTicks());
                }
            }

            Iterator<Class> it = laserRenderer.keySet().iterator();
            while (it.hasNext()) {
                Class clas = it.next();
                if(entity.getClass() == clas || clas == AllEntity.class) {
                    EasyRenderer renderer = laserRenderer.get(clas);
                    renderer.preRender(entity, matrixStackIn, bufferIn, event.getPartialTicks());
                }
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

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(Minecraft.getInstance().isGamePaused() || Minecraft.getInstance().world == null || event.phase == TickEvent.Phase.START)
            return;
        Vector3d vec = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        VertexShakerHelper.tickGroup();
        for(int i = 0; i < particles.size(); ++i)
        {
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

        Minecraft.getInstance().world.getAllEntities().forEach((e) -> MinecraftForge.EVENT_BUS.post(new EntityEvents.EntityUpdateEvent(e)));
    }

    @SubscribeEvent
    public void onClientWorldTick(TickEvent.ClientTickEvent event)
    {
        if(Minecraft.getInstance().isGamePaused() || Minecraft.getInstance().world == null || event.phase == TickEvent.Phase.START)
            return;
        Iterator<Entity> list = Minecraft.getInstance().world.getAllEntities().iterator();
        while (list.hasNext())
        {
            Entity entity = list.next();
            IEntityState state = entity.getCapability(MagickCore.entityState).orElse(null);
            if(state != null && !state.getBuffList().isEmpty())
            {
                if(state.getBuffList().containsKey(LibBuff.PARALYSIS))
                    applydeBuffParticle(entity, MagickCore.proxy.getElementRender(LibElements.ARC), Minecraft.getInstance().world);

                if(state.getBuffList().containsKey(LibBuff.WITHER) || state.getBuffList().containsKey(LibBuff.CRIPPLE))
                    applydeBuffParticle(entity, MagickCore.proxy.getElementRender(LibElements.WITHER), Minecraft.getInstance().world);

                if(state.getBuffList().containsKey(LibBuff.FREEZE) || state.getBuffList().containsKey(LibBuff.SLOW))
                    applydeBuffParticle(entity, MagickCore.proxy.getElementRender(LibElements.STASIS), Minecraft.getInstance().world);

                if(state.getBuffList().containsKey(LibBuff.FRAGILE) || state.getBuffList().containsKey(LibBuff.WEAKEN))
                    applydeBuffParticle(entity, MagickCore.proxy.getElementRender(LibElements.VOID), Minecraft.getInstance().world);

                if(state.getBuffList().containsKey(LibBuff.TAKEN))
                    applydeBuffParticle(entity, MagickCore.proxy.getElementRender(LibElements.TAKEN), Minecraft.getInstance().world);

                /////////////////////////////BUFF//////////////////////////////////////

                if(state.getBuffList().containsKey(LibBuff.STASIS))
                    applybuffParticle(entity, MagickCore.proxy.getElementRender(LibElements.STASIS), Minecraft.getInstance().world);

                if(state.getBuffList().containsKey(LibBuff.LIGHT))
                    applybuffParticle(entity, MagickCore.proxy.getElementRender(LibElements.VOID), Minecraft.getInstance().world);

                if(state.getBuffList().containsKey(LibBuff.RADIANCE_WELL))
                    applybuffParticle(entity, MagickCore.proxy.getElementRender(LibElements.SOLAR), Minecraft.getInstance().world);

                if(state.getBuffList().containsKey(LibBuff.DECAY))
                    applybuffParticle(entity, MagickCore.proxy.getElementRender(LibElements.WITHER), Minecraft.getInstance().world);

                if(state.getBuffList().containsKey(LibBuff.HYPERMUTEKI))
                    applybuffParticle(entity, MagickCore.proxy.getElementRender(LibElements.ORIGIN), Minecraft.getInstance().world);
            }

            IElementAnimalState animalState = entity.getCapability(MagickCore.elementAnimal).orElse(null);
            if(animalState != null && animalState.getElement().getType() != LibElements.ORIGIN)
            {
                applyAnimalParticle(entity, MagickCore.proxy.getElementRender(animalState.getElement().getType()), Minecraft.getInstance().world);
            }
        }
    }

    public static void applybuffParticle(Entity entity, ElementRenderer render, World world)
    {
        for(int i = 0; i < 3; ++i) {
            LitParticle litPar = new LitParticle(world, render.getParticleTexture()
                    , new Vector3d(MagickCore.getNegativeToOne() * entity.getWidth() * 2 + entity.getPosX()
                    , MagickCore.getNegativeToOne() * entity.getHeight() * 2 + entity.getPosY() + entity.getHeight() / 2
                    , MagickCore.getNegativeToOne() * entity.getWidth()  * 2 + entity.getPosZ())
                    , entity.getWidth() / 5f * MagickCore.rand.nextFloat(), entity.getWidth() / 5f * MagickCore.rand.nextFloat(), 0.8f * MagickCore.rand.nextFloat(), 40, render);
            litPar.setGlow();
            litPar.setParticleGravity(0f);
            litPar.addMotion(MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10, MagickCore.getNegativeToOne() / 10);
            litPar.setTraceTarget(entity);
            MagickCore.addMagickParticle(litPar);
        }
    }

    public static void applyAnimalParticle(Entity entity, ElementRenderer render, World world)
    {
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

    public static void applydeBuffParticle(Entity entity, ElementRenderer render, World world)
    {
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
