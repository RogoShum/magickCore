package com.rogoshum.magickcore;

import com.rogoshum.magickcore.block.tileentity.*;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.client.tileentity.*;
import com.rogoshum.magickcore.entity.living.MageVillagerEntity;
import com.rogoshum.magickcore.enums.ApplyType;
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
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.DepthAverageConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.registries.ForgeRegistries;
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
        ModVillager.POI_TYPES.register(eventBus);
        ModVillager.VILLAGER_PROFESSIONS.register(eventBus);
        ModVillager.SENSOR_TYPES.register(eventBus);
        ModVillager.init();
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
        event.enqueueWork(() -> {
            Networking.registerMessage();
            ModBrew.registryBrewing();
            EntitySpawnPlacementRegistry.register(ModEntities.MAGE.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MageVillagerEntity::canSpawnOn);
            ModRegistry.init();
            proxy.initBlockRenderer();
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) {

    }
}
