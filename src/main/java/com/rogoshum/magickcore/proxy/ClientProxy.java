package com.rogoshum.magickcore.proxy;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.block.tileentity.*;
import com.rogoshum.magickcore.client.VertexShakerHelper;
import com.rogoshum.magickcore.client.entity.easyrender.*;
import com.rogoshum.magickcore.client.entity.easyrender.laser.*;
import com.rogoshum.magickcore.client.entity.easyrender.layer.*;
import com.rogoshum.magickcore.client.element.*;
import com.rogoshum.magickcore.client.entity.easyrender.superrender.*;
import com.rogoshum.magickcore.client.entity.render.LifeStateEntityRenderer;
import com.rogoshum.magickcore.client.entity.render.ManaEntityRenderer;
import com.rogoshum.magickcore.client.entity.render.ManaObjectRenderer;
import com.rogoshum.magickcore.client.item.MagickBakedModel;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.shader.LightShaderManager;
import com.rogoshum.magickcore.client.tileentity.easyrender.*;
import com.rogoshum.magickcore.entity.pointed.*;
import com.rogoshum.magickcore.entity.projectile.*;
import com.rogoshum.magickcore.entity.superentity.*;
import com.rogoshum.magickcore.event.RenderEvent;
import com.rogoshum.magickcore.event.ShaderEvent;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibRegistry;
import com.rogoshum.magickcore.magick.MagickPoint;
import com.rogoshum.magickcore.registry.ObjectRegistry;
import com.rogoshum.magickcore.registry.elementmap.EntityRenderers;
import com.rogoshum.magickcore.registry.elementmap.RenderFunctions;
import com.rogoshum.magickcore.tool.EntityLightSourceHandler;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import com.rogoshum.magickcore.init.ModEntities;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.color.IItemColor;
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
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.awt.*;
import java.util.*;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IProxy
{
	private final HashMap<String, ElementRenderer> renderers = new HashMap<>();
	public final ShaderEvent event = new ShaderEvent();
	private int clientPreTick;
	private volatile int clientTick;
	private int serverPreTick;
	private volatile int serverTick;
	private final List<Runnable> taskList = new Vector<>();

	private Thread magickThread;

	public ElementRenderer getElementRender(String name) {
		return this.renderers.get(name) != null ? this.renderers.get(name) : this.renderers.get(LibElements.ORIGIN);
	}

	@Override
	public void tick(LogicalSide side) {
		if(side.isClient()) {
			int pre = clientTick;
			clientTick = 1 + pre;
		}
		else {
			int pre = serverTick;
			serverTick = 1 + pre;
		}
	}

	@Override
	public int getRunTick() {
		return clientTick;
	}

	@Override
	public void addTask(Runnable run) {
		taskList.add(run);
	}

	@Override
	public void createThread() {
		if(magickThread != null) {
			magickThread.interrupt();
		}
		magickThread = new Thread(() -> {
			while (!magickThread.isInterrupted()) {
				boolean vertexShaker = false;
				boolean tickParticle = false;
				boolean tickTask = false;
				try {
					if(clientTick > clientPreTick) {
						clientPreTick = clientTick;
						if(Minecraft.getInstance().player == null) {
							EntityLightSourceHandler.clear();
							RenderEvent.clearParticle();
							VertexShakerHelper.clear();
						}

						VertexShakerHelper.tickGroup();
						vertexShaker = true;
						RenderEvent.tickParticle();
						tickParticle = true;
						MagickPoint.points.forEach(MagickPoint::tick);

						for (int i = 0; i < taskList.size(); ++i) {
							taskList.get(i).run();
						}
						tickTask = true;
						taskList.clear();
					}

					if(serverTick > serverPreTick) {
						//EntityLightSourceHandler.tick(LogicalSide.SERVER);
						serverPreTick = serverTick;
					}
				} catch (Exception e) {
					MagickCore.LOGGER.info("MagickCore Client Thread Crashed!");
					MagickCore.LOGGER.info("happened: ");
					if(!vertexShaker)
						MagickCore.LOGGER.info("vertexShaker");
					else if(!tickParticle)
						MagickCore.LOGGER.info("tickParticle");
					else if(!tickTask)
						MagickCore.LOGGER.info("tickTask");
					else
						MagickCore.LOGGER.info("other");

					MagickCore.LOGGER.debug(e);
					RenderEvent.clearParticle();
					VertexShakerHelper.clear();
					taskList.clear();
					createThread();
				}
		}}, "MagickCore Client Thread");
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
		registerEasyRenderer();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBaked);

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
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.mana_rift.get(), ManaEntityRenderer::new);
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

	public void onModelBaked(ModelBakeEvent event) {
		Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
		ModelResourceLocation location = new ModelResourceLocation(ModItems.orb_bottle.get().getRegistryName(), "inventory");
		IBakedModel existingModel = modelRegistry.get(location);
		if (existingModel == null) {
			throw new RuntimeException("Did not find magick_crafting in registry");
		} else {
			MagickBakedModel magickBakedModel = new MagickBakedModel(existingModel);
			event.getModelRegistry().put(location, magickBakedModel);
		}

		location = new ModelResourceLocation(ModItems.element_wool.get().getRegistryName(), "inventory");
		existingModel = modelRegistry.get(location);
		if (existingModel == null) {
			throw new RuntimeException("Did not find magick_crafting in registry");
		} else {
			MagickBakedModel magickBakedModel = new MagickBakedModel(existingModel);
			event.getModelRegistry().put(location, magickBakedModel);
		}
	}

	public void putElementRendererIn(String name, ElementRenderer renderer)
	{
		renderers.put(name, renderer);
	}

	private void registerEasyRenderer() {
		//normal
		RenderEvent.registerEasyRender(ManaOrbEntity.class, new ManaOrbRenderer());
		RenderEvent.registerEasyRender(DawnWardEntity.class, new DawnWardRenderer());
		RenderEvent.registerEasyRender(Entity.class, new ElementShieldRenderer());
		RenderEvent.registerEasyRender(ManaStarEntity.class, new ManaStarRenderer());
		RenderEvent.registerEasyRender(ManaLaserEntity.class, new ManaLaserRenderer());
		RenderEvent.registerEasyRender(ManaRiftEntity.class, new ManaRiftRenderer());
		RenderEvent.registerEasyRender(ManaSphereEntity.class, new ManaSphereRenderer());
		RenderEvent.registerEasyRender(RadianceWellEntity.class, new RadianceWellRenderer());
		RenderEvent.registerEasyRender(ChaoReachEntity.class, new ChaosReachRenderer());
		RenderEvent.registerEasyRender(SilenceSquallEntity.class, new SilenceSqualRenderer());
		RenderEvent.registerEasyRender(ThornsCaressEntity.class, new ThornsCaressRenderer());
		RenderEvent.registerEasyRender(AscendantRealmEntity.class, new AscendantRealmRenderer());
		RenderEvent.registerEasyRender(ContextCreatorEntity.class, new ContextCreatorRenderer());
		RenderEvent.registerEasyRender(ManaCapacityEntity.class, new ManaCapacityRenderer());
		RenderEvent.registerEasyRender(ContextPointerEntity.class, new ContextPointerRenderer());
		RenderEvent.registerEasyRender(BubbleEntity.class, new BubbleRenderer());
		RenderEvent.registerEasyRender(BloodBubbleEntity.class, new BloodBubbleRenderer());
		RenderEvent.registerEasyRender(EntityHunterEntity.class, new EntityHunterRenderer());
		RenderEvent.registerEasyRender(GravityLiftEntity.class, new GravityLiftRenderer());
		RenderEvent.registerEasyRender(LeafEntity.class, new LeafRenderer());
		RenderEvent.registerEasyRender(RedStoneEntity.class, new RedStoneRenderer());
		RenderEvent.registerEasyRender(WindEntity.class, new WindRenderer());
		RenderEvent.registerEasyRender(RayEntity.class, new RayRenderer());

		//layer
		RenderEvent.registerLayerRender(LivingEntity.class, new ManaBuffRenderer());
		RenderEvent.registerLayerRender(ClientPlayerEntity.class, new PlayerShieldRenderer());

		//laser
		RenderEvent.registerLaserRender(RadianceWellEntity.class, new RadianceWellLaserRenderer());
		RenderEvent.registerLaserRender(ChaoReachEntity.class, new ChaosReachLaserRenderer());
		RenderEvent.registerLaserRender(ThornsCaressEntity.class, new ThornsCaressLaserRenderer());

		//tile
		RenderEvent.registerTileRender(MagickCraftingTileEntity.class, new MagickCraftingRenderer());
		RenderEvent.registerTileRender(MagickContainerTileEntity.class, new MagickContainerRenderer());
		RenderEvent.registerTileRender(ElementCrystalTileEntity.class, new ElementCrystalRenderer());
		RenderEvent.registerTileRender(ElementWoolTileEntity.class, new ElementWoolRenderer());
		RenderEvent.registerTileRender(MagickBarrierTileEntity.class, new MagickBarrierRenderer());
		RenderEvent.registerTileRender(MagickRepeaterTileEntity.class, new MagickRepeaterRenderer());
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

		if(!RenderEvent.isInRangeToRender3d(par, vec.x, vec.y, vec.z))
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

	public static void registerParticleSprite(TextureStitchEvent.Pre event, String name) {
		String path = MagickCore.MOD_ID + ":element/" + name;
		if(event.addSprite(new ResourceLocation(path)))
			MagickCore.LOGGER.info("Successes to add sprite [" + name + "]");
		else
			MagickCore.LOGGER.info("Failed to add sprite [" + name + "]");
	}

	public static class ErrorRenderer extends ElementRenderer {
		public ErrorRenderer() {
			super(com.rogoshum.magickcore.magick.Color.create(1f, 0, 0));
		}
	}
}
