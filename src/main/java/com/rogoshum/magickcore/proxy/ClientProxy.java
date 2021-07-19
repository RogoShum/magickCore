package com.rogoshum.magickcore.proxy;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.AllEntity;
import com.rogoshum.magickcore.block.tileentity.ElementCrystalTileEntity;
import com.rogoshum.magickcore.block.tileentity.ElementWoolTileEntity;
import com.rogoshum.magickcore.block.tileentity.MagickContainerTileEntity;
import com.rogoshum.magickcore.block.tileentity.MagickCraftingTileEntity;
import com.rogoshum.magickcore.client.entity.easyrender.*;
import com.rogoshum.magickcore.client.entity.easyrender.laser.*;
import com.rogoshum.magickcore.client.entity.easyrender.layer.*;
import com.rogoshum.magickcore.client.element.*;
import com.rogoshum.magickcore.client.entity.easyrender.outline.ManaRiftOutlineRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.superrender.*;
import com.rogoshum.magickcore.client.entity.render.ManaEntityRenderer;
import com.rogoshum.magickcore.client.entity.render.ManaObjectRenderer;
import com.rogoshum.magickcore.client.entity.render.living.TimeManagerRenderer;
import com.rogoshum.magickcore.client.item.MagickBakedModel;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.tileentity.easyrender.ElementCrystalRenderer;
import com.rogoshum.magickcore.client.tileentity.easyrender.ElementWoolRenderer;
import com.rogoshum.magickcore.client.tileentity.easyrender.MagickContainerRenderer;
import com.rogoshum.magickcore.client.tileentity.easyrender.MagickCraftingRenderer;
import com.rogoshum.magickcore.entity.*;
import com.rogoshum.magickcore.entity.superentity.*;
import com.rogoshum.magickcore.event.RenderEvent;
import com.rogoshum.magickcore.event.RenderOutlineEvent;
import com.rogoshum.magickcore.helper.NBTTagHelper;
import com.rogoshum.magickcore.init.ModEntites;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IProxy
{
	private HashMap<String, ElementRenderer> renderers = new HashMap<String, ElementRenderer>();
	private RenderOutlineEvent event = new RenderOutlineEvent();
	public static ErrorRenderer error = new ErrorRenderer();
	public ElementRenderer getElementRender(String name)
	{
		return this.renderers.get(name) != null ? this.renderers.get(name) : this.renderers.get(LibElements.ORIGIN);
	}

	public void init() {}
	
	public void preInit() {}

	public void registerHandlers()
	{
		MinecraftForge.EVENT_BUS.register(new RenderEvent());
		MinecraftForge.EVENT_BUS.addListener(event::onRenderWorldLast);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerEntityRenderer);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(event::onModelRegistry);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerItemColors);
		putElementRenderer();
		putEasyRenderer();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBaked);
	}

	public void registerEntityRenderer(FMLClientSetupEvent event)
	{
		RenderingRegistry.registerEntityRenderingHandler(ModEntites.time_manager, TimeManagerRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntites.mana_orb, ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntites.mana_shield, ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntites.mana_star, ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntites.mana_laser, ManaObjectRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntites.mana_eye, ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntites.mana_rift, ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntites.mana_rune, ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntites.mana_sphere, ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntites.radiance_wall, ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntites.chaos_reach, ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntites.thorns_caress, ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntites.silence_squall, ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntites.ascendant_realm, ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntites.mana_power, ManaEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntites.element_orb, ManaObjectRenderer::new);
	}

	public void registerItemColors(ColorHandlerEvent.Item event) {
		IItemColor color = new IItemColor() {
			@Override
			public int getColor(ItemStack stack, int p_getColor_2_) {
				if(NBTTagHelper.hasElement(stack)) {
					float[] color = MagickCore.proxy.getElementRender(NBTTagHelper.getElement(stack)).getColor();
					float[] hsv = Color.RGBtoHSB((int) (color[0] * 255), (int) (color[1] * 255), (int) (color[2] * 255), null);
					return MathHelper.hsvToRGB(hsv[0], hsv[1], hsv[2]);
				}
				return 	16777215;
			}
		};
		event.getItemColors().register(color, ModItems.element_crystal_seeds.get());
		event.getItemColors().register(color, ModItems.element_meat.get());
		event.getItemColors().register(color, ModItems.element_crystal.get());
		event.getItemColors().register(color, ModItems.element_string.get());
		event.getItemColors().register(color, ModItems.element_wool.get());
	}

	public void onModelBaked(ModelBakeEvent event) {
		Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
		ModelResourceLocation location = new ModelResourceLocation(ModItems.magick_container.get().getRegistryName(), "inventory");
		IBakedModel existingModel = modelRegistry.get(location);
		if (existingModel == null) {
			throw new RuntimeException("Did not find magick_container in registry");
		} else {
			MagickBakedModel magickBakedModel = new MagickBakedModel(existingModel);
			event.getModelRegistry().put(location, magickBakedModel);
		}

		location = new ModelResourceLocation(ModItems.magick_crafting.get().getRegistryName(), "inventory");
		existingModel = modelRegistry.get(location);
		if (existingModel == null) {
			throw new RuntimeException("Did not find magick_crafting in registry");
		} else {
			MagickBakedModel magickBakedModel = new MagickBakedModel(existingModel);
			event.getModelRegistry().put(location, magickBakedModel);
		}

		location = new ModelResourceLocation(ModItems.orb_bottle.get().getRegistryName(), "inventory");
		existingModel = modelRegistry.get(location);
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

	private void putEasyRenderer()
	{
		//normal
		RenderEvent.putEasyRender(ManaOrbEntity.class, new ManaOrbRenderer());
		RenderEvent.putEasyRender(DawnWardEntity.class, new DawnWardRenderer());
		RenderEvent.putEasyRender(AllEntity.class, new ElementShieldRenderer());
		RenderEvent.putEasyRender(ManaStarEntity.class, new ManaStarRenderer());
		RenderEvent.putEasyRender(ManaLaserEntity.class, new ManaLaserRenderer());
		RenderEvent.putEasyRender(ManaRiftEntity.class, new ManaRiftRenderer());
		RenderEvent.putEasyRender(ManaRuneEntity.class, new ManaRuneRenderer());
		RenderEvent.putEasyRender(ManaSphereEntity.class, new ManaSphereRenderer());
		RenderEvent.putEasyRender(ManaEyeEntity.class, new ManaEyeRenderer());
		RenderEvent.putEasyRender(RadianceWellEntity.class, new RadianceWellRenderer());
		RenderEvent.putEasyRender(ChaoReachEntity.class, new ChaosReachRenderer());
		RenderEvent.putEasyRender(SilenceSquallEntity.class, new SilenceSqualRenderer());
		RenderEvent.putEasyRender(ThornsCaressEntity.class, new ThornsCaressRenderer());
		RenderEvent.putEasyRender(AscendantRealmEntity.class, new AscendantRealmRenderer());

		//layer
		RenderEvent.putLayerRender(AllEntity.class, new ManaBuffRenderer());
		RenderEvent.putLayerRender(ClientPlayerEntity.class, new PlayerShieldRenderer());

		//outline
		RenderOutlineEvent.putOutlineRender(RadianceWellEntity.class, new ManaRiftOutlineRenderer());

		//laser
		RenderEvent.putLaserRender(RadianceWellEntity.class, new RadianceWellLaserRenderer());
		RenderEvent.putLaserRender(ChaoReachEntity.class, new ChaosReachLaserRenderer());
		RenderEvent.putLaserRender(ThornsCaressEntity.class, new ThornsCaressLaserRenderer());

		//tile
		RenderEvent.putTileRender(MagickCraftingTileEntity.class, new MagickCraftingRenderer());
		RenderEvent.putTileRender(MagickContainerTileEntity.class, new MagickContainerRenderer());
		RenderEvent.putTileRender(ElementCrystalTileEntity.class, new ElementCrystalRenderer());
		RenderEvent.putTileRender(ElementWoolTileEntity.class, new ElementWoolRenderer());
	}

	private void putElementRenderer()
	{
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
		else
			RenderEvent.addMagickParticle(par);
	}

	public static void registerParticleSprite(TextureStitchEvent.Pre event, String name)
	{
		String path = MagickCore.MOD_ID + ":element/" + name;
		if(event.addSprite(new ResourceLocation(path)))
			MagickCore.LOGGER.info("Successes to add sprite [" + name + "]");
		else
			MagickCore.LOGGER.info("Failed to add sprite [" + name + "]");
	}

	public static class ErrorRenderer extends ElementRenderer
	{
		public ErrorRenderer() {
			super(new float[]{1f, 0, 0});
		}
	}
}
