package com.rogoshum.magickcore.common.event;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IQuadrantEntity;
import com.rogoshum.magickcore.api.event.ExtraDataEvent;
import com.rogoshum.magickcore.api.event.RecipeLoadedEvent;
import com.rogoshum.magickcore.api.extradata.entity.*;
import com.rogoshum.magickcore.api.extradata.item.ItemDimensionData;
import com.rogoshum.magickcore.common.entity.living.QuadrantCrystalEntity;
import com.rogoshum.magickcore.common.entity.projectile.ManaElementOrbEntity;
import com.rogoshum.magickcore.common.init.*;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;
import java.util.function.BiPredicate;

import net.minecraft.world.entity.monster.Enemy;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RegisterEvent {
    private static final HashMap<EntityType<?>, LivingElementTable> MOB_ELEMENT_MAP = new HashMap<>();
    private static final HashMap<BiPredicate<Level, Holder<Biome>>, ElementTable> DIMENSION_ELEMENT_MAP = new HashMap<>();
    private static boolean INIT = false;

    @SubscribeEvent
    public void entityExtraData(ExtraDataEvent.Entity event) {
        event.add(LibEntityData.ENTITY_STATE, EntityStateData::new);
        event.add(LibEntityData.ELEMENT_TOOL, ElementToolData::new);
        event.add(LibEntityData.TAKEN_ENTITY, TakenEntityData::new);
        event.add(LibEntityData.PLAYER_TRADE, PlayerTradeUnlock::new);
        event.add(LibEntityData.LEECH_ENTITY, LeechEntityData::new);
    }

    public static void initElementMap() {
        DIMENSION_ELEMENT_MAP.put(((level, biomeHolder) -> level.dimension() == Level.END), new ElementTable(0.1f, 0.2f, LibElements.VOID));
        DIMENSION_ELEMENT_MAP.put(((level, biomeHolder) -> level.dimension() == Level.NETHER), new ElementTable(0.1f, 0.2f, LibElements.SOLAR));

        DIMENSION_ELEMENT_MAP.put(((level, biomeHolder) -> Biome.getBiomeCategory(biomeHolder) == Biome.BiomeCategory.SWAMP), new ElementTable(0.25f, 0.15f, LibElements.WITHER));
        DIMENSION_ELEMENT_MAP.put(((level, biomeHolder) -> Biome.getBiomeCategory(biomeHolder) == Biome.BiomeCategory.ICY), new ElementTable(0.25f, 0.15f, LibElements.STASIS));

        DIMENSION_ELEMENT_MAP.put(((level, biomeHolder) -> level.isNight()), new ElementTable(0.0075f, 1.0f, LibElements.TAKEN));
        DIMENSION_ELEMENT_MAP.put(((level, biomeHolder) -> level.isThundering() && biomeHolder.value().getPrecipitation() == Biome.Precipitation.RAIN), new ElementTable(0.07f, 1.0f, LibElements.ARC));
        DIMENSION_ELEMENT_MAP.put(((level, biomeHolder) -> biomeHolder.value().getBaseTemperature() < 0.7), new ElementTable(0.1f, 0.5f, LibElements.STASIS));
        DIMENSION_ELEMENT_MAP.put(((level, biomeHolder) -> level.isRaining() && biomeHolder.value().getBaseTemperature() < 0.7), new ElementTable(0.25f, 1.0f, LibElements.STASIS));
    }

    @SubscribeEvent
    public void onDrops(LivingDropsEvent event) {
        ExtraDataUtil.entityStateData(event.getEntityLiving(), state -> {
            if(!event.getEntityLiving().level.isClientSide) {
                int chance = state.getElement() != ModElements.ORIGIN ? 15 : 100;

                if(event.getSource().getEntity() instanceof Player) {
                    PlayerTradeUnlock lock = ExtraDataUtil.playerTradeData((Player) event.getSource().getEntity());
                    if(lock.getUnLock().size() < 8)
                        chance /= 2;
                }

                if(event.getEntityLiving().level.random.nextInt(chance) == 0) {
                    boolean flag = false;
                    if(event.getSource().getEntity() instanceof Player)
                        flag = ExtraDataUtil.playerTradeData((Player) event.getSource().getEntity()).getUnLock().size() != 0;

                    ItemStack stack = ModVillager.getEntityTypeItem(flag ? ModVillager.getRandomType() : ModEntities.SQUARE.get());
                    ItemEntity entity = new ItemEntity(event.getEntityLiving().level, event.getEntityLiving().getX(), event.getEntityLiving().getY() + 0.5f, event.getEntityLiving().getZ(), stack);
                    if(!event.getEntityLiving().level.isClientSide)
                        event.getEntityLiving().level.addFreshEntity(entity);
                } else if(!(event.getEntityLiving() instanceof IQuadrantEntity)) {
                    ManaElementOrbEntity orb = new ManaElementOrbEntity(ModEntities.ELEMENT_ORB.get(), event.getEntityLiving().level);
                    Vec3 vec = event.getEntityLiving().position();
                    orb.setPos(vec.x, vec.y + event.getEntityLiving().getBbHeight() / 2, vec.z);
                    orb.spellContext().element(state.getElement());
                    orb.setOrbType(true);
                    orb.manaCapacity().setMana(event.getEntityLiving().getMaxHealth());
                    orb.spellContext().tick(200);
                    orb.setCaster(event.getEntityLiving());
                    event.getEntityLiving().level.addFreshEntity(orb);
                    if(event.getSource().getEntity() instanceof ServerPlayer) {
                        AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayer) event.getSource().getEntity(), "element_energy_" + state.getElement().type());
                    }
                }
            }
        });

        ExtraDataUtil.entityStateData(event.getEntityLiving(), state -> {
            if (!state.getElement().type().equals(LibElements.ORIGIN) && !event.getEntityLiving().level.isClientSide) {
                Collection<ItemEntity> loots = event.getDrops();
                loots.forEach((e) -> {
                    if (e.getItem().isEdible()) {
                        int count = e.getItem().getCount();
                        ItemStack stack = new ItemStack(ModItems.ELEMENT_MEAT.get());
                        CompoundTag tag = new CompoundTag();
                        tag.putString("ELEMENT", state.getElement().type());
                        stack.setCount(count);
                        stack.setTag(tag);
                        e.setItem(stack);
                    }

                    if (e.getItem().getItem().getRegistryName().toString().contains("wool")) {
                        int count = e.getItem().getCount();
                        ItemStack stack = new ItemStack(ModItems.ELEMENT_WOOL.get());
                        CompoundTag tag = new CompoundTag();
                        tag.putString("ELEMENT", state.getElement().type());
                        stack.setCount(count);
                        stack.setTag(tag);
                        e.setItem(stack);
                    }

                    if (e.getItem().getItem().getRegistryName().toString().contains("string")) {
                        int count = e.getItem().getCount();
                        ItemStack stack = new ItemStack(ModItems.ELEMENT_STRING.get());
                        CompoundTag tag = new CompoundTag();
                        tag.putString("ELEMENT", state.getElement().type());
                        stack.setCount(count);
                        stack.setTag(tag);
                        e.setItem(stack);
                    }
                });
            }
        });
    }

    public static void spawnQuadrantCrystal(WorldGenLevel level, ChunkAccess chunk, BlockPos pos) {
        if(chunk.getPos().x % 12 == 0 && chunk.getPos().z % 12 == 0) {
            ServerLevel server = level.getLevel();
            Random rand = new Random(server.getSeed() + chunk.getPos().x + chunk.getPos().z);
            if(rand.nextInt(5) == 0) {
                QuadrantCrystalEntity quadrant = ModEntities.QUADRANT_CRYSTAL.get().create(server);
                quadrant.spellContext().element(MagickRegistry.getRandomFromAllElements());
                quadrant.spellContext().force(1.2f + rand.nextFloat()*2f);
                quadrant.spellContext().range(8+rand.nextInt(25));
                quadrant.setPos(new Vec3(pos.getX(), 128, pos.getZ()));
                quadrant.init();
                server.addFreshEntity(quadrant);
            }
        }
    }

    public static float getShieldCapacity(LivingEntity livingEntity) {
        return MagickCore.rand.nextInt(Math.max(1,  (int)(livingEntity.getHealth() * 0.6f))) + livingEntity.getHealth() * 0.4f;
    }


    public static boolean testIfGenerateShield(LivingEntity livingEntity) {
        for(Player playerEntity : livingEntity.level.players()) {
            if(playerEntity.getOffhandItem().getItem() instanceof EnderpearlItem &&
                    playerEntity.getCommandSenderWorld() == livingEntity.getCommandSenderWorld() &&
                    playerEntity.distanceToSqr(livingEntity) <= 4096)
                return true;
        }

        return false;
    }

    @SubscribeEvent
    public void onLivingSpawn(LivingSpawnEvent.CheckSpawn event) {
        testIfElement(event.getEntityLiving());
    }

    public static void testIfElement(LivingEntity living) {
        if(!living.isAlive() || living.level.isClientSide) return;
        if(!INIT) {
            INIT = true;
            List<?> list = CommonConfig.ELEMENT_MOB.get();
            for(Object o : list) {
                LivingElementTable table = LivingElementTable.fromString(o.toString());
                if(table != null) {
                    MOB_ELEMENT_MAP.put(table.getMobType(), table);
                }
            }
        }
        EntityStateData state = ExtraDataUtil.entityStateData(living);
        if(state == null) return;
        if(!state.allowElement())
            return;

        if(MOB_ELEMENT_MAP.containsKey(living.getType())) {
            transEntityElement(living, state, MOB_ELEMENT_MAP.get(living.getType()));
        }

        Level level = living.level;
        Holder<Biome> biome = level.getBiome(living.blockPosition());
        if(living instanceof Enemy) {
            for(BiPredicate<Level, Holder<Biome>> predicate : DIMENSION_ELEMENT_MAP.keySet()) {
                if(predicate.test(level, biome)) {
                    transEntityElement(living, state, DIMENSION_ELEMENT_MAP.get(predicate));
                }
            }
        }
        state.setElemented();
    }

    public static void transEntityElement(LivingEntity living, EntityStateData state, ElementTable table) {
        //boolean testMod = testIfGenerateShield(living);
        if(state.allowElement() && (MagickCore.rand.nextFloat() <= table.getElementChance())) {
            state.setElement(MagickRegistry.getElement(table.getType()));
            state.setElemented();
            if(MagickCore.rand.nextFloat() <= table.getShieldChance()) {
                state.setFinalMaxElementShield(getShieldCapacity(living));
                state.setMaxManaValue(state.getFinalMaxElementShield());
            }
        }
    }

    public static class LivingElementTable extends ElementTable {
        private final EntityType<?> type;
        private final String string;
        private LivingElementTable(@Nonnull EntityType<?> mob, float chanceElement, float chanceShield, String element) {
            super(chanceElement, chanceShield, element);
            this.type = mob;
            this.string = type.getDescriptionId()+"_"+chanceElement+"_"+chanceShield+"_"+element;
        }

        @Nullable
        public static LivingElementTable fromString(String s) {
            String[] item = s.split("_");
            if(item.length != 4) return null;
            String type = item[0];
            ResourceLocation res = new ResourceLocation(type);
            if(!ForgeRegistries.ENTITIES.containsKey(res))
                return null;
            EntityType<?> mob = ForgeRegistries.ENTITIES.getValue(res);
            if(mob == null)
                return null;
            String element = item[3];
            if(!ModElements.elements.contains(element))
                return null;
            float chanceElement = Float.parseFloat(item[1]);
            float chanceShield = Float.parseFloat(item[2]);
            if(Float.isNaN(chanceElement) || Float.isNaN(chanceShield))
                return null;
            return new LivingElementTable(mob, Mth.clamp(chanceElement, 0.0f, 1.0f), Mth.clamp(chanceShield, 0.0f, 1.0f), element);
        }

        protected EntityType<?> getMobType() {
            return type;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    public static class ElementTable{
        private final float chanceShield;
        private final float chanceElement;
        private final String element;
        private final String string;
        private ElementTable(float chanceElement, float chanceShield, String element) {
            this.chanceShield = chanceShield;
            this.chanceElement = chanceElement;
            this.element = element;
            this.string = chanceElement+"_"+chanceShield+"_"+element;
        }

        @Nullable
        public static ElementTable fromString(String s) {
            String[] item = s.split("_");
            if(item.length != 3) return null;
            String element = item[2];
            if(!ModElements.elements.contains(element))
                return null;
            float chanceElement = Float.parseFloat(item[0]);
            float chanceShield = Float.parseFloat(item[1]);
            if(Float.isNaN(chanceElement) || Float.isNaN(chanceShield))
                return null;
            return new ElementTable(Mth.clamp(chanceElement, 0.0f, 1.0f), Mth.clamp(chanceShield, 0.0f, 1.0f), element);
        }

        protected float getShieldChance(){return this.chanceShield;}
        protected float getElementChance(){return this.chanceElement;}
        protected String getType(){return this.element;}
        @Override
        public String toString() {
            return string;
        }
    }

    @SubscribeEvent
    public void itemExtraData(ExtraDataEvent.ItemStack event) {
        event.add(LibRegistry.ITEM_DATA, ItemManaData::new);
        event.add(LibRegistry.ITEM_DIMENSION_DATA, ItemDimensionData::new);
    }

    @SubscribeEvent
    public void tradeEvent(VillagerTradesEvent event) {
        if(event.getType() == ModVillager.MAGE.get()) {
            for (int i = 1; i < 6; i++) {
                ArrayList<VillagerTrades.ItemListing> list = new ArrayList<>();
                for (int c = 0; c < 6; ++c) {
                    list.add(new ModVillager.EntityTypeTrade());
                }
                event.getTrades().put(i, list);
            }
        }
    }

    public static List<OreConfiguration.TargetBlockState> ORE_SPIRIT_TARGET_LIST;
    public static Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_SPIRIT;
    public static Holder<PlacedFeature> ORE_SPIRIT_UPPER;
    public static Holder<PlacedFeature> ORE_SPIRIT_MIDDLE;

    public static void registerOres() {
        ORE_SPIRIT_TARGET_LIST = List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, ModBlocks.SPIRIT_ORE.get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEP_SPIRIT_ORE.get().defaultBlockState()));
        ORE_SPIRIT = FeatureUtils.register(MagickCore.fromId("ore_spirit").toString(), Feature.ORE, new OreConfiguration(ORE_SPIRIT_TARGET_LIST, 9));
        ORE_SPIRIT_UPPER = PlacementUtils.register(MagickCore.fromId("ore_spirit_upper").toString(), ORE_SPIRIT, commonOrePlacement(10, HeightRangePlacement.triangle(VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56))));
        ORE_SPIRIT_MIDDLE = PlacementUtils.register(MagickCore.fromId("ore_spirit_middle").toString(), ORE_SPIRIT, commonOrePlacement(90,HeightRangePlacement.triangle(VerticalAnchor.absolute(80), VerticalAnchor.absolute(384))));
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
        return orePlacement(CountPlacement.of(p_195344_), p_195345_);
    }

    @SubscribeEvent
    public void onBiomesLoad(BiomeLoadingEvent event) {
        if(event.getClimate().temperature > 0.5 && event.getClimate().temperature < 1.3) {
            event.getSpawns().getSpawner(MobCategory.CREATURE).add(new MobSpawnSettings.SpawnerData(ModEntities.MAGE.get(), 10, 1, 1));
        }
        event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_SPIRIT_UPPER);
        event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_SPIRIT_MIDDLE);
    }

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
