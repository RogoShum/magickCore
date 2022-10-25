package com.rogoshum.magickcore;

import com.rogoshum.magickcore.block.tileentity.*;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.tileentity.CanSeeTileEntityRenderer;
import com.rogoshum.magickcore.client.tileentity.MagickCraftingRenderer;
import com.rogoshum.magickcore.client.tileentity.MagickRepeaterRenderer;
import com.rogoshum.magickcore.client.tileentity.SpiritCrystalRenderer;
import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.event.AdvancementsEvent;
import com.rogoshum.magickcore.event.ElementOrbEvent;
import com.rogoshum.magickcore.event.RegisterEvent;
import com.rogoshum.magickcore.event.magickevent.ElementThingEvent;
import com.rogoshum.magickcore.event.magickevent.LivingLootsEvent;
import com.rogoshum.magickcore.event.magickevent.MagickLogicEvent;
import com.rogoshum.magickcore.init.*;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.lib.LibConditions;
import com.rogoshum.magickcore.lib.LibRegistry;
import com.rogoshum.magickcore.magick.MagickElement;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.ability.*;
import com.rogoshum.magickcore.magick.condition.*;
import com.rogoshum.magickcore.magick.context.child.*;
import com.rogoshum.magickcore.magick.lifestate.LifeState;
import com.rogoshum.magickcore.network.Networking;
import com.rogoshum.magickcore.proxy.ClientProxy;
import com.rogoshum.magickcore.proxy.CommonProxy;
import com.rogoshum.magickcore.proxy.IProxy;
import com.rogoshum.magickcore.registry.MagickRegistry;
import com.rogoshum.magickcore.registry.ObjectRegistry;
import com.rogoshum.magickcore.registry.elementmap.ElementFunctions;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.network.ForgeConnectionNetworkFilter;
import net.minecraftforge.network.NetworkFilters;
import net.minecraftforge.network.VanillaPacketFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MagickCore.MOD_ID)
public class MagickCore {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "magickcore";
	public static final String NAME = "MagickCore";
	public static final String VERSION = "0.6";
	
	public static final String Data = MOD_ID + ":data";
	public static final UUID emptyUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static final UUID emptyUUID_EYE = UUID.fromString("00000000-0000-0000-0000-000000000001");
	
	public static Random rand = new Random();
    public static IProxy proxy;
    public static int tick;

