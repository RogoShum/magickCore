package com.rogoshum.magickcore.proxy;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.render.IEasyRender;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.render.*;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.client.render.RenderThread;
import com.rogoshum.magickcore.client.element.*;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.shader.LightShaderManager;
import com.rogoshum.magickcore.client.tileentity.*;
import com.rogoshum.magickcore.client.event.RenderEvent;
import com.rogoshum.magickcore.client.event.ShaderEvent;
import com.rogoshum.magickcore.common.init.*;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.registry.ObjectRegistry;
import com.rogoshum.magickcore.common.registry.elementmap.EntityRenderers;
import com.rogoshum.magickcore.common.registry.elementmap.RenderFunctions;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.lib.LibElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IProxy {
	private final HashMap<String, ElementRenderer> renderers = new HashMap<>();
	public final ShaderEvent event = new ShaderEvent();
	private TaskThread magickThread;
	private RenderThread renderThread;

	public ElementRenderer getElementRender(String name) {
		return this.renderers.get(name) != null ? this.renderers.get(name) : this.renderers.get(LibElements.ORIGIN);
	}

	public void checkRenderer() {
		if(renderThread != null && renderThread.getState().equals(Thread.State.WAITING)) {
			renderThread.notify();
			return;
		}
		if(renderThread == null || renderThread.isInterrupted() || !renderThread.isAlive() || renderThread.getState().equals(Thread.State.BLOCKED)) {
			if(renderThread != null && renderThread.isInterrupted())
				MagickCore.LOGGER.info("Render Thread Interrupted");
			if(renderThread != null && renderThread.getState().equals(Thread.State.BLOCKED))
				MagickCore.LOGGER.info("Render Thread BLOCKED");
			createRenderer();
		}
	}


	public void createRenderer() {
		if (renderThread != null) {
			renderThread.interrupt();
		}
		renderThread = new RenderThread("MagickCore Render Thread");
		renderThread.start();
		MagickCore.LOGGER.info("Create New Render Thread");
	}

	@Override
	public void addRenderer(Supplier<IEasyRender> renderSupplier) {
		IEasyRender renderer = renderSupplier.get();
		if(renderer instanceof EasyRenderer && !((EasyRenderer<?>) renderer).isRemote()) {
			return;
		}
		checkRenderer();
		renderThread.addRenderer(renderer);
	}

	@Override
	public HashMap<RenderMode, Queue<Consumer<RenderParams>>> getGlFunction() {
		checkRenderer();
		return renderThread.getGlFunction();
	}

	public ConcurrentLinkedQueue<IEasyRender> getRenderer() {
		checkRenderer();
		return renderThread.getRenderer();
	}

	@Override
	public void updateRenderer() {
		checkRenderer();
		renderThread.update();
	}

	@Override
	public void setClippingHelper(ClippingHelper clippingHelper) {
		checkRenderer();
		renderThread.setClippingHelper(clippingHelper);
	}

	@Override
	public void tick(LogicalSide side) {
		checkThread();
		magickThread.tick();
	}

	@Override
	public int getRunTick() {
		checkThread();
		return magickThread.getTick();
	}

	@Override
	public void addTask(Runnable run) {
		checkThread();
		magickThread.addTask(run);
	}

	@Override
	public void addAdditionTask(Runnable tryTask, Runnable catchTask) {
		checkThread();
		magickThread.setAdditionTask(tryTask);
		magickThread.setAdditionCatch(catchTask);
	}

	public void checkThread() {
		if(magickThread == null || magickThread.isInterrupted() || !magickThread.isAlive() || magickThread.getState().equals(Thread.State.WAITING) || magickThread.getState().equals(Thread.State.BLOCKED)) {
			if(magickThread != null && magickThread.isInterrupted())
				MagickCore.LOGGER.info("Task Thread Interrupted");
			if(magickThread != null && magickThread.getState().equals(Thread.State.BLOCKED))
				MagickCore.LOGGER.info("Task Thread BLOCKED");
			createThread();
		}
	}

	public void createThread() {
		if (magickThread != null) {
			magickThread.interrupt();
		}
		magickThread = new TaskThread("MagickCore Client Thread");
		magickThread.start();
		MagickCore.LOGGER.info("Create New Task Thread");
	}

	public void init() {}
	
	public void preInit() {}

	public void registerHandlers() {
		MinecraftForge.EVENT_BUS.register(new RenderEvent());
		MinecraftForge.EVENT_BUS.register(new LightShaderManager());
		MinecraftForge.EVENT_BUS.addListener(event::onSetupShaders);
		MinecraftForge.EVENT_BUS.addListener(event::onRenderShaders);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerEntityRenderer);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(event::onModelRegistry);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerItemColors);
		putElementRenderer();
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBaked);

		ObjectRegistry<EntityRenderers> entityRenderers = new ObjectRegistry<>(LibRegistry.ENTITY_RENDERER);
		ObjectRegistry<RenderFunctions> renderFunctions = new ObjectRegistry<>(LibRegistry.RENDER_FUNCTION);
		ModElements.elements.forEach(elementType -> {
			entityRenderers.register(elementType, EntityRenderers.create());
			renderFunctions.register(elementType, RenderFunctions.create());
		});
		ManaEnergyRenderer.addApplyTypeTexture(ApplyType.ATTACK, new ResourceLocation(MagickCore.MOD_ID, "textures/apply_type/attack.png"));
		ManaEnergyRenderer.addApplyTypeTexture(ApplyType.BUFF, new ResourceLocation(MagickCore.MOD_ID, "textures/apply_type/buff.png"));
		ManaEnergyRenderer.addApplyTypeTexture(ApplyType.DE_BUFF, new ResourceLocation(MagickCore.MOD_ID, "textures/apply_type/debuff.png"));
		ManaEnergyRenderer.addApplyTypeTexture(ApplyType.AGGLOMERATE, new ResourceLocation(MagickCore.MOD_ID, "textures/apply_type/agglomerate.png"));
		ManaEnergyRenderer.addApplyTypeTexture(ApplyType.DIFFUSION, new ResourceLocation(MagickCore.MOD_ID, "textures/apply_type/diffusion.png"));
	}

	public void registerEntityRenderer(FMLClientSetupEvent event) {
		//RenderingRegistry.registerEntityRenderingHandler(ModEntites.time_manager, TimeManagerRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.MANA_ORB.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.MANA_SHIELD.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.MANA_STAR.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.MANA_LASER.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.MANA_SPHERE.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.RADIANCE_WALL.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.CHAOS_REACH.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.THORNS_CARESS.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.SILENCE_SQUALL.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.ASCENDANT_REALM.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.ELEMENT_ORB.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.CONTEXT_CREATOR.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.MANA_CAPACITY.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.CONTEXT_POINTER.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.RAY_TRACE.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.ENTITY_CAPTURE.get(), EntityHunterRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.CONE.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.SECTOR.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.SPHERE.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.SQUARE.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.RAY.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.BLOOD_BUBBLE.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.LAMP.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.ARROW.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.BUBBLE.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.LEAF.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.RED_STONE.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.SHADOW.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.WIND.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.JEWELRY_BAG.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.REPEATER.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.GRAVITY_LIFT.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.PLACEABLE_ENTITY.get(), PlaceableItemEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.MAGE.get(), MageRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.PHANTOM.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.ARTIFICIAL_LIFE.get(), ArtificialLifeEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.CHAIN.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.SPIN.get(), ManaEntityRenderer::new);
	}

	public void registerItemColors(ColorHandlerEvent.Item event) {
		IItemColor color = (stack, p_getColor_2_) -> {
			if(NBTTagHelper.hasElement(stack)) {
				com.rogoshum.magickcore.common.magick.Color color1 = MagickCore.proxy.getElementRender(NBTTagHelper.getElement(stack)).getColor();
				float[] hsv = Color.RGBtoHSB((int) (color1.r() * 255), (int) (color1.g() * 255), (int) (color1.b() * 255), null);
				return MathHelper.hsvToRGB(hsv[0], hsv[1], hsv[2]);
			}
			return 	16777215;
		};
		event.getItemColors().register(color, ModItems.ELEMENT_CRYSTAL_SEEDS.get());
		event.getItemColors().register(color, ModItems.ELEMENT_MEAT.get());
		event.getItemColors().register(color, ModItems.ELEMENT_CRYSTAL.get());
		event.getItemColors().register(color, ModItems.ELEMENT_STRING.get());
		event.getItemColors().register(color, ModItems.ELEMENT_WOOL.get());
	}

	public void putElementRendererIn(String name, ElementRenderer renderer) {
		renderers.put(name, renderer);
	}

	public void initBlockRenderer() {
		ClientRegistry.bindTileEntityRenderer(ModTileEntities.magick_crafting_tileentity.get(), com.rogoshum.magickcore.client.tileentity.MagickCraftingRenderer::new);
		RenderTypeLookup.setRenderLayer(ModBlocks.magick_crafting.get(), RenderType.getCutout());
		ClientRegistry.bindTileEntityRenderer(ModTileEntities.spirit_crystal_tileentity.get(), SpiritCrystalRenderer::new);
		RenderTypeLookup.setRenderLayer(ModBlocks.spirit_crystal.get(), RenderType.getCutout());
		ClientRegistry.bindTileEntityRenderer(ModTileEntities.MATERIAL_JAR_TILE_ENTITY.get(), MaterialJarRenderer::new);
		RenderTypeLookup.setRenderLayer(ModBlocks.MATERIAL_JAR.get(), RenderType.getCutout());
		ClientRegistry.bindTileEntityRenderer(ModTileEntities.element_crystal_tileentity.get(), ElementCrystalRenderer::new);
		RenderTypeLookup.setRenderLayer(ModBlocks.element_crystal.get(), RenderType.getCutout());
		ClientRegistry.bindTileEntityRenderer(ModTileEntities.element_wool_tileentity.get(), ElementWoolRenderer::new);
		RenderTypeLookup.setRenderLayer(ModBlocks.element_wool.get(), RenderType.getSolid());
	}

	private void putElementRenderer() {
		putElementRendererIn(LibElements.ORIGIN, new OriginRenderer());

		putElementRendererIn(LibElements.SOLAR, new SolarRenderer());
		putElementRendererIn(LibElements.VOID, new VoidRenderer());
		putElementRendererIn(LibElements.ARC, new ArcRenderer());

		putElementRendererIn(LibElements.STASIS, new StasisRenderer());
		putElementRendererIn(LibElements.WITHER, new WitherRenderer());
		putElementRendererIn(LibElements.TAKEN, new TakenRenderer());
	}

	public void addMagickParticle(LitParticle par) {
		Vector3d vec = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();

		if(!RenderHelper.isInRangeToRender3d(par, vec.x, vec.y, vec.z))
			return;

		if(Minecraft.getInstance().gameSettings.particles == ParticleStatus.DECREASED) {
			if(MagickCore.rand.nextInt(3) > 0)
				RenderEvent.addMagickParticle(par);
		}
		else if(Minecraft.getInstance().gameSettings.particles == ParticleStatus.MINIMAL) {
			if(MagickCore.rand.nextInt(4) == 0)
				RenderEvent.addMagickParticle(par);
		}
		else {
			RenderEvent.addMagickParticle(par);
		}
	}
}
