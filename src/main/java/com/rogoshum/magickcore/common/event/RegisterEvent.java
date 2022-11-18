package com.rogoshum.magickcore.common.event;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.api.event.ExtraDataEvent;
import com.rogoshum.magickcore.common.api.event.RecipeLoadedEvent;
import com.rogoshum.magickcore.common.api.itemstack.IManaData;
import com.rogoshum.magickcore.common.entity.projectile.ManaElementOrbEntity;
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Dimension;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
        spawnElementMap.put(EndermanEntity.class, new LivingElementTable(1, 50, LibElements.VOID));
        spawnElementMap.put(BlazeEntity.class, new LivingElementTable(2, 50, LibElements.SOLAR));
        spawnElementMap.put(CaveSpiderEntity.class, new LivingElementTable(1, 5, LibElements.WITHER));
        spawnElementMap.put(SpiderEntity.class, new LivingElementTable(15, 50, LibElements.WITHER));
        spawnElementMap.put(ShulkerEntity.class, new LivingElementTable(2, 200, LibElements.VOID));
        spawnElementMap.put(MagmaCubeEntity.class, new LivingElementTable(2, 7, LibElements.SOLAR));
        spawnElementMap.put(EvokerEntity.class, new LivingElementTable(1, 10, LibElements.TAKEN));
        spawnElementMap.put(VexEntity.class, new LivingElementTable(2, 10, LibElements.TAKEN));
        spawnElementMap.put(CreeperEntity.class, new LivingElementTable(5, 50, LibElements.ARC));
        spawnElementMap.put(PhantomEntity.class, new LivingElementTable(1, 50, LibElements.ARC));
        spawnElementMap.put(SnowGolemEntity.class, new LivingElementTable(10, 20, LibElements.TAKEN));
        spawnElementMap.put(IronGolemEntity.class, new LivingElementTable(1, 10, LibElements.TAKEN));

        spawnElementMap_dimension.put(Dimension.THE_END.getLocation().toString(), new LivingElementTable(5, 70, LibElements.VOID));
        spawnElementMap_dimension.put(Dimension.THE_NETHER.getLocation().toString(), new LivingElementTable(7, 70, LibElements.SOLAR));

        spawnElementMap_biome.put(Biome.Category.SWAMP.getString(), new LivingElementTable(4, 30, LibElements.WITHER));
        spawnElementMap_biome.put(Biome.Category.ICY.getString(), new LivingElementTable(4, 30, LibElements.STASIS));
        spawnElementMap_biome.put(Biome.Category.MESA.getString(), new LivingElementTable(6, 30, LibElements.STASIS));
        spawnElementMap_biome.put(Biome.Category.EXTREME_HILLS.getString(), new LivingElementTable(5, 30, LibElements.STASIS));

        element_animal.add(EntityType.COW);
        element_animal.add(EntityType.SHEEP);
        element_animal.add(EntityType.CHICKEN);
        element_animal.add(EntityType.PIG);
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

    @SubscribeEvent
    public void onDrops(LivingDropsEvent event) {
        ExtraDataUtil.entityStateData(event.getEntityLiving(), state -> {
            if(!event.getEntityLiving().world.isRemote) {
                /*
                if(!state.getElement().type().equals(LibElements.ORIGIN) && state.getIsDeprived()) {
                    ManaElementOrbEntity orb = new ManaElementOrbEntity(ModEntities.element_orb.get(), event.getEntityLiving().world);
                    Vector3d vec = event.getEntityLiving().getPositionVec();
                    orb.setPosition(vec.x, vec.y + event.getEntityLiving().getHeight() / 2, vec.z);
                    orb.spellContext().element(state.getElement());
                    orb.spellContext().tick(200);
                    orb.setShooter(event.getEntityLiving());
                    event.getEntityLiving().world.addEntity(orb);
                }
                else {
                    ManaPowerEntity orb = new ManaPowerEntity(ModEntities.mana_power.get(), event.getEntityLiving().world);
                    Vector3d vec = event.getEntityLiving().getPositionVec();
                    orb.setPosition(vec.x, vec.y + event.getEntityLiving().getHeight() / 2, vec.z);
                    orb.spellContext().tick(100);
                    orb.setMana(event.getEntityLiving().getMaxHealth() / 2);
                    event.getEntityLiving().world.addEntity(orb);
                }

                 */
                ManaElementOrbEntity orb = new ManaElementOrbEntity(ModEntities.ELEMENT_ORB.get(), event.getEntityLiving().world);
                Vector3d vec = event.getEntityLiving().getPositionVec();
                orb.setPosition(vec.x, vec.y + event.getEntityLiving().getHeight() / 2, vec.z);
                orb.spellContext().element(state.getElement());
                orb.setOrbType(true);
                orb.manaCapacity().setMana(event.getEntityLiving().getMaxHealth());
                orb.spellContext().tick(200);
                orb.setShooter(event.getEntityLiving());
                event.getEntityLiving().world.addEntity(orb);
            }
        });


        ExtraDataUtil.entityStateData(event.getEntityLiving(), state -> {
            if (!state.getElement().type().equals(LibElements.ORIGIN) && !event.getEntityLiving().world.isRemote) {
                Collection<ItemEntity> loots = event.getDrops();
                loots.forEach((e) -> {
                    if (e.getItem().isFood()) {
                        int count = e.getItem().getCount();
                        ItemStack stack = new ItemStack(ModItems.ELEMENT_MEAT.get());
                        CompoundNBT tag = new CompoundNBT();
                        tag.putString("ELEMENT", state.getElement().type());
                        stack.setCount(count);
                        stack.setTag(tag);
                        e.setItem(stack);
                    }

                    if (e.getItem().getItem().getRegistryName().toString().contains("wool")) {
                        int count = e.getItem().getCount();
                        ItemStack stack = new ItemStack(ModItems.ELEMENT_WOOL.get());
                        CompoundNBT tag = new CompoundNBT();
                        tag.putString("ELEMENT", state.getElement().type());
                        stack.setCount(count);
                        stack.setTag(tag);
                        e.setItem(stack);
                    }
                });
            }
        });
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        /*
        if(event.getSource().getTrueSource() == event.getSource().getImmediateSource() && event.getSource().getTrueSource() instanceof LivingEntity) {
            ItemStack stack = ((LivingEntity) event.getSource().getTrueSource()).getHeldItemMainhand();
            if(EnchantmentHelper.getEnchantmentLevel(ModEnchantments.ELEMENT_DEPRIVATION.get(), stack) > 0) {
                ExtraDataHelper.entityStateData(event.getEntityLiving(), EntityStateData::setDeprived);
            }
        }

         */
    }

    public static float getShieldCapacity(LivingEntity livingEntity) {
        return MagickCore.rand.nextInt( (int)(livingEntity.getHealth() * 0.3f)) + livingEntity.getHealth() * 0.4f;
    }


    public static boolean testIfGenerateShield(LivingEntity livingEntity) {
        for(PlayerEntity playerEntity : livingEntity.world.getPlayers()) {
            if(playerEntity.getHeldItemOffhand().getItem() instanceof EnderPearlItem &&
                    playerEntity.getEntityWorld() == livingEntity.getEntityWorld() &&
                    playerEntity.getDistanceSq(livingEntity) <= 4096)
                return true;
        }

        return false;
    }

    @SubscribeEvent
    public void onLivingSpawn(LivingSpawnEvent.CheckSpawn event) {
        testIfElement(event.getEntityLiving());
    }

    public static boolean testIfElement(LivingEntity living) {
        EntityStateData state = ExtraDataUtil.entityStateData(living);
        if(state == null) return false;
        if(!state.allowElement())
            return state.getElement() != ModElements.ORIGIN;
        if(!living.isNonBoss())
            state.setElemented();

        if(spawnElementMap.containsKey(living.getClass())) {
            transEntityElement(living, state, spawnElementMap.get(living.getClass()));
        }

        String dimension_name = living.world.getDimensionKey().getLocation().toString();
        if(living instanceof IMob && spawnElementMap_dimension.containsKey(dimension_name)) {
            transEntityElement(living, state, spawnElementMap_dimension.get(dimension_name));
        }

        String biome_type = living.world.getBiome(living.getPosition()).getCategory().getString();
        if(living instanceof IMob && spawnElementMap_biome.containsKey(biome_type)) {
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

    @SubscribeEvent
    public void tradeEvent(VillagerTradesEvent event) {
        if(event.getType() == ModVillager.MAGE.get()) {
            for (int i = 1; i < 6; i++) {
                ArrayList<VillagerTrades.ITrade> list = new ArrayList<>();
                for (int c = 0; c < i; ++c) {
                    list.add(new ModVillager.EntityTypeTrade());
                }
                event.getTrades().put(i, list);
            }
        }
    }

    @SubscribeEvent
    public void onBiomesLoad(BiomeLoadingEvent event) {
        if(event.getClimate().temperature > 0.5 && event.getClimate().temperature < 1.3) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(ModEntities.MAGE.get(), 10, 1, 1));
        }
        event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD,
                ModBlocks.SPIRIT_ORE.get().getDefaultState(),
                6)
        ).range(64).square().func_242731_b(20));
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
