package com.rogoshum.magickcore;

import com.rogoshum.magickcore.client.init.ModKeyBind;
import com.rogoshum.magickcore.client.integration.jei.RecipeCollector;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.living.MageVillagerEntity;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.event.RegisterEvent;
import com.rogoshum.magickcore.common.event.magickevent.ElementFunctionEvent;
import com.rogoshum.magickcore.common.event.magickevent.LivingLootsEvent;
import com.rogoshum.magickcore.common.event.magickevent.MagickLogicEvent;
import com.rogoshum.magickcore.common.init.*;
import com.rogoshum.magickcore.common.integration.AdditionLoader;
import com.rogoshum.magickcore.common.network.Networking;
import com.rogoshum.magickcore.proxy.ClientProxy;
import com.rogoshum.magickcore.proxy.CommonProxy;
import com.rogoshum.magickcore.proxy.IProxy;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

// The value here should match an entry in the META-INF/mods.toml file
public class MagickCore implements ModInitializer {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "magickcore";
	public static final String Data = MOD_ID + ":data";
	public static final UUID emptyUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	
	public static Random rand = new Random();
    public static IProxy proxy;
    public static int tick;
    public HashMap<String, String> modCompatibility = new HashMap<>();
    public static HashMap<String, AdditionLoader> modLoader = new HashMap<>();

    public MagickCore() {
        LOGGER.debug("register RegisterEvent");
        MinecraftForge.EVENT_BUS.register(new RegisterEvent());
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(RE::onRegiste2r);
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::inter);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::generate);
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> proxy = new ClientProxy());
        DistExecutor.unsafeCallWhenOn(Dist.DEDICATED_SERVER, () -> () -> proxy = new CommonProxy());
        ModElements.registerElement();
        RegisterEvent.initElementMap();
        proxy.registerHandlers();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new MagickLogicEvent());
        MinecraftForge.EVENT_BUS.register(new ElementFunctionEvent());
        MinecraftForge.EVENT_BUS.register(new AdvancementsEvent());
        MinecraftForge.EVENT_BUS.register(new LivingLootsEvent());
        MinecraftForge.EVENT_BUS.register(new ModEntities());
        ModDataSerializers.DATA_SERIALIZERS.register();
        ModEntities.Entities.register();
        ModBlocks.BLOCKS.register();
        ModEffects.EFFECTS.register();
        ModEffects.POTIONS.register();
        ModItems.ITEMS.register();
        ModSounds.SOUNDS.register();
        ModTileEntities.TILE_ENTITY.register();
        //ModEnchantments.ENCHANTMENTS.register(eventBus);
        ModVillager.POI_TYPES.register();
        ModVillager.VILLAGER_PROFESSIONS.register();
        ModVillager.SENSOR_TYPES.register();
        ModVillager.init();
        ModBuffs.initBuff();
        ManaMaterials.init();
        ModRegistry.init();
        Networking.registerMessage();
        modCompatibility.put("curios", "com.rogoshum.magickcore.common.integration.curios.CuriosLoader");
        FabricLoader.getInstance().getAllMods().forEach( modInfo -> {
            modCompatibility.forEach( (key, value) -> {
                if(modInfo.getMetadata().getId().equals(key)) {
                    try {
                        modLoader.put(key, Class.forName(value).asSubclass(AdditionLoader.class).newInstance());
                    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
        modLoader.values().forEach(additionLoader -> additionLoader.onLoad(eventBus));
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, com.rogoshum.magickcore.common.init.ModConfig.COMMON_CONFIG);
        //GeckoLib.initialize();
    }

    public static boolean isModLoaded(String modid) {
        return modLoader.containsKey(modid);
    }

    public static float getNegativeToOne() {
        return rand.nextFloat() - rand.nextFloat();
    }

    public static float getRandFloat() {
        return Float.parseFloat(String.format("%.1f",rand.nextFloat()));
    }
    public static void addMagickParticle(LitParticle par) {proxy.addMagickParticle(par);}

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModBrews.registryBrewing();
            EntitySpawnPlacementRegistry.register(ModEntities.MAGE.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MageVillagerEntity::checkMobSpawnRules);
            RecipeCollector.init();
            modLoader.values().forEach(additionLoader -> additionLoader.setup(event));
        });
    }

    private void inter(final InterModEnqueueEvent event) {
        event.enqueueWork(() -> {
            modLoader.values().forEach(additionLoader -> additionLoader.inter(event));
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            proxy.initBlockRenderer();
            ClientRegistry.registerKeyBinding(ModKeyBind.SWAP_KEY);
            modLoader.values().forEach(additionLoader -> additionLoader.doClientStuff(event));
        });
    }

    private void generate(final FMLLoadCompleteEvent event) {
        modLoader.values().forEach(additionLoader -> additionLoader.generate(event));
    }

    @Override
    public void onInitialize() {

    }

    public static ResourceLocation fromId(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
