package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.item.*;
import com.rogoshum.magickcore.common.item.*;
import com.rogoshum.magickcore.common.item.material.*;
import com.rogoshum.magickcore.common.item.placeable.*;

import com.rogoshum.magickcore.common.item.tool.*;
import com.rogoshum.magickcore.common.lib.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MagickCore.MOD_ID);
    public static final RegistryObject<Item> MAGE_EGG = ITEMS.register("mage_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.MAGE::get, 0xff0000, 0xffe700, new Item.Properties().tab(ModGroups.ITEM_GROUP)));
    //block items
    public static final RegistryObject<Item> MATERIAL_JAR = ITEMS.register(LibItem.MATERIAL_JAR, () -> new EntityRendererBlockItem(ModBlocks.MATERIAL_JAR.get()
            , BaseItem.properties(), () -> MaterialJarItemRenderer::new));
    public static final RegistryObject<Item> SPIRIT_ORE = ITEMS.register(LibItem.SPIRIT_ORE, () -> new BlockItem(ModBlocks.SPIRIT_ORE.get()
            , BaseItem.properties()));
    public static final RegistryObject<Item> DEEP_SPIRIT_ORE = ITEMS.register(LibItem.DEEP_SPIRIT_ORE, () -> new BlockItem(ModBlocks.DEEP_SPIRIT_ORE.get()
            , BaseItem.properties()));
    public static final RegistryObject<Item> ITEM_EXTRACTOR = ITEMS.register(LibItem.ITEM_EXTRACTOR, () -> new EntityRendererBlockItem(ModBlocks.ITEM_EXTRACTOR.get()
            , BaseItem.properties(), () -> ItemExtractorRenderer::new));
    public static final RegistryObject<Item> RADIANCE_CRYSTAL = ITEMS.register(LibItem.RADIANCE_CRYSTAL, RadianceCrystalItem::new);
    public static final RegistryObject<Item> DIMENSION_INFLATE = ITEMS.register(LibItem.DIMENSION_INFLATE, () -> new BlockItem(ModBlocks.DIMENSION_INFLATE.get()
            , BaseItem.properties()));
    public static final RegistryObject<PlaceableEntityItem> SPIRIT_CRYSTAL = ITEMS.register("spirit_crystal", SpiritCrystalItem::new);
    public static final RegistryObject<PlaceableEntityItem> SPIRIT_WOOD_STICK = ITEMS.register("spirit_wood_stick", () -> new PlaceableEntityItem(BaseItem.properties(), 0.4f, 0.4f));
    public static final RegistryObject<Item> MAGICK_CONTAINER = ITEMS.register("magick_container", ManaCapacityItem::new);
    public static final RegistryObject<Item> WAND = ITEMS.register(LibItem.WAND, WandItem::new);

    public static final RegistryObject<Item> STAFF = ITEMS.register(LibItem.STAFF, () -> new SpiritWoodStaffItem(BaseItem.properties().stacksTo(1)));
    public static final RegistryObject<Item> SPIRIT_CRYSTAL_STAFF = ITEMS.register(LibItem.SPIRIT_CRYSTAL_STAFF, () -> new SpiritCrystalStaffItem(BaseItem.properties().stacksTo(1)));
    public static final RegistryObject<ManaItem> SPIRIT_BOW = ITEMS.register(LibItem.SPIRIT_BOW, () -> new SpiritBowItem(BaseItem.properties().stacksTo(1)));
    public static final RegistryObject<ManaItem> SPIRIT_SWORD = ITEMS.register(LibItem.SPIRIT_SWORD, () -> new SpiritSwordItem(BaseItem.properties().stacksTo(1)));
    public static final RegistryObject<ManaItem> STAR_STAFF = ITEMS.register(LibItem.STAR_STAFF, StarStaffItem::new);
    public static final RegistryObject<ManaItem> ORB_STAFF = ITEMS.register(LibItem.ORB_STAFF, OrbStaffItem::new);
    public static final RegistryObject<ManaItem> LASER_STAFF = ITEMS.register(LibItem.LASER_STAFF, LaserStaffItem::new);
    public static final RegistryObject<ManaItem> RAY_STAFF = ITEMS.register(LibItem.RAY_STAFF, RayStaffItem::new);
    public static final RegistryObject<Item> SUPER_SPAWNER = ITEMS.register(LibItem.SUPER_SPAWNER, SuperItem::new);

    public static final RegistryObject<Item> ARTIFICIAL_LIFE = ITEMS.register(LibEntities.ARTIFICIAL_LIFE, ArtificialLifeItem::new);
    public static final RegistryObject<Item> CONTEXT_CORE = ITEMS.register(LibItem.CONTEXT_CORE, ContextCoreItem::new);
    public static final RegistryObject<Item> MAGICK_CORE = ITEMS.register(LibItem.MAGICK_CONTEXT, MagickContextItem::new);
    public static final RegistryObject<Item> CONTEXT_POINTER = ITEMS.register(LibItem.CONTEXT_POINTER, ContextPointerItem::new);
    public static final RegistryObject<Item> ENTITY_TYPE = ITEMS.register(LibItem.ENTITY_TYPE_ITEM, EntityTypeItem::new);
    public static final RegistryObject<Item> POTION_TYPE = ITEMS.register(LibItem.POTION_TYPE_ITEM, PotionTypeItem::new);
    public static final RegistryObject<Item> MANA_ENERGY = ITEMS.register(LibItem.MANA_ENERGY, ManaEnergyItem::new);

    public static final RegistryObject<Item> ARC = ITEMS.register(LibItem.ARC, () -> new ElementItem(LibElements.ARC));
    public static final RegistryObject<Item> SOLAR = ITEMS.register(LibItem.SOLAR, () -> new ElementItem(LibElements.SOLAR));
    public static final RegistryObject<Item> VOID = ITEMS.register(LibItem.VOID, () -> new ElementItem(LibElements.VOID));
    public static final RegistryObject<Item> STASIS = ITEMS.register(LibItem.STASIS, () -> new ElementItem(LibElements.STASIS));
    public static final RegistryObject<Item> WITHER = ITEMS.register(LibItem.WITHER, () -> new ElementItem(LibElements.WITHER));
    public static final RegistryObject<Item> TAKEN = ITEMS.register(LibItem.TAKEN, () -> new ElementItem(LibElements.TAKEN));
    //public static final RegistryObject<Item> AIR = ITEMS.register(LibItem.AIR, () -> new ElementItem(LibElements.AIR));

    //material
    public static final RegistryObject<Item> MANA_SPIDER_EYE = ITEMS.register("mana_spider_eye", ManaFermentedSpiderEyeItem::new);
    public static final RegistryObject<Item> MANA_GUNPOWDER = ITEMS.register("mana_gunpowder", ManaGunpowderItem::new);
    public static final RegistryObject<Item> MANA_DRAGON_BREATH = ITEMS.register("mana_dragon_breath", ManaDragonBreathItem::new);
    public static final RegistryObject<Item> MANA_NETHER_WART = ITEMS.register("mana_nether_wart", ManaNetherWartItem::new);
    public static final RegistryObject<Item> MANA_BONE = ITEMS.register("mana_bone", ManaBoneItem::new);
    public static final RegistryObject<Item> MANA_FLESH = ITEMS.register("mana_flesh", ManaFleshItem::new);
    public static final RegistryObject<Item> POSITION_MEMORY = ITEMS.register("position_memory", PositionMemoryItem::new);
    public static final RegistryObject<Item> DIRECTION_MEMORY = ITEMS.register("direction_memory", DirectionMemoryItem::new);
    public static final RegistryObject<Item> OFFSET_MEMORY = ITEMS.register("offset_memory", OffsetMemoryItem::new);
    public static final RegistryObject<Item> COMPLETELY_SELF = ITEMS.register("completely_self", CompletelySelfItem::new);
    public static final RegistryObject<Item> REVERSE = ITEMS.register("reverse", ReverseItem::new);
    public static final RegistryObject<Item> SEPARATOR = ITEMS.register("form_separator", FormSeparatorItem::new);
    public static final RegistryObject<Item> CONDITION_BLOCK = ITEMS.register("condition_block", () -> new ConditionItem(LibConditions.BLOCK_ONLY));
    public static final RegistryObject<Item> CONDITION_NON_LIVING = ITEMS.register("condition_non_living", () -> new ConditionItem(LibConditions.NON_LIVING_ENTITY));
    public static final RegistryObject<Item> CONDITION_LIVING = ITEMS.register("condition_living", () -> new ConditionItem(LibConditions.LIVING_ENTITY));

    public static final RegistryObject<Item> ELEMENT_CRYSTAL_SEEDS = ITEMS.register("element_crystal_seeds", () -> new ElementSeedsItem(ModBlocks.ELEMENT_CRYSTAL.get(), BaseItem.properties()));
    public static final RegistryObject<Item> ORB_BOTTLE = ITEMS.register("orb_bottle", OrbBottleItem::new);
    public static final RegistryObject<Item> ELEMENT_MEAT = ITEMS.register("element_meat", () -> new ElementMeatItem(BaseItem.properties().food(
            new FoodProperties.Builder().meat().saturationMod(3f).fast().alwaysEat().effect(() -> new MobEffectInstance(ModEffects.MANA_STASIS.get(), 1200), 0.1f).build())));
    public static final RegistryObject<Item> ELEMENT_CRYSTAL = ITEMS.register("element_crystal", () -> new ElementCrystalItem(BaseItem.properties()));
    public static final RegistryObject<Item> QUADRANT_FRAGMENTS = ITEMS.register("quadrant_fragments", () -> new QuadrantFragmentItem(BaseItem.properties()));
    public static final RegistryObject<Item> ELEMENT_WOOL = ITEMS.register("element_wool", ElementWoolItem::new);
    public static final RegistryObject<Item> ELEMENT_STRING = ITEMS.register("element_string", ElementStringItem::new);
    public static final RegistryObject<Item> ASSEMBLY_ESSENCE = ITEMS.register("assembly_essence", AssemblyEssenceItem::new);
    public static final RegistryObject<Item> CRYSTAL_BOX = ITEMS.register("crystal_box", CrystalBoxItem::new);
    public static final RegistryObject<Item> COLORED_GLOW_DUST = ITEMS.register("colored_glow_dust", ColoredGlowDustItem::new);

    //public static final RegistryObject<Item> origin_material = ITEMS.register("origin_material", () -> new ManaMaterialsItem(LibMaterial.ORIGIN));
    public static final RegistryObject<Item> NETHER_STAR_MATERIAL = ITEMS.register("nether_star_material", () -> new ManaMaterialItem(LibMaterial.NETHER_STAR));
    public static final RegistryObject<Item> ENDER_DRAGON_MATERIAL = ITEMS.register("ender_dragon_material", () -> new ManaMaterialItem(LibMaterial.ENDER_DRAGON));
    //public static final RegistryObject<Item> THUNDER_MATERIAL = ITEMS.register("thunder_material", () -> new ManaMaterialItem(LibMaterial.THUNDER));
}
