package com.rogoshum.magickcore;

import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.entity.living.MageVillagerEntity;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.event.RegisterEvent;
import com.rogoshum.magickcore.common.event.magickevent.ElementThingEvent;
import com.rogoshum.magickcore.common.event.magickevent.LivingLootsEvent;
import com.rogoshum.magickcore.common.event.magickevent.MagickLogicEvent;
import com.rogoshum.magickcore.common.init.*;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.magick.lifestate.LifeState;
import com.rogoshum.magickcore.common.network.Networking;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.util.GenerationUtil;
import com.rogoshum.magickcore.proxy.ClientProxy;
import com.rogoshum.magickcore.proxy.CommonProxy;
import com.rogoshum.magickcore.proxy.IProxy;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MagickCore.MOD_ID)
public class MagickCore {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "magickcore";
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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::generate);
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> proxy = new ClientProxy());
        DistExecutor.unsafeCallWhenOn(Dist.DEDICATED_SERVER, () -> () -> proxy = new CommonProxy());
        ModElements.registerElement();
        RegisterEvent.initElementMap();
        LifeState.init();
        proxy.registerHandlers();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new MagickLogicEvent());
        MinecraftForge.EVENT_BUS.register(new ElementThingEvent());
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
        //ModEnchantments.ENCHANTMENTS.register(eventBus);
        ModVillager.POI_TYPES.register(eventBus);
        ModVillager.VILLAGER_PROFESSIONS.register(eventBus);
        ModVillager.SENSOR_TYPES.register(eventBus);
        ModVillager.init();
        ModBuff.initBuff();
        ManaMaterials.init();
        ModRegistry.init();
        Networking.registerMessage();
        //GeckoLib.initialize();
    }

    public static float getNegativeToOne()
    {
        return rand.nextFloat() - rand.nextFloat();
    }
    public static void addMagickParticle(LitParticle par) {proxy.addMagickParticle(par);}

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModBrew.registryBrewing();
            EntitySpawnPlacementRegistry.register(ModEntities.MAGE.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MageVillagerEntity::canSpawnOn);
            ModRecipes.registerExplosionRecipes();
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            proxy.initBlockRenderer();
        });
    }

    private void generate(final FMLLoadCompleteEvent event) {
        /*
        new ArrayList<>(ModEntities.Entities.getEntries()).forEach(ob -> {
            if(ob.isPresent())
                GenerationUtil.generateEntityType(ob.get());
        });
        MagickRegistry.getRegistry(LibRegistry.ELEMENT).registry().keySet().forEach(elementType -> {
            for (int i = 0; i < ApplyType.values().length; ++i) {
                ApplyType type = ApplyType.values()[i];
                if(type != ApplyType.SPAWN_ENTITY type != ApplyType.NONE && type != ApplyType.HIT_ENTITY && type != ApplyType.HIT_BLOCK && type != ApplyType.SUPER && type != ApplyType.ELEMENT_TOOL)
                    GenerationUtil.generateElementFuncFile(elementType, ApplyType.values()[i].getLabel());
            }
        });

         */
    }
}
