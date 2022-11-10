package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.item.*;
import com.rogoshum.magickcore.common.item.*;
import com.rogoshum.magickcore.common.item.placeable.ManaCapacityItem;
import com.rogoshum.magickcore.common.item.placeable.PlaceableEntityItem;

import com.rogoshum.magickcore.common.item.placeable.ContextPointerItem;
import com.rogoshum.magickcore.common.item.placeable.SpiritCrystalItem;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.lib.LibMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MagickCore.MOD_ID);
    //block items
    public static final RegistryObject<Item> MATERIAL_JAR = ITEMS.register(LibItem.MATERIAL_JAR, () -> new BlockItem(ModBlocks.MATERIAL_JAR.get()
            , BaseItem.properties().setISTER(() -> MaterialJarItemRenderer::new)));
    public static final RegistryObject<Item> SPIRIT_ORE = ITEMS.register(LibItem.SPIRIT_ORE, () -> new BlockItem(ModBlocks.SPIRIT_ORE.get()
            , BaseItem.properties()));
    public static final RegistryObject<PlaceableEntityItem> SPIRIT_CRYSTAL = ITEMS.register("spirit_crystal", SpiritCrystalItem::new);
    public static final RegistryObject<PlaceableEntityItem> SPIRIT_WOOD_STICK = ITEMS.register("spirit_wood_stick", () -> new PlaceableEntityItem(BaseItem.properties().setISTER(() -> SpiritWoodStickRenderer::new), 0.4f, 0.4f));
    public static final RegistryObject<Item> MAGICK_CONTAINER = ITEMS.register("magick_container", ManaCapacityItem::new);
    public static final RegistryObject<Item> WAND = ITEMS.register(LibItem.WAND, WandItem::new);

    public static final RegistryObject<Item> STAFF = ITEMS.register(LibItem.STAFF, () -> new SpiritWoodStaffItem(BaseItem.properties().maxStackSize(1).setISTER(() -> StaffRenderer::new)));
    public static final RegistryObject<Item> SPIRIT_CRYSTAL_STAFF = ITEMS.register(LibItem.SPIRIT_CRYSTAL_STAFF, () -> new SpiritCrystalStaffItem(BaseItem.properties().maxStackSize(1).setISTER(() -> StaffRenderer::new)));
    public static final RegistryObject<ManaItem> SPIRIT_BOW = ITEMS.register(LibItem.SPIRIT_BOW, () -> new SpiritBowItem(BaseItem.properties().maxStackSize(1).setISTER(() -> SpiritBowRenderer::new)));
    public static final RegistryObject<ManaItem> SPIRIT_SWORD = ITEMS.register(LibItem.SPIRIT_SWORD, () -> new SpiritSwordItem(BaseItem.properties().maxStackSize(1).setISTER(() -> SpiritSwordRenderer::new)));
    public static final RegistryObject<ManaItem> STAR_STAFF = ITEMS.register(LibItem.STAR_STAFF, StarStaffItem::new);
    public static final RegistryObject<ManaItem> ORB_STAFF = ITEMS.register(LibItem.ORB_STAFF, OrbStaffItem::new);
    public static final RegistryObject<ManaItem> LASER_STAFF = ITEMS.register(LibItem.LASER_STAFF, LaserStaffItem::new);
    public static final RegistryObject<ManaItem> RAY_STAFF = ITEMS.register(LibItem.RAY_STAFF, RayStaffItem::new);
    public static final RegistryObject<Item> SUPER_SPAWNER = ITEMS.register(LibItem.SUPER_SPAWNER, SuperItem::new);

    public static final RegistryObject<Item> CONTEXT_CORE = ITEMS.register(LibItem.CONTEXT_CORE, ContextCoreItem::new);
    public static final RegistryObject<Item> MAGICK_CORE = ITEMS.register(LibItem.MAGICK_CONTEXT, MagickContextItem::new);
    public static final RegistryObject<Item> CONTEXT_POINTER = ITEMS.register(LibItem.CONTEXT_POINTER, ContextPointerItem::new);
    public static final RegistryObject<Item> ENTITY_TYPE = ITEMS.register(LibItem.ENTITY_TYPE_ITEM, EntityTypeItem::new);
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
    public static final RegistryObject<Item> ELEMENT_CRYSTAL_SEEDS = ITEMS.register("element_crystal_seeds", () -> new ElementSeedsItem(ModBlocks.element_crystal.get(), BaseItem.properties()));
    //public static final RegistryObject<Item> magick_barrier = ITEMS.register("magick_barrier", () -> new BlockItem(ModBlocks.magick_barrier.get(), BaseItem.properties().setISTER(() -> MagickBarrierRenderer::new)));
    //public static final RegistryObject<Item> magick_supplier = ITEMS.register("magick_supplier", () -> new BlockItem(ModBlocks.magick_supplier.get(), BaseItem.properties()));
    //public static final RegistryObject<Item> magick_repeater = ITEMS.register("magick_repeater", () -> new BlockItem(ModBlocks.magick_repeater.get(), BaseItem.properties().setISTER(() -> MagickRepeaterItemStackTileEntityRenderer::new)));
    //public static final RegistryObject<Item> void_sphere = ITEMS.register("void_sphere", () -> new BlockItem(ModBlocks.void_sphere.get(), BaseItem.properties()));

    public static final RegistryObject<Item> ORB_BOTTLE = ITEMS.register("orb_bottle", OrbBottleItem::new);
    public static final RegistryObject<Item> ELEMENT_MEAT = ITEMS.register("element_meat", () -> new ElementMeatItem(BaseItem.properties().food(
            new Food.Builder().meat().saturation(3f).fastToEat().setAlwaysEdible().effect(() -> new EffectInstance(ModEffects.MANA_STASIS.get(), 1200), 0.1f).build())));
    public static final RegistryObject<Item> ELEMENT_CRYSTAL = ITEMS.register("element_crystal", () -> new ElementCrystalItem(BaseItem.properties()));
    public static final RegistryObject<Item> ELEMENT_WOOL = ITEMS.register("element_wool", ElementWoolItem::new);
    public static final RegistryObject<Item> ELEMENT_STRING = ITEMS.register("element_string", () -> new ElementContainerItem(BaseItem.properties()));

    //public static final RegistryObject<Item> entity_repeater = ITEMS.register("entity_repeater", () -> new LifeRepeaterItem(EntityRepeater::new));
    //public static final RegistryObject<Item> ordinary_repeater = ITEMS.register("ordinary_repeater", () -> new LifeRepeaterItem(OrdinaryRepeater::new));
    //public static final RegistryObject<Item> item_repeater = ITEMS.register("item_repeater", () -> new LifeRepeaterItem(ItemRepeater::new));
    //public static final RegistryObject<Item> material_repeater = ITEMS.register("material_repeater", () -> new LifeRepeaterItem(MaterialRepeater::new));
    //public static final RegistryObject<Item> potion_repeater = ITEMS.register("potion_repeater", () -> new LifeRepeaterItem(PotionRepeater::new));
    //public static final RegistryObject<Item> mana_extract_repeater = ITEMS.register("mana_extract_repeater", () -> new LifeRepeaterItem(ManaExtractRepeater::new));
    //public static final RegistryObject<Item> entity_selector = ITEMS.register("entity_selector", () -> new LifeRepeaterItem(EntitySelector::new, SELECTOR));
    //public static final RegistryObject<Item> living_entity_selector = ITEMS.register("living_entity_selector", () -> new LifeRepeaterItem(LivingEntitySelector::new, SELECTOR));

    //public static final RegistryObject<Item> origin_material = ITEMS.register("origin_material", () -> new ManaMaterialsItem(LibMaterial.ORIGIN));
    public static final RegistryObject<Item> NETHER_STAR_MATERIAL = ITEMS.register("nether_star_material", () -> new ManaMaterialItem(LibMaterial.NETHER_STAR));
    public static final RegistryObject<Item> ENDER_DRAGON_MATERIAL = ITEMS.register("ender_dragon_material", () -> new ManaMaterialItem(LibMaterial.ENDER_DRAGON));
    //public static final RegistryObject<Item> THUNDER_MATERIAL = ITEMS.register("thunder_material", () -> new ManaMaterialItem(LibMaterial.THUNDER));
}
