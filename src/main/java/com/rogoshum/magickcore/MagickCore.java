package com.rogoshum.magickcore;

import com.rogoshum.magickcore.client.init.ModKeyBind;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.entity.living.MageVillagerEntity;
import com.rogoshum.magickcore.common.event.EventBus;
import com.rogoshum.magickcore.common.event.magickevent.*;
import com.rogoshum.magickcore.common.init.*;
import com.rogoshum.magickcore.common.integration.AdditionLoader;
import com.rogoshum.magickcore.common.network.Networking;
import com.rogoshum.magickcore.mixin.fabric.registry.MixinSpawnPlacements;
import com.rogoshum.magickcore.proxy.ClientProxy;
import com.rogoshum.magickcore.proxy.CommonProxy;
import com.rogoshum.magickcore.proxy.IProxy;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.levelgen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

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
    public static EventBus EVENT_BUS = new EventBus();

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

    private void setup() {
        ModBrews.registryBrewing();
        MixinSpawnPlacements.add(ModEntities.MAGE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MageVillagerEntity::checkMobSpawnRules);
        //RecipeCollector.init();
        LivingAttributeEvent.registerEntityAttributes();
    }

    private void doClientStuff() {
        proxy.initBlockRenderer();
        ModKeyBind.onKeyboardInput();
    }

    @Override
    public void onInitialize() {
        LOGGER.debug("register RegisterEvent");
        EVENT_BUS.register(new RegisterEvent());
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(RE::onRegiste2r);
        // Register the setup method for modloading
        this.setup();
        callWhenOn(EnvType.CLIENT, () -> () -> proxy = new ClientProxy());
        callWhenOn(EnvType.SERVER, () -> () -> proxy = new CommonProxy());
        callWhenOn(EnvType.CLIENT, () -> this::doClientStuff);
        ModElements.registerElement();
        RegisterEvent.initElementMap();
        proxy.registerHandlers();

        // Register ourselves for server and other game events we are interested in
        EVENT_BUS.register(new MagickLogicEvent());
        EVENT_BUS.register(new ElementFunctionEvent());
        EVENT_BUS.register(new AdvancementsEvent());
        EVENT_BUS.register(new LivingLootsEvent());
        ModDataSerializers.DATA_SERIALIZERS.register();
        ModEntities.Entities.register();
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), MobCategory.CREATURE, ModEntities.MAGE.get(), 10, 1, 1);
        ModBlocks.BLOCKS.register();
        ModEffects.EFFECTS.register();
        ModEffects.POTIONS.register();
        ModItems.ITEMS.register();
        ModSounds.SOUNDS.register();
        ModTileEntities.TILE_ENTITY.register();
        //ModEnchantments.ENCHANTMENTS.register(eventBus);
        ModVillager.init();
        ModBuffs.initBuff();
        ManaMaterials.init();
        ModRegistry.init();
        ModRecipes.init();
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
        modLoader.values().forEach(AdditionLoader::onInitialize);
        //GeckoLib.initialize();
    }

    public static ResourceLocation fromId(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static void callWhenOn(EnvType envType, Supplier<Runnable> supplier) {
        if(envType == FabricLoader.getInstance().getEnvironmentType()) {
            supplier.get().run();
        }
    }
}
