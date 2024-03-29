package com.rogoshum.magickcore.common.event.magickevent;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.ExtraDataEvent;
import com.rogoshum.magickcore.api.event.RecipeLoadedEvent;
import com.rogoshum.magickcore.api.event.living.LivingDeathEvent;
import com.rogoshum.magickcore.common.entity.projectile.ManaElementOrbEntity;
import com.rogoshum.magickcore.common.event.SubscribeEvent;
import com.rogoshum.magickcore.common.init.*;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.extradata.entity.ElementToolData;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.biome.Biome;

import java.util.*;

public class RegisterEvent {
    private static final HashMap<Class<? extends Entity>, LivingElementTable> spawnElementMap = new HashMap<>();
    private static final HashMap<String, LivingElementTable> spawnElementMap_dimension = new HashMap<>();
    private static final HashMap<String, LivingElementTable> spawnElementMap_biome = new HashMap<>();
    private static final List<EntityType<?>> element_animal = new ArrayList<>();

    @SubscribeEvent
    public void entityExtraData(ExtraDataEvent.Entity event) {
        event.add(LibEntityData.ENTITY_STATE, EntityStateData::new);
        event.add(LibEntityData.ELEMENT_TOOL, ElementToolData::new);
        event.add(LibEntityData.TAKEN_ENTITY, TakenEntityData::new);
    }