    public MagickCore() {
        LOGGER.debug("register RegisterEvent");
        MinecraftForge.EVENT_BUS.register(new RegisterEvent());
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(RE::onRegiste2r);
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> proxy = new ClientProxy());
        DistExecutor.unsafeCallWhenOn(Dist.DEDICATED_SERVER, () -> () -> proxy = new CommonProxy());
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> proxy = new ClientProxy());
        ModElements.registerElement();
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
        MinecraftForge.EVENT_BUS.register(new ModEntities());
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModDataSerializers.DATA_SERIALIZERS.register(eventBus);
        ModEntities.Entities.register(eventBus);
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

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        event.enqueueWork(() -> {
            Networking.registerMessage();
            ModBrew.registryBrewing();

            MagickRegistry.<MagickElement>getRegistry(LibRegistry.ELEMENT).registry().forEach((type, o) -> {
                ElementFunctions functions = MagickRegistry.<ElementFunctions>getRegistry(LibRegistry.ELEMENT_FUNCTION).registry().get(type);
                functions.add(EnumApplyType.SPAWN_ENTITY, MagickReleaseHelper::spawnEntity);
                switch (type) {
                    case LibElements.ORIGIN:
                        functions.add(EnumApplyType.ATTACK, OriginElement::damageEntity);
                        break;
                    case LibElements.ARC:
                        functions.add(EnumApplyType.ATTACK, ArcAbility::damageEntity);
                        functions.add(EnumApplyType.BUFF, ArcAbility::applyBuff);
                        functions.add(EnumApplyType.HIT_ENTITY, ArcAbility::hitEntity);
                        functions.add(EnumApplyType.DE_BUFF, ArcAbility::applyDebuff);
                        functions.add(EnumApplyType.HIT_BLOCK, ArcAbility::hitBlock);
                        functions.add(EnumApplyType.ELEMENT_TOOL, ArcAbility::applyToolElement);
                        break;
                    case LibElements.SOLAR:
                        functions.add(EnumApplyType.ATTACK, SolarAbility::damageEntity);
                        functions.add(EnumApplyType.BUFF, SolarAbility::applyBuff);
                        functions.add(EnumApplyType.HIT_ENTITY, SolarAbility::hitEntity);
                        functions.add(EnumApplyType.DE_BUFF, SolarAbility::applyDebuff);
                        functions.add(EnumApplyType.HIT_BLOCK, SolarAbility::hitBlock);
                        break;
                    case LibElements.VOID:
                        functions.add(EnumApplyType.ATTACK, VoidAbility::damageEntity);
                        functions.add(EnumApplyType.BUFF, VoidAbility::applyBuff);
                        functions.add(EnumApplyType.HIT_ENTITY, VoidAbility::hitEntity);
                        functions.add(EnumApplyType.DE_BUFF, VoidAbility::applyDebuff);
                        functions.add(EnumApplyType.HIT_BLOCK, VoidAbility::hitBlock);
                        functions.add(EnumApplyType.ELEMENT_TOOL, VoidAbility::applyToolElement);
                        break;
                    case LibElements.STASIS:
                        functions.add(EnumApplyType.ATTACK, StasisAbility::damageEntity);
                        functions.add(EnumApplyType.BUFF, StasisAbility::applyBuff);
                        functions.add(EnumApplyType.HIT_ENTITY, StasisAbility::hitEntity);
                        functions.add(EnumApplyType.DE_BUFF, StasisAbility::applyDebuff);
                        functions.add(EnumApplyType.HIT_BLOCK, StasisAbility::hitBlock);
                        functions.add(EnumApplyType.ELEMENT_TOOL, StasisAbility::applyToolElement);
                        break;
                    case LibElements.TAKEN:
                        functions.add(EnumApplyType.ATTACK, TakenAbility::damageEntity);
                        functions.add(EnumApplyType.BUFF, TakenAbility::applyBuff);
                        functions.add(EnumApplyType.HIT_ENTITY, TakenAbility::hitEntity);
                        functions.add(EnumApplyType.DE_BUFF, TakenAbility::applyDebuff);
                        functions.add(EnumApplyType.HIT_BLOCK, TakenAbility::hitBlock);
                        break;
                    case LibElements.WITHER:
                        functions.add(EnumApplyType.ATTACK, WitherAbility::damageEntity);
                        functions.add(EnumApplyType.BUFF, WitherAbility::applyBuff);
                        functions.add(EnumApplyType.HIT_ENTITY, WitherAbility::hitEntity);
                        functions.add(EnumApplyType.DE_BUFF, WitherAbility::applyDebuff);
                        functions.add(EnumApplyType.HIT_BLOCK, WitherAbility::hitBlock);
                        functions.add(EnumApplyType.ELEMENT_TOOL, WitherAbility::applyToolElement);
                    case LibElements.AIR:
                        functions.add(EnumApplyType.ATTACK, AirAbility::damageEntity);
                        functions.add(EnumApplyType.BUFF, AirAbility::applyBuff);
                        functions.add(EnumApplyType.HIT_ENTITY, AirAbility::hitEntity);
                        functions.add(EnumApplyType.DE_BUFF, AirAbility::applyDebuff);
                        functions.add(EnumApplyType.HIT_BLOCK, AirAbility::hitBlock);
                        functions.add(EnumApplyType.ELEMENT_TOOL, AirAbility::applyToolElement);
                        break;
                }
            });

            ObjectRegistry<Callable<ChildContext>> childContexts = new ObjectRegistry<>(LibRegistry.CHILD_CONTEXT);
            childContexts.register(LibContext.SPAWN, SpawnContext::new);
            childContexts.register(LibContext.TRACE, TraceContext::new);
            childContexts.register(LibContext.POSITION, PositionContext::new);
            childContexts.register(LibContext.ITEM, ItemContext::new);
            childContexts.register(LibContext.CONDITION, ConditionContext::new);
            childContexts.register(LibContext.DIRECTION, DirectionContext::new);

            ObjectRegistry<Callable<Condition>> conditions = new ObjectRegistry<>(LibRegistry.CONDITION);
            conditions.register(LibConditions.ALWAYS, AlwaysCondition::new);
            conditions.register(LibConditions.ENTITY_TYPE, EntityTypeCondition::new);
            conditions.register(LibConditions.INJURABLE, InjurableEntityCondition::new);
            conditions.register(LibConditions.LIVING_ENTITY, LivingEntityCondition::new);
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.magick_crafting_tileentity.get(), MagickCraftingRenderer::new);
        RenderTypeLookup.setRenderLayer(ModBlocks.magick_crafting.get(), RenderType.getCutout());
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.spirit_crystal_tileentity.get(), SpiritCrystalRenderer::new);
        RenderTypeLookup.setRenderLayer(ModBlocks.spirit_crystal.get(), RenderType.getCutout());
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
