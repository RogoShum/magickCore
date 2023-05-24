package com.rogoshum.magickcore.proxy;

import com.mojang.math.Vector4f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.extradata.item.ItemDimensionData;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.api.render.ElementRenderer;
import com.rogoshum.magickcore.api.render.easyrender.IEasyRender;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.render.*;
import com.rogoshum.magickcore.client.gui.ManaBuffHUD;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.client.render.RenderThread;
import com.rogoshum.magickcore.client.element.*;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.tileentity.*;
import com.rogoshum.magickcore.client.event.RenderEvent;
import com.rogoshum.magickcore.client.event.ShaderEvent;
import com.rogoshum.magickcore.common.init.*;
import com.rogoshum.magickcore.common.item.material.ElementItem;
import com.rogoshum.magickcore.common.item.tool.WandItem;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.api.registry.ObjectRegistry;
import com.rogoshum.magickcore.api.registry.elementmap.EntityRenderers;
import com.rogoshum.magickcore.api.registry.elementmap.RenderFunctions;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.lib.LibElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.resource.ResourcePackLoader;

import javax.annotation.Nullable;
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
		if(renderThread == null || renderThread.isInterrupted()) {
			if(renderThread != null)
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
	public Queue<Consumer<RenderParams>> getOriginalGlFunction() {
		checkRenderer();
		return renderThread.getOriginalGlFunction();
	}

	@Override
	public HashMap<RenderMode, Queue<Consumer<RenderParams>>> getSolidGlFunction() {
		checkRenderer();
		return renderThread.getSolidGlFunction();
	}

	@Override
	public HashMap<RenderMode, Queue<Consumer<RenderParams>>> getGlFunction() {
		checkRenderer();
		return renderThread.getGlFunction();
	}

	@Override
	public HashMap<RenderMode, Queue<Consumer<RenderParams>>> getShaderGlFunction() {
		checkRenderer();
		return renderThread.getShaderGlFunction();
	}

	@Override
	public Queue<Vector4f> getColorLightFunction() {
		checkRenderer();
		return renderThread.getLight();
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
	public void setClippingHelper(Frustum clippingHelper) {
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
	public void resetTask() {
		if(magickThread != null) {
			magickThread.interrupt();
			magickThread = null;
		}
		if(renderThread != null) {
			renderThread.interrupt();
			renderThread = null;
		}
	}

	@Override
	public void addAdditionTask(Runnable tryTask, Runnable catchTask) {
		checkThread();
		magickThread.setAdditionTask(tryTask);
		magickThread.setAdditionCatch(catchTask);
	}

	public void checkThread() {
		if(magickThread == null || magickThread.isInterrupted()) {
			if(magickThread != null) {
				MagickCore.LOGGER.info("Task Thread Interrupted");
				if(magickThread.getState().equals(Thread.State.BLOCKED))
					MagickCore.LOGGER.info("Task Thread BLOCKED");
				if(!magickThread.isAlive())
					MagickCore.LOGGER.info("Task Thread Dead");
				if(magickThread.getState().equals(Thread.State.WAITING))
					MagickCore.LOGGER.info("Task Thread Waiting");
			}
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
		MinecraftForge.EVENT_BUS.addListener(event::onSetupShaders);
		MinecraftForge.EVENT_BUS.addListener(event::onRenderShaders);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerEntityRenderer);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(event::onModelRegistry);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelRegistry);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBakeEvent);
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
		ManaBuffHUD.addBuffTexture(LibBuff.CRIPPLE, new ResourceLocation(MagickCore.MOD_ID, "textures/mob_effect/cripple.png"));
		ManaBuffHUD.addBuffTexture(LibBuff.DECAY, new ResourceLocation(MagickCore.MOD_ID, "textures/mob_effect/decay.png"));
		ManaBuffHUD.addBuffTexture(LibBuff.FRAGILE, new ResourceLocation(MagickCore.MOD_ID, "textures/mob_effect/fragile.png"));
		ManaBuffHUD.addBuffTexture(LibBuff.FREEZE, new ResourceLocation(MagickCore.MOD_ID, "textures/mob_effect/freeze.png"));
		ManaBuffHUD.addBuffTexture(LibBuff.LIGHT, new ResourceLocation(MagickCore.MOD_ID, "textures/mob_effect/light.png"));
		ManaBuffHUD.addBuffTexture(LibBuff.PARALYSIS, new ResourceLocation(MagickCore.MOD_ID, "textures/mob_effect/paralysis.png"));
		ManaBuffHUD.addBuffTexture(LibBuff.PURE, new ResourceLocation(MagickCore.MOD_ID, "textures/mob_effect/pure.png"));
		ManaBuffHUD.addBuffTexture(LibBuff.TAKEN, new ResourceLocation(MagickCore.MOD_ID, "textures/mob_effect/taken.png"));
		ManaBuffHUD.addBuffTexture(LibBuff.TAKEN_KING, new ResourceLocation(MagickCore.MOD_ID, "textures/mob_effect/taken_king.png"));
		ManaBuffHUD.addBuffTexture(LibBuff.WEAKEN, new ResourceLocation(MagickCore.MOD_ID, "textures/mob_effect/weaken.png"));
		ManaBuffHUD.addBuffTexture(LibBuff.SLOW, new ResourceLocation("textures/mob_effect/slowness.png"));
		ManaBuffHUD.addBuffTexture(LibBuff.RADIANCE_WELL, new ResourceLocation(MagickCore.MOD_ID, "textures/mob_effect/radiance_well.png"));
		ManaBuffHUD.addBuffTexture(LibBuff.STASIS, new ResourceLocation(MagickCore.MOD_ID, "textures/items/stasis.png"));
		ManaBuffHUD.addBuffTexture(LibBuff.BOTAN, new ResourceLocation(MagickCore.MOD_ID, "textures/mob_effect/botan.png"));
		ManaBuffHUD.addBuffTexture(LibBuff.THORNS, new ResourceLocation(MagickCore.MOD_ID, "textures/mob_effect/thorns.png"));
		RenderHelper.addItemModelResource("staff", MagickCore.fromId("staff_layer"));
		RenderHelper.addItemModelResource("staff_crystal", MagickCore.fromId("staff_crystal_layer"));
		RenderHelper.addItemModelResource("sword", MagickCore.fromId("sword_layer"));
		RenderHelper.addItemModelResource("bow", MagickCore.fromId("bow_layer"));
		RenderHelper.addItemModelResource("mana_energy", MagickCore.fromId("mana_energy_layer"));
	}

	public void registerEntityRenderer(FMLClientSetupEvent event) {
		//net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntites.time_manager, TimeManagerRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.MANA_ORB.get(), ManaProjectileRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.MANA_SHIELD.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.MANA_STAR.get(), ManaProjectileRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.MANA_LASER.get(), ManaProjectileRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.MANA_SPHERE.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.RADIANCE_WALL.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.CHAOS_REACH.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.THORNS_CARESS.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.SILENCE_SQUALL.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.ASCENDANT_REALM.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.ELEMENT_ORB.get(), ManaProjectileRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.CONTEXT_CREATOR.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.MANA_CAPACITY.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.CONTEXT_POINTER.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.RAY_TRACE.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.ENTITY_CAPTURE.get(), EntityHunterRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.CONE.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.SECTOR.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.SPHERE.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.SQUARE.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.RAY.get(), ManaProjectileRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.BLOOD_BUBBLE.get(), ManaProjectileRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.LAMP.get(), ManaProjectileRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.ARROW.get(), ManaProjectileRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.BUBBLE.get(), ManaProjectileRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.LEAF.get(), ManaProjectileRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.RED_STONE.get(), ManaProjectileRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.SHADOW.get(), ManaProjectileRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.WIND.get(), ManaProjectileRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.JEWELRY_BAG.get(), ManaProjectileRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.REPEATER.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.GRAVITY_LIFT.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.PLACEABLE_ENTITY.get(), PlaceableItemEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.MAGE.get(), MageRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.PHANTOM.get(), ManaProjectileRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.ARTIFICIAL_LIFE.get(), ArtificialLifeEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.CHAIN.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.SPIN.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.CHARGE.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.MULTI_RELEASE.get(), ManaEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.QUADRANT_CRYSTAL.get(), ManaLivingEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.LIVING_ARGENT.get(), ManaLivingEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.INTERACTIVE_ITEM.get(), InteractiveItemEntityRenderer::new);
		net.minecraft.client.renderer.entity.EntityRenderers.register(ModEntities.LIGHT.get(), EntityEmptyRenderer::new);

	}

	public float manaEnergy(ItemStack p_174676_, @Nullable ClientLevel p_174677_, @Nullable LivingEntity p_174678_, int p_174679_) {
		float result = 0;
		ItemManaData data = ExtraDataUtil.itemManaData(p_174676_, 0);
		if(data.contextCore().haveMagickContext())
			result += 1;
		if(data.spellContext().tick()>0)
			result += 2;
		if(data.spellContext().force()>0)
			result += 4;
		if(data.spellContext().range()>0)
			result += 8;
		return result;
	}

	public float crystalBox(ItemStack p_174676_, @Nullable ClientLevel p_174677_, @Nullable LivingEntity p_174678_, int p_174679_) {
		float result = 0;
		ItemDimensionData data = ExtraDataUtil.itemDimensionData(p_174676_);
		for(ItemStack slot : data.getSlots()) {
			if(slot.getItem() instanceof ElementItem)
				result+=1;
		}
		return result;
	}

	public void onModelRegistry(ModelRegistryEvent event) {
		ResourceLocation res = new ResourceLocation("mana_energy_layer");
		ItemProperties.register(ModItems.SPIRIT_SWORD.get(), res, this::manaEnergy);
		ItemProperties.register(ModItems.SPIRIT_BOW.get(), res, this::manaEnergy);
		ItemProperties.register(ModItems.SPIRIT_CRYSTAL_STAFF.get(), res, this::manaEnergy);
		ItemProperties.register(ModItems.STAFF.get(), res, this::manaEnergy);
		ItemProperties.register(ModItems.CRYSTAL_BOX.get(), new ResourceLocation("crystal_box"), this::crystalBox);
	}

	public void onModelBakeEvent(ModelBakeEvent evt) {

	}

	public void registerItemColors(ColorHandlerEvent.Item event) {
		ItemColor color = (stack, p_getColor_2_) -> {
			if(NBTTagHelper.hasElement(stack)) {
				return MagickCore.proxy.getElementRender(NBTTagHelper.getElement(stack)).getPrimaryColor().decimalColor();
			}
			return 	16777215;
		};
		event.getItemColors().register(color, ModItems.ELEMENT_CRYSTAL_SEEDS.get());
		event.getItemColors().register(color, ModItems.ELEMENT_MEAT.get());
		event.getItemColors().register(color, ModItems.ELEMENT_CRYSTAL.get());
		event.getItemColors().register(color, ModItems.ELEMENT_STRING.get());
		event.getItemColors().register(color, ModItems.ELEMENT_WOOL.get());
		event.getItemColors().register(color, ModItems.QUADRANT_FRAGMENTS.get());
		color = (stack, p_getColor_2_) -> {
			CompoundTag tag = stack.getOrCreateTag();
			if(tag.contains(WandItem.SET_KEY) && !tag.getCompound(WandItem.SET_KEY).isEmpty())
				return RenderHelper.getRGB().decimalColor();
			return 	16777215;
		};
		event.getItemColors().register(color, ModItems.WAND.get());

		color = (stack, p_getColor_2_) -> {
			if(p_getColor_2_ > 0) {
				List<ItemStack> slots = ExtraDataUtil.itemDimensionData(stack).getSlots();
				if(slots.size() > p_getColor_2_-1) {
					int count = 0;
					for(ItemStack slot : slots) {
						if(slot.getItem() instanceof ElementItem)
							count++;
						if(p_getColor_2_ == count)
							return MagickCore.proxy.getElementRender(((ElementItem) slot.getItem()).getElementType()).getPrimaryColor().decimalColor();
					}
				}
			}
			return 	16777215;
		};
		event.getItemColors().register(color, ModItems.CRYSTAL_BOX.get());
		color = (stack, p_getColor_2_) -> {
			if(p_getColor_2_ == 1) {
				return MagickCore.proxy.getElementRender(NBTTagHelper.getElement(stack)).getPrimaryColor().decimalColor();
			}
			return 	16777215;
		};
		event.getItemColors().register(color, ModItems.ASSEMBLY_ESSENCE.get());
		event.getItemColors().register((stack, index) -> index > 0 ? -1 : ((DyeableLeatherItem)stack.getItem()).getColor(stack), ModItems.COLORED_GLOW_DUST.get());
	}

	public void putElementRendererIn(String name, ElementRenderer renderer) {
		renderers.put(name, renderer);
	}

	public void initBlockRenderer() {
		Minecraft.getInstance().textureManager.register(LitParticle.TEXTURE_ATLAS.location(), LitParticle.TEXTURE_ATLAS);
		Minecraft.getInstance().textureManager.register(RenderHelper.TEXTURE_ATLAS.location(), RenderHelper.TEXTURE_ATLAS);
		Collection<ResourceLocation> res =  ResourcePackLoader.getPackFor(MagickCore.MOD_ID).get().getResources(PackType.CLIENT_RESOURCES, MagickCore.MOD_ID, "textures", Integer.MAX_VALUE, (s) -> s.endsWith(".png"));
		RenderHelper.addTexture(res);
		RenderHelper.addTexture(new ResourceLocation("textures/block/white_wool.png"));
		RenderHelper.addTexture(new ResourceLocation("textures/item/ender_eye.png"));
		res =  ResourcePackLoader.getPackFor(MagickCore.MOD_ID).get().getResources(PackType.CLIENT_RESOURCES, MagickCore.MOD_ID, "textures/element", Integer.MAX_VALUE, (s) -> s.endsWith(".png"));
		LitParticle.addTexture(res);
		res =  ResourcePackLoader.getPackFor(MagickCore.MOD_ID).get().getResources(PackType.CLIENT_RESOURCES, MagickCore.MOD_ID, "textures/particle", Integer.MAX_VALUE, (s) -> s.endsWith(".png"));
		LitParticle.addTexture(res);
		//BlockEntityRenderers.register(ModTileEntities.MAGICK_CRAFTING_TILE_ENTITY.get(), com.rogoshum.magickcore.client.tileentity.MagickCraftingRenderer::new);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.MAGICK_CRAFTING.get(), RenderType.cutout());
		BlockEntityRenderers.register(ModTileEntities.SPIRIT_CRYSTAL_TILE_ENTITY.get(), SpiritCrystalRenderer::new);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.SPIRIT_CRYSTAL.get(), RenderType.cutout());
		BlockEntityRenderers.register(ModTileEntities.MATERIAL_JAR_TILE_ENTITY.get(), MaterialJarRenderer::new);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.MATERIAL_JAR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.RADIANCE_CRYSTAL.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.DIMENSION_INFLATE.get(), RenderType.solid());
		BlockEntityRenderers.register(ModTileEntities.DIMENSION_INFLATE_TILE_ENTITY.get(), DimensionInflateRenderer::new);
		//BlockEntityRenderers.register(ModTileEntities.ELEMENT_CRYSTAL_TILE_ENTITY.get(), ElementCrystalRenderer::new);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.ELEMENT_CRYSTAL.get(), RenderType.cutout());
		BlockEntityRenderers.register(ModTileEntities.ITEM_EXTRACTOR_TILE_ENTITY.get(), ItemExtractorRenderer::new);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.ITEM_EXTRACTOR.get(), RenderType.cutout());
		//BlockEntityRenderers.register(ModTileEntities.ELEMENT_WOOL_TILE_ENTITY.get(), ElementWoolRenderer::new);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.ELEMENT_WOOL.get(), RenderType.solid());
	}

	private void putElementRenderer() {
		putElementRendererIn(LibElements.ORIGIN, new OriginRenderer());

		putElementRendererIn(LibElements.SOLAR, new SolarRenderer());
		putElementRendererIn(LibElements.VOID, new VoidRenderer());
		putElementRendererIn(LibElements.ARC, new ArcRenderer());

		putElementRendererIn(LibElements.STASIS, new StasisRenderer());
		putElementRendererIn(LibElements.WITHER, new WitherRenderer());
		putElementRendererIn(LibElements.TAKEN, new TakenRenderer());

		putElementRendererIn(LibElements.PSI, new PsiRenderer());
		putElementRendererIn(LibElements.BOTANIA, new BotaniaRenderer());
	}

	public void addMagickParticle(LitParticle par) {
		Vec3 vec = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

		if(!RenderHelper.isInRangeToRender3d(par, vec.x, vec.y, vec.z))
			return;

		if(Minecraft.getInstance().options.particles == ParticleStatus.DECREASED) {
			if(MagickCore.rand.nextInt(3) > 0)
				RenderEvent.addMagickParticle(par);
		}
		else if(Minecraft.getInstance().options.particles == ParticleStatus.MINIMAL) {
			if(MagickCore.rand.nextInt(4) == 0)
				RenderEvent.addMagickParticle(par);
		}
		else {
			RenderEvent.addMagickParticle(par);
		}
	}
}