    public static void initElementMap() {
        spawnElementMap.put(EnderMan.class, new LivingElementTable(1, 50, LibElements.VOID));
        spawnElementMap.put(Blaze.class, new LivingElementTable(2, 50, LibElements.SOLAR));
        spawnElementMap.put(CaveSpider.class, new LivingElementTable(1, 5, LibElements.WITHER));
        spawnElementMap.put(Spider.class, new LivingElementTable(15, 50, LibElements.WITHER));
        spawnElementMap.put(Shulker.class, new LivingElementTable(2, 200, LibElements.VOID));
        spawnElementMap.put(MagmaCube.class, new LivingElementTable(2, 7, LibElements.SOLAR));
        spawnElementMap.put(Evoker.class, new LivingElementTable(1, 10, LibElements.TAKEN));
        spawnElementMap.put(Vex.class, new LivingElementTable(2, 10, LibElements.TAKEN));
        spawnElementMap.put(Creeper.class, new LivingElementTable(5, 50, LibElements.ARC));
        spawnElementMap.put(Phantom.class, new LivingElementTable(1, 50, LibElements.ARC));
        spawnElementMap.put(SnowGolem.class, new LivingElementTable(10, 20, LibElements.TAKEN));
        spawnElementMap.put(IronGolem.class, new LivingElementTable(1, 10, LibElements.TAKEN));

        spawnElementMap_dimension.put(DimensionType.END_LOCATION.location().toString(), new LivingElementTable(5, 70, LibElements.VOID));
        spawnElementMap_dimension.put(DimensionType.NETHER_LOCATION.location().toString(), new LivingElementTable(7, 70, LibElements.SOLAR));

        spawnElementMap_biome.put(Biome.BiomeCategory.SWAMP.getSerializedName(), new LivingElementTable(4, 30, LibElements.WITHER));
        spawnElementMap_biome.put(Biome.BiomeCategory.ICY.getSerializedName(), new LivingElementTable(4, 30, LibElements.STASIS));
        spawnElementMap_biome.put(Biome.BiomeCategory.MESA.getSerializedName(), new LivingElementTable(6, 30, LibElements.STASIS));
        spawnElementMap_biome.put(Biome.BiomeCategory.EXTREME_HILLS.getSerializedName(), new LivingElementTable(5, 30, LibElements.STASIS));

        element_animal.add(EntityType.COW);
        element_animal.add(EntityType.SHEEP);
        element_animal.add(EntityType.CHICKEN);
        element_animal.add(EntityType.PIG);

        for (int i = 1; i < 6; i++) {
            Int2ObjectMap<VillagerTrades.ItemListing[]> int2ObjectMap = new Int2ObjectOpenHashMap<>();
            int2ObjectMap.put(i, Util.make(new VillagerTrades.ItemListing[1], itemListings -> {
                itemListings[0] = new ModVillager.EntityTypeTrade();
            }));
            VillagerTrades.TRADES.put(ModVillager.MAGE, int2ObjectMap);
        }
        ResourceLocation key = MagickCore.fromId("spirit_ore");
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, key, SPIRIT_ORE);
    }

    public static boolean containAnimalType(EntityType<?> type)
    {
        return element_animal.contains(type);
    }

    public static boolean registerAnimalTypeElementTable(EntityType<?> type) {
        if(element_animal.contains(type))
            return false;

        element_animal.add(type);
        return true;
    }

    public static boolean registerBiomeElementTable(String s, LivingElementTable table) {
        if(spawnElementMap_biome.containsKey(s))
            return false;

        spawnElementMap_biome.put(s, table);
        return true;
    }

    public static boolean registerDimensionElementTable(String s, LivingElementTable table) {
        if(spawnElementMap_dimension.containsKey(s))
            return false;

        spawnElementMap_dimension.put(s, table);
        return true;
    }

    public static boolean registerLivingElementTable(Class clazz, LivingElementTable table) {
        if(spawnElementMap.containsKey(clazz))
            return false;

        spawnElementMap.put(clazz, table);
        return true;
    }

    public static float getShieldCapacity(LivingEntity livingEntity) {
        return MagickCore.rand.nextInt(Math.max(1,  (int)(livingEntity.getHealth() * 0.3f))) + livingEntity.getHealth() * 0.4f;
    }


    public static boolean testIfGenerateShield(LivingEntity livingEntity) {
        for(Player Player : livingEntity.level.players()) {
            if(Player.getOffhandItem().getItem() instanceof EnderpearlItem &&
                    Player.level == livingEntity.level &&
                    Player.distanceToSqr(livingEntity) <= 4096)
                return true;
        }

        return false;
    }

    static {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if(entity instanceof LivingEntity)
                testIfElement((LivingEntity) entity);
        });
    }

    public static boolean testIfElement(LivingEntity living) {
        if(!living.isAlive() || living.level.isClientSide) return false;
        EntityStateData state = ExtraDataUtil.entityStateData(living);
        if(state == null) return false;
        if(!state.allowElement())
            return state.getElement() != ModElements.ORIGIN;
        if(!living.canChangeDimensions())
            state.setElemented();

        if(spawnElementMap.containsKey(living.getClass())) {
            transEntityElement(living, state, spawnElementMap.get(living.getClass()));
        }

        String dimension_name = living.level.dimension().location().toString();
        if(living instanceof Mob && spawnElementMap_dimension.containsKey(dimension_name)) {
            transEntityElement(living, state, spawnElementMap_dimension.get(dimension_name));
        }

        String biome_type = living.level.getBiome(living.blockPosition()).getBiomeCategory().getSerializedName();
        if(living instanceof Mob && spawnElementMap_biome.containsKey(biome_type)) {
            transEntityElement(living, state, spawnElementMap_biome.get(biome_type));
        }
        return state.getElement() != ModElements.ORIGIN;
    }

    public static void transEntityElement(LivingEntity living, EntityStateData state, LivingElementTable table) {
        boolean testMod = testIfGenerateShield(living);
        if(state.allowElement() && (MagickCore.rand.nextInt(table.getElementChance()) == 0 || testMod)) {
            state.setElement(MagickRegistry.getElement(table.element));

            if(MagickCore.rand.nextInt(table.getShieldChance()) == 0 || testMod) {
                state.setFinalMaxElementShield(getShieldCapacity(living));
                state.setMaxManaValue(state.getFinalMaxElementShield());
            }
        }

        state.setElemented();
    }

    public static class LivingElementTable{
        private final int chanceShield;
        private final int chanceElement;
        private final String element;
        protected LivingElementTable(int chanceElement, int chanceShield, String element) {
            this.chanceShield = chanceShield;
            this.chanceElement = chanceElement;
            this.element = element;
        }

        protected int getShieldChance(){return this.chanceShield;}
        protected int getElementChance(){return this.chanceElement;}
        protected String getType(){return this.element;}
    }

    @SubscribeEvent
    public void itemExtraData(ExtraDataEvent.ItemStack event) {
        event.add(LibRegistry.ITEM_DATA, ItemManaData::new);
    }

    public static final ConfiguredFeature<?, ?> SPIRIT_ORE = Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE,
            ModBlocks.SPIRIT_ORE.get().defaultBlockState(),
            6)
    ).range(64).squared().count(20);

    @SubscribeEvent
    public void onAddReload(RecipeLoadedEvent event) {
        /*
        ModRecipes.init();
        Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> recipes = event.getRecipes();
        ImmutableMap.Builder<ResourceLocation, IRecipe<?>> function = recipes.computeIfAbsent(IRecipeType.CRAFTING, (recipeType) -> {
                    return ImmutableMap.builder();
                });
        ModRecipes.getRecipes().forEach((res, recipe) -> function.put(recipe.getId(), recipe));

         */
    }
}
