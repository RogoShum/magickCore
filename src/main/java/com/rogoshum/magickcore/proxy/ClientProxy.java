package com.rogoshum.magickcore.proxy;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.IEasyRender;
import com.rogoshum.magickcore.block.tileentity.*;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.render.*;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.client.render.RenderThread;
import com.rogoshum.magickcore.client.entity.easyrender.*;
import com.rogoshum.magickcore.client.entity.easyrender.laser.*;
import com.rogoshum.magickcore.client.entity.easyrender.layer.*;
import com.rogoshum.magickcore.client.element.*;
import com.rogoshum.magickcore.client.entity.easyrender.superrender.*;
import com.rogoshum.magickcore.client.item.MagickBakedModel;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.shader.LightShaderManager;
import com.rogoshum.magickcore.client.tileentity.CanSeeTileEntityRenderer;
import com.rogoshum.magickcore.client.tileentity.MagickRepeaterRenderer;
import com.rogoshum.magickcore.client.tileentity.MaterialJarRenderer;
import com.rogoshum.magickcore.client.tileentity.SpiritCrystalRenderer;
import com.rogoshum.magickcore.client.tileentity.easyrender.*;
import com.rogoshum.magickcore.entity.pointed.*;
import com.rogoshum.magickcore.entity.projectile.*;
import com.rogoshum.magickcore.entity.superentity.*;
import com.rogoshum.magickcore.event.RenderEvent;
import com.rogoshum.magickcore.event.ShaderEvent;
import com.rogoshum.magickcore.init.*;
import com.rogoshum.magickcore.lib.LibRegistry;
import com.rogoshum.magickcore.registry.ObjectRegistry;
import com.rogoshum.magickcore.registry.elementmap.EntityRenderers;
import com.rogoshum.magickcore.registry.elementmap.RenderFunctions;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.awt.*;
import java.util.*;
import java.util.function.Consumer;

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
		if(renderThread == null || renderThread.isInterrupted() || !renderThread.isAlive() || renderThread.getState().equals(Thread.State.WAITING) || renderThread.getState().equals(Thread.State.BLOCKED)) {
			createRenderer();
		}
	}


	public void createRenderer() {
		if (renderThread != null) {
			renderThread.interrupt();
		}
		renderThread = new RenderThread("MagickCore Render Thread");
		renderThread.start();
	}

	@Override
	public void addRenderer(IEasyRender renderer) {
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
		if(magickThread == null || magickThread.isInterrupted() || !magickThread.isAlive() || magickThread.getState().equals(Thread.State.WAITING) || magickThread.getState().equals(Thread.State.BLOCKED))
			createThread();
	}

	public void createThread() {
		if (magickThread != null) {
			magickThread.interrupt();
		}
		magickThread = new TaskThread("MagickCore Client Thread");
		magickThread.start();
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
		ModElements.elements.forEach( elementType -> {
			entityRenderers.register(elementType, EntityRenderers.create());
			renderFunctions.register(elementType, RenderFunctions.create());
		});
	}

	public void registerEntityRenderer(FMLClientSetupEvent event) {
		//RenderingRegistry.registerEntityRenderingHandler(ModEntites.time_manager, TimeManagerRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.mana_orb.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.mana_shield.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.mana_star.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.mana_laser.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.mana_sphere.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.radiance_wall.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.chaos_reach.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.thorns_caress.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.silence_squall.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.ascendant_realm.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.mana_power.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.element_orb.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.life_state.get(), LifeStateEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.context_creator.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.mana_capacity.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.context_pointer.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.ray_trace.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.entity_capture.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.cone.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.sector.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.sphere.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.square.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.ray.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.blood_bubble.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.lamp.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.arrow.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.bubble.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.leaf.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.red_stone.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.shadow.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.wind.get(), ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.gravity_lift.get(), ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.placeable_entity.get(), PlaceableItemEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.MAGE.get(), MageRenderer::new);
	}

	public void registerItemColors(ColorHandlerEvent.Item event) {
		IItemColor color = (stack, p_getColor_2_) -> {
			if(NBTTagHelper.hasElement(stack)) {
				com.rogoshum.magickcore.magick.Color color1 = MagickCore.proxy.getElementRender(NBTTagHelper.getElement(stack)).getColor();
				float[] hsv = Color.RGBtoHSB((int) (color1.r() * 255), (int) (color1.g() * 255), (int) (color1.b() * 255), null);
				return MathHelper.hsvToRGB(hsv[0], hsv[1], hsv[2]);
			}
			return 	16777215;
		};
		event.getItemColors().register(color, ModItems.element_crystal_seeds.get());
		event.getItemColors().register(color, ModItems.element_meat.get());
		event.getItemColors().register(color, ModItems.element_crystal.get());
		event.getItemColors().register(color, ModItems.element_string.get());
		event.getItemColors().register(color, ModItems.element_wool.get());
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
		ClientRegistry.bindTileEntityRenderer(ModTileEntities.magick_container_tileentity.get(), (CanSeeTileEntityRenderer<MagickContainerTileEntity>::new));
		RenderTypeLookup.setRenderLayer(ModBlocks.magick_container.get(), RenderType.getCutout());
		ClientRegistry.bindTileEntityRenderer(ModTileEntities.element_crystal_tileentity.get(), (CanSeeTileEntityRenderer<ElementCrystalTileEntity>::new));
		RenderTypeLookup.setRenderLayer(ModBlocks.element_crystal.get(), RenderType.getCutout());
		ClientRegistry.bindTileEntityRenderer(ModTileEntities.element_wool_tileentity.get(), (CanSeeTileEntityRenderer<ElementWoolTileEntity>::new));
		RenderTypeLookup.setRenderLayer(ModBlocks.element_wool.get(), RenderType.getCutout());
		ClientRegistry.bindTileEntityRenderer(ModTileEntities.magick_repeater_tileentity.get(), (MagickRepeaterRenderer::new));
		RenderTypeLookup.setRenderLayer(ModBlocks.magick_repeater.get(), RenderType.getCutout());
		ClientRegistry.bindTileEntityRenderer(ModTileEntities.magick_barrier_tileentity.get(), (CanSeeTileEntityRenderer<MagickBarrierTileEntity>::new));
		RenderTypeLookup.setRenderLayer(ModBlocks.magick_barrier.get(), RenderType.getCutout());

		RenderTypeLookup.setRenderLayer(ModBlocks.magick_supplier.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.void_sphere.get(), RenderType.getTranslucent());
	}

	private void putElementRenderer() {
		putElementRendererIn(LibElements.ORIGIN, new OriginRenderer());

		putElementRendererIn(LibElements.SOLAR, new SolarRenderer());
		putElementRendererIn(LibElements.VOID, new VoidRenderer());
		putElementRendererIn(LibElements.ARC, new ArcRenderer());

		putElementRendererIn(LibElements.STASIS, new StasisRenderer());
		putElementRendererIn(LibElements.WITHER, new WitherRenderer());
		putElementRendererIn(LibElements.TAKEN, new TakenRenderer());
		putElementRendererIn(LibElements.AIR, new AirRenderer());
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
