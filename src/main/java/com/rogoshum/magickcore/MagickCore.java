package com.rogoshum.magickcore;

import com.rogoshum.magickcore.block.tileentity.*;
import com.rogoshum.magickcore.capability.*;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.tileentity.CanSeeTileEntityRenderer;
import com.rogoshum.magickcore.client.tileentity.MagickRepeaterRenderer;
import com.rogoshum.magickcore.event.AdvancementsEvent;
import com.rogoshum.magickcore.event.ElementOrbEvent;
import com.rogoshum.magickcore.event.magickevent.ElementThingEvent;
import com.rogoshum.magickcore.event.magickevent.LivingLootsEvent;
import com.rogoshum.magickcore.event.magickevent.MagickLogicEvent;
import com.rogoshum.magickcore.init.*;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.lifestate.LifeState;
import com.rogoshum.magickcore.network.Networking;
import com.rogoshum.magickcore.proxy.ClientProxy;
import com.rogoshum.magickcore.proxy.CommonProxy;
import com.rogoshum.magickcore.proxy.IProxy;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.UUID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MagickCore.MOD_ID)
public class MagickCore
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "magickcore";
	public static final String NAME = "MagickCore";
	public static final String VERSION = "0.6";
	
	public static final String Data = MOD_ID + ":data";
	public static final UUID emptyUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static final UUID emptyUUID_EYE = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @CapabilityInject(ITakenState.class)
    public static Capability<ITakenState> takenState;

	@CapabilityInject(IEntityState.class)
    public static Capability<IEntityState> entityState;

    @CapabilityInject(IManaData.class)
    public static Capability<IManaData> manaData;

    @CapabilityInject(IManaItemData.class)
    public static Capability<IManaItemData> manaItemData;

    @CapabilityInject(IElementAnimalState.class)
    public static Capability<IElementAnimalState> elementAnimal;

    @CapabilityInject(IElementOnTool.class)
    public static Capability<IElementOnTool> elementOnTool;
	
	public static Random rand = new Random();
    public static IProxy proxy;

    public MagickCore() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> proxy = new ClientProxy());
        DistExecutor.unsafeCallWhenOn(Dist.DEDICATED_SERVER, () -> () -> proxy = new CommonProxy());
        ModElements.registryElement();
        ElementOrbEvent.initElementMap();
        LifeState.init();
        proxy.registerHandlers();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new MagickLogicEvent());
        MinecraftForge.EVENT_BUS.register(new ElementThingEvent());
        MinecraftForge.EVENT_BUS.register(new ElementOrbEvent());
        MinecraftForge.EVENT_BUS.register(new AdvancementsEvent());
        MinecraftForge.EVENT_BUS.register(new LivingLootsEvent());
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(eventBus);
        ModEffects.EFFECTS.register(eventBus);
        ModEffects.POTIONS.register(eventBus);
        ModItems.ITEMS.register(eventBus);
        ModSounds.SOUNDS.register(eventBus);
        ModTileEntities.TILE_ENTITY.register(eventBus);
        ModEnchantments.ENCHANTMENTS.register(eventBus);
        ModBuff.initBuff();
        ManaMaterials.init();
        //GeckoLib.initialize();
    }

    public static float getNegativeToOne()
    {
        return rand.nextFloat() - rand.nextFloat();
    }
    public static void addMagickParticle(LitParticle par) {proxy.addMagickParticle(par);}

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        event.enqueueWork(() -> {
            CapabilityManager.INSTANCE.register(IEntityState.class, new CapabilityEntityState.Storage<>(), () -> new CapabilityEntityState.Implementation(ModElements.getElement(LibElements.ORIGIN)));
            CapabilityManager.INSTANCE.register(IManaData.class, new CapabilityManaData.Storage<>(), () -> new CapabilityManaData.Implementation(ModElements.getElement(LibElements.ORIGIN)));
            CapabilityManager.INSTANCE.register(IManaItemData.class, new CapabilityManaItemData.Storage<>(), () -> new CapabilityManaItemData.Implementation(ModElements.getElement(LibElements.ORIGIN)));
            CapabilityManager.INSTANCE.register(IElementAnimalState.class, new CapabilityElementAnimalState.Storage<>(), () -> new CapabilityElementAnimalState.Implementation(ModElements.getElement(LibElements.ORIGIN)));
            CapabilityManager.INSTANCE.register(IElementOnTool.class, new CapabilityElementOnTool.Storage<>(), CapabilityElementOnTool.Implementation::new);
            CapabilityManager.INSTANCE.register(ITakenState.class, new CapabilityTakenEntity.Storage<>(), CapabilityTakenEntity.Implementation::new);
            Networking.registerMessage();
            ModBrew.registryBrewing();
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.magick_crafting_tileentity.get(), (CanSeeTileEntityRenderer<MagickCraftingTileEntity>::new));
        RenderTypeLookup.setRenderLayer(ModBlocks.magick_crafting.get(), RenderType.getCutout());
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
}
