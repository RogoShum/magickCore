package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.item.*;

import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.lib.LibItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MagickCore.MOD_ID);
    public static final RegistryObject<ManaItem> star_staff = ITEMS.register(LibItem.STAR_STAFF, StarStaffItem::new);
    public static final RegistryObject<ManaItem> orb_staff = ITEMS.register(LibItem.ORB_STAFF, OrbStaffItem::new);
    public static final RegistryObject<ManaItem> laser_staff = ITEMS.register(LibItem.LASER_STAFF, LaserStaffItem::new);
    public static final RegistryObject<Item> super_spawner = ITEMS.register("super_spawner", SuperItem::new);
    public static final RegistryObject<Item> rune = ITEMS.register("rune", RuneItem::new);
    public static final RegistryObject<Item> rift = ITEMS.register("rift", RiftItem::new);
    public static final RegistryObject<ManaItem> eye = ITEMS.register("eye", EyeItem::new);

    //public static final RegistryObject<ManaItem> buff = ITEMS.register("buff", BuffItem::new);

    public static final RegistryObject<Item> arc = ITEMS.register("arc", () -> new ElementItem(LibElements.ARC));
    public static final RegistryObject<Item> solar = ITEMS.register("solar", () -> new ElementItem(LibElements.SOLAR));
    public static final RegistryObject<Item> voidE = ITEMS.register("void", () -> new ElementItem(LibElements.VOID));
    public static final RegistryObject<Item> stasis = ITEMS.register("stasis", () -> new ElementItem(LibElements.STASIS));
    public static final RegistryObject<Item> wither = ITEMS.register("wither", () -> new ElementItem(LibElements.WITHER));
    public static final RegistryObject<Item> taken = ITEMS.register("taken", () -> new ElementItem(LibElements.TAKEN));

    //material
    public static final RegistryObject<Item> mana_spider_eye = ITEMS.register("mana_spider_eye", ManaFermentedSpiderEyeItem::new);
    public static final RegistryObject<Item> mana_glowstone = ITEMS.register("mana_glowstone", ManaGlowstoneItem::new);
    public static final RegistryObject<Item> mana_gunpowder = ITEMS.register("mana_gunpowder", ManaGunpowderItem::new);
    public static final RegistryObject<Item> mana_radstone = ITEMS.register("mana_redstone", ManaRedstoneItem::new);
    public static final RegistryObject<Item> mana_dragon_breath = ITEMS.register("mana_dragon_breath", ManaDragonBreathItem::new);
    public static final RegistryObject<Item> mana_nether_wart = ITEMS.register("mana_nether_wart", ManaNetherWartItem::new);

    public static final RegistryObject<Item> magick_crafting = ITEMS.register("magick_crafting", MagickCraftingItem::new);
    public static final RegistryObject<Item> magick_container = ITEMS.register("magick_container", MagickContainerItem::new);
    public static final RegistryObject<Item> element_crystal_seeds = ITEMS.register("element_crystal_seeds", () -> new ElementSeedsItem(ModBlocks.element_crystal.get(), BaseItem.properties.maxStackSize(4)));

    public static final RegistryObject<Item> orb_bottle = ITEMS.register("orb_bottle", OrbBottleItem::new);
    public static final RegistryObject<Item> element_meat = ITEMS.register("element_meat", () -> new ElementMeatItem(BaseItem.properties.maxStackSize(16).food(
            new Food.Builder().meat().saturation(3f).fastToEat().setAlwaysEdible().effect(() -> new EffectInstance(ModEffects.MANA_STASIS.get(), 1200), 0.1f).build())));
    public static final RegistryObject<Item> element_crystal = ITEMS.register("element_crystal", () -> new ElementCrystalItem(BaseItem.properties.maxStackSize(8)));
    public static final RegistryObject<Item> element_wool = ITEMS.register("element_wool", ElementWoolItem::new);
    public static final RegistryObject<Item> element_string = ITEMS.register("element_string", () -> new ElementContainerItem(BaseItem.properties.maxStackSize(64)));
}
