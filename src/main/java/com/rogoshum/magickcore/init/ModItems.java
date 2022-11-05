package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.item.MaterialJarItemRenderer;
import com.rogoshum.magickcore.client.item.SpiritWoodStickRenderer;
import com.rogoshum.magickcore.item.*;

import com.rogoshum.magickcore.item.placeable.ContextPointerItem;
import com.rogoshum.magickcore.item.placeable.ManaCapacityItem;
import com.rogoshum.magickcore.item.placeable.PlaceableEntityItem;
import com.rogoshum.magickcore.item.placeable.SpiritCrystalItem;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.lib.LibItem;
import com.rogoshum.magickcore.lib.LibMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final String SELECTOR = "selector";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MagickCore.MOD_ID);
    //block items
    public static final RegistryObject<Item> MATERIAL_JAR = ITEMS.register(LibItem.MATERIAL_JAR, () -> new BlockItem(ModBlocks.MATERIAL_JAR.get()
            , BaseItem.properties().setISTER(() -> MaterialJarItemRenderer::new)));
    public static final RegistryObject<Item> SPIRIT_ORE = ITEMS.register(LibItem.SPIRIT_ORE, () -> new BlockItem(ModBlocks.SPIRIT_ORE.get()
            , BaseItem.properties()));

    public static final RegistryObject<Item> wand = ITEMS.register(LibItem.WAND, WandItem::new);
    public static final RegistryObject<ManaItem> star_staff = ITEMS.register(LibItem.STAR_STAFF, StarStaffItem::new);
    public static final RegistryObject<ManaItem> orb_staff = ITEMS.register(LibItem.ORB_STAFF, OrbStaffItem::new);
    public static final RegistryObject<ManaItem> laser_staff = ITEMS.register(LibItem.LASER_STAFF, LaserStaffItem::new);
    public static final RegistryObject<Item> super_spawner = ITEMS.register(LibItem.SUPER_SPAWNER, SuperItem::new);

    public static final RegistryObject<Item> CONTEXT_CORE = ITEMS.register(LibItem.CONTEXT_CORE, ContextCoreItem::new);
    public static final RegistryObject<Item> MAGICK_CORE = ITEMS.register(LibItem.MAGICK_CONTEXT, MagickContextItem::new);
    public static final RegistryObject<Item> CONTEXT_POINTER = ITEMS.register(LibItem.CONTEXT_POINTER, ContextPointerItem::new);
    public static final RegistryObject<Item> STAFF = ITEMS.register(LibItem.STAFF, StaffItem::new);
    public static final RegistryObject<Item> ENTITY_TYPE = ITEMS.register(LibItem.ENTITY_TYPE_ITEM, EntityTypeItem::new);
    public static final RegistryObject<Item> MANA_ENERGY = ITEMS.register(LibItem.MANA_ENERGY, ManaEnergyItem::new);

    public static final RegistryObject<Item> arc = ITEMS.register(LibItem.ARC, () -> new ElementItem(LibElements.ARC));
    public static final RegistryObject<Item> solar = ITEMS.register(LibItem.SOLAR, () -> new ElementItem(LibElements.SOLAR));
    public static final RegistryObject<Item> voidE = ITEMS.register(LibItem.VOID, () -> new ElementItem(LibElements.VOID));
    public static final RegistryObject<Item> stasis = ITEMS.register(LibItem.STASIS, () -> new ElementItem(LibElements.STASIS));
    public static final RegistryObject<Item> wither = ITEMS.register(LibItem.WITHER, () -> new ElementItem(LibElements.WITHER));
    public static final RegistryObject<Item> taken = ITEMS.register(LibItem.TAKEN, () -> new ElementItem(LibElements.TAKEN));
    public static final RegistryObject<Item> AIR = ITEMS.register(LibItem.AIR, () -> new ElementItem(LibElements.AIR));

    //material
    public static final RegistryObject<Item> mana_spider_eye = ITEMS.register("mana_spider_eye", ManaFermentedSpiderEyeItem::new);
    public static final RegistryObject<Item> mana_gunpowder = ITEMS.register("mana_gunpowder", ManaGunpowderItem::new);
    public static final RegistryObject<Item> mana_dragon_breath = ITEMS.register("mana_dragon_breath", ManaDragonBreathItem::new);
    public static final RegistryObject<Item> mana_nether_wart = ITEMS.register("mana_nether_wart", ManaNetherWartItem::new);
    public static final RegistryObject<PlaceableEntityItem> spirit_crystal = ITEMS.register("spirit_crystal", SpiritCrystalItem::new);
    public static final RegistryObject<PlaceableEntityItem> spirit_wood_stick = ITEMS.register("spirit_wood_stick", () -> new PlaceableEntityItem(BaseItem.properties().setISTER(() -> SpiritWoodStickRenderer::new), 0.4f, 0.4f));
    public static final RegistryObject<Item> magick_container = ITEMS.register("magick_container", ManaCapacityItem::new);
    public static final RegistryObject<Item> element_crystal_seeds = ITEMS.register("element_crystal_seeds", () -> new ElementSeedsItem(ModBlocks.element_crystal.get(), BaseItem.properties().maxStackSize(32)));
    //public static final RegistryObject<Item> magick_barrier = ITEMS.register("magick_barrier", () -> new BlockItem(ModBlocks.magick_barrier.get(), BaseItem.properties().setISTER(() -> MagickBarrierRenderer::new)));
    //public static final RegistryObject<Item> magick_supplier = ITEMS.register("magick_supplier", () -> new BlockItem(ModBlocks.magick_supplier.get(), BaseItem.properties()));
    //public static final RegistryObject<Item> magick_repeater = ITEMS.register("magick_repeater", () -> new BlockItem(ModBlocks.magick_repeater.get(), BaseItem.properties().setISTER(() -> MagickRepeaterItemStackTileEntityRenderer::new)));
    //public static final RegistryObject<Item> void_sphere = ITEMS.register("void_sphere", () -> new BlockItem(ModBlocks.void_sphere.get(), BaseItem.properties()));

    public static final RegistryObject<Item> orb_bottle = ITEMS.register("orb_bottle", OrbBottleItem::new);
    public static final RegistryObject<Item> element_meat = ITEMS.register("element_meat", () -> new ElementMeatItem(BaseItem.properties().food(
            new Food.Builder().meat().saturation(3f).fastToEat().setAlwaysEdible().effect(() -> new EffectInstance(ModEffects.MANA_STASIS.get(), 1200), 0.1f).build())));
    public static final RegistryObject<Item> element_crystal = ITEMS.register("element_crystal", () -> new ElementCrystalItem(BaseItem.properties().maxStackSize(32)));
    public static final RegistryObject<Item> element_wool = ITEMS.register("element_wool", ElementWoolItem::new);
    public static final RegistryObject<Item> element_string = ITEMS.register("element_string", () -> new ElementContainerItem(BaseItem.properties().maxStackSize(64)));

    //public static final RegistryObject<Item> entity_repeater = ITEMS.register("entity_repeater", () -> new LifeRepeaterItem(EntityRepeater::new));
    //public static final RegistryObject<Item> ordinary_repeater = ITEMS.register("ordinary_repeater", () -> new LifeRepeaterItem(OrdinaryRepeater::new));
    //public static final RegistryObject<Item> item_repeater = ITEMS.register("item_repeater", () -> new LifeRepeaterItem(ItemRepeater::new));
    //public static final RegistryObject<Item> material_repeater = ITEMS.register("material_repeater", () -> new LifeRepeaterItem(MaterialRepeater::new));
    //public static final RegistryObject<Item> potion_repeater = ITEMS.register("potion_repeater", () -> new LifeRepeaterItem(PotionRepeater::new));
    //public static final RegistryObject<Item> mana_extract_repeater = ITEMS.register("mana_extract_repeater", () -> new LifeRepeaterItem(ManaExtractRepeater::new));
    //public static final RegistryObject<Item> entity_selector = ITEMS.register("entity_selector", () -> new LifeRepeaterItem(EntitySelector::new, SELECTOR));
    //public static final RegistryObject<Item> living_entity_selector = ITEMS.register("living_entity_selector", () -> new LifeRepeaterItem(LivingEntitySelector::new, SELECTOR));

    //public static final RegistryObject<Item> origin_material = ITEMS.register("origin_material", () -> new ManaMaterialsItem(LibMaterial.ORIGIN));
    public static final RegistryObject<Item> nether_star_material = ITEMS.register("nether_star_material", () -> new ManaMaterialItem(LibMaterial.NETHER_STAR));
    public static final RegistryObject<Item> ender_dragon_material = ITEMS.register("ender_dragon_material", () -> new ManaMaterialItem(LibMaterial.ENDER_DRAGON));
    public static final RegistryObject<Item> thunder_material = ITEMS.register("thunder_material", () -> new ManaMaterialItem(LibMaterial.THUNDER));
}
