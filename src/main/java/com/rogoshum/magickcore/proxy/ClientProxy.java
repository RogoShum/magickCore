package com.rogoshum.magickcore.proxy;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.render.IEasyRender;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.entity.render.*;
import com.rogoshum.magickcore.client.gui.ManaBuffHUD;
import com.rogoshum.magickcore.client.item.*;
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
import com.rogoshum.magickcore.client.tileentity.ElementWoolRenderer;
import com.rogoshum.magickcore.client.tileentity.ItemExtractorRenderer;
import com.rogoshum.magickcore.common.init.*;
import com.rogoshum.magickcore.common.item.tool.WandItem;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.registry.ObjectRegistry;
import com.rogoshum.magickcore.common.registry.elementmap.EntityRenderers;
import com.rogoshum.magickcore.common.registry.elementmap.RenderFunctions;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.lib.LibElements;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ClientProxy implements IProxy {
	private final HashMap<String, ElementRenderer> renderers = new HashMap<>();
	public final ShaderEvent event = new ShaderEvent();
	private static final HashMap<Item, EasyItemRenderer> customItemRenderer = new HashMap<>();
	private TaskThread magickThread;
	private RenderThread renderThread;

	public ElementRenderer getElementRender(String name) {
		return this.renderers.get(name) != null ? this.renderers.get(name) : this.renderers.get(LibElements.ORIGIN);
	}

	public static void registerItemRenderer(Item item, EasyItemRenderer renderer) {
		customItemRenderer.put(item, renderer);
	}

	public static EasyItemRenderer getItemRenderer(Item item) {
		return customItemRenderer.get(item);
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
	public void setClippingHelper(Frustum clippingHelper) {
		checkRenderer();
		renderThread.setFrustum(clippingHelper);
	}

	@Override
	public void tick(EnvType side) {
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
		MagickCore.EVENT_BUS.register(new RenderEvent());
		MagickCore.EVENT_BUS.register(new LightShaderManager());
		event.onModelRegistry();
		MagickCore.EVENT_BUS.register(event);
		this.registerItemColors(ColorProviderRegistry.ITEM);
		putElementRenderer();
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBaked);
		registerItemRenderer(ModItems.MATERIAL_JAR.get(), new MaterialJarItemRenderer());
		registerItemRenderer(ModItems.ITEM_EXTRACTOR.get(), new com.rogoshum.magickcore.client.item.ItemExtractorRenderer());
		registerItemRenderer(ModItems.SPIRIT_WOOD_STICK.get(), new SpiritWoodStickRenderer());
		registerItemRenderer(ModItems.STAFF.get(), new StaffRenderer());
		registerItemRenderer(ModItems.SPIRIT_CRYSTAL_STAFF.get(), new StaffRenderer());
		registerItemRenderer(ModItems.SPIRIT_BOW.get(), new SpiritBowRenderer());
		registerItemRenderer(ModItems.SPIRIT_SWORD.get(), new SpiritSwordRenderer());
		registerItemRenderer(ModItems.ELEMENT_WOOL.get(), new com.rogoshum.magickcore.client.item.ElementWoolRenderer());
		registerItemRenderer(ModItems.MANA_ENERGY.get(), new ManaEnergyRenderer());
		registerItemRenderer(ModItems.SPIRIT_CRYSTAL.get(), new SpiritCrystalItemRenderer());
		registerItemRenderer(ModItems.CONTEXT_CORE.get(), new ManaEnergyRenderer());
		registerItemRenderer(ModItems.MAGICK_CORE.get(), new ManaEnergyRenderer());
		registerItemRenderer(ModItems.ORB_BOTTLE.get(), new OrbBottleRenderer());
		registerItemRenderer(ModItems.CONTEXT_POINTER.get(), new ContextPointerRenderer());
		registerItemRenderer(ModItems.MAGICK_CONTAINER.get(), new ManaCapacityRenderer());
		registerItemRenderer(ModItems.POTION_TYPE.get(), new ManaEnergyRenderer());
		registerItemRenderer(ModItems.ENTITY_TYPE.get(), new ManaEnergyRenderer());
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
	}

	public void registerItemColors(ColorProviderRegistry<ItemLike, ItemColor> event) {
		ItemColor color = (stack, p_getColor_2_) -> {
			if(NBTTagHelper.hasElement(stack)) {
				return MagickCore.proxy.getElementRender(NBTTagHelper.getElement(stack)).getColor().getDecimalColor();
			}
			return 	16777215;
		};
		event.register(color, ModItems.ELEMENT_CRYSTAL_SEEDS.get());
		event.register(color, ModItems.ELEMENT_MEAT.get());
		event.register(color, ModItems.ELEMENT_CRYSTAL.get());
		event.register(color, ModItems.ELEMENT_STRING.get());
		event.register(color, ModItems.ELEMENT_WOOL.get());
		color = (stack, p_getColor_2_) -> {
			CompoundTag tag = stack.getOrCreateTag();
			if(tag.contains(WandItem.SET_KEY) && !tag.getCompound(WandItem.SET_KEY).isEmpty())
				return RenderHelper.getRGB().getDecimalColor();
			return 	16777215;
		};
		event.register(color, ModItems.WAND.get());
	}

	public void putElementRendererIn(String name, ElementRenderer renderer) {
		renderers.put(name, renderer);
	}

	public void initBlockRenderer() {
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MAGICK_CRAFTING.get(), RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SPIRIT_CRYSTAL.get(), RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MATERIAL_JAR.get(), RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ELEMENT_CRYSTAL.get(), RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ITEM_EXTRACTOR.get(), RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ELEMENT_WOOL.get(), RenderType.solid());
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
