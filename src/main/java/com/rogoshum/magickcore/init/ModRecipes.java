package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.helper.NBTTagHelper;
import com.rogoshum.magickcore.item.ManaItem;
import com.rogoshum.magickcore.item.OrbBottleItem;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.recipes.CopyNBTTagRecipe;
import com.rogoshum.magickcore.recipes.ItemTagMatchContainer;
import com.rogoshum.magickcore.recipes.NBTRecipe;
import com.rogoshum.magickcore.recipes.NBTRecipeContainer;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IRegistryDelegate;
import org.lwjgl.system.CallbackI;

import java.util.HashMap;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = MagickCore.MOD_ID)
public class ModRecipes {
    public static final String element_crystal_seeds = MagickCore.MOD_ID + ":element_crystal_seeds";
    public static final String element_wool = MagickCore.MOD_ID + ":element_wool";
    public static final String element_string = MagickCore.MOD_ID + ":element_string";
    public static final String orb_bottle = MagickCore.MOD_ID + ":orb_bottle";
    public static final String element_crystal = MagickCore.MOD_ID + ":element_crystal";
    public static final String arc = MagickCore.MOD_ID + ":arc";
    public static final String voidItem = MagickCore.MOD_ID + ":void";
    public static final String solar = MagickCore.MOD_ID + ":solar";
    public static final String stasis = MagickCore.MOD_ID + ":stasis";
    public static final String wither = MagickCore.MOD_ID + ":wither";
    public static final String taken = MagickCore.MOD_ID + ":taken";

    public static final CopyNBTTagRecipe elementOrbTag = (CopyNBTTagRecipe) CopyNBTTagRecipe.create(element_crystal_seeds, NBTRecipeContainer.ItemContainer.create(orb_bottle, "ELEMENT"), NBTRecipeContainer.ItemContainer.create("seeds")).shapeless();
    public static final SpecialRecipeSerializer<?> element_orb_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementOrbTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_orb_recipe;
        }
    }).setRegistryName("element_orb_recipe");

    public static final CopyNBTTagRecipe elementWoolTag = (CopyNBTTagRecipe) CopyNBTTagRecipe.create(element_wool, NBTRecipeContainer.ItemContainer.create(orb_bottle, "ELEMENT"), NBTRecipeContainer.ItemContainer.create("wool")).shapeless();
    public static final SpecialRecipeSerializer<?> element_wool_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementWoolTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_wool_recipe;
        }
    }).setRegistryName("element_wool_recipe");

    public static final CopyNBTTagRecipe elementStringTag = (CopyNBTTagRecipe) CopyNBTTagRecipe.create(element_string, 4, NBTRecipeContainer.ItemContainer.create("element_wool", "ELEMENT")).shapeless();
    public static final SpecialRecipeSerializer<?> element_string_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementStringTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_string_recipe;
        }
    }).setRegistryName("element_string_recipe");

    public static SpecialRecipeSerializer<?> arc_element_recipe;
    public static SpecialRecipeSerializer<?> solar_element_recipe;
    public static SpecialRecipeSerializer<?> void_element_recipe;
    public static SpecialRecipeSerializer<?> stasis_element_recipe;
    public static SpecialRecipeSerializer<?> wither_element_recipe;
    public static SpecialRecipeSerializer<?> taken_element_recipe;

    public static void init()
    {
        ItemTagMatchContainer arcContainer = ItemTagMatchContainer.create(element_crystal, getStringTagMap("ELEMENT", LibElements.ARC));
        arc_element_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CopyNBTTagRecipe.create(arc, arcContainer, arcContainer, arcContainer, arcContainer).shapeless(), r){
            @Override
            public IRecipeSerializer<?> getSerializer() {
                return arc_element_recipe;
            }
        }).setRegistryName("arc_element_recipe");

        ItemTagMatchContainer solarContainer = ItemTagMatchContainer.create(element_crystal, getStringTagMap("ELEMENT", LibElements.SOLAR));
        solar_element_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CopyNBTTagRecipe.create(solar, solarContainer, solarContainer, solarContainer, solarContainer).shapeless(), r){
            @Override
            public IRecipeSerializer<?> getSerializer() {
                return solar_element_recipe;
            }
        }).setRegistryName("solar_element_recipe");

        ItemTagMatchContainer voidContainer = ItemTagMatchContainer.create(element_crystal, getStringTagMap("ELEMENT", LibElements.VOID));
        void_element_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CopyNBTTagRecipe.create(voidItem, voidContainer, voidContainer, voidContainer, voidContainer).shapeless(), r){
            @Override
            public IRecipeSerializer<?> getSerializer() {
                return void_element_recipe;
            }
        }).setRegistryName("void_element_recipe");

        ItemTagMatchContainer stasisContainer = ItemTagMatchContainer.create(element_crystal, getStringTagMap("ELEMENT", LibElements.STASIS));
        stasis_element_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CopyNBTTagRecipe.create(stasis, stasisContainer, stasisContainer, stasisContainer, stasisContainer).shapeless(), r){
            @Override
            public IRecipeSerializer<?> getSerializer() {
                return stasis_element_recipe;
            }
        }).setRegistryName("stasis_element_recipe");

        ItemTagMatchContainer witherContainer = ItemTagMatchContainer.create(element_crystal, getStringTagMap("ELEMENT", LibElements.WITHER));
        wither_element_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CopyNBTTagRecipe.create(wither, witherContainer, witherContainer, witherContainer, witherContainer).shapeless(), r){
            @Override
            public IRecipeSerializer<?> getSerializer() {
                return wither_element_recipe;
            }
        }).setRegistryName("wither_element_recipe");

        ItemTagMatchContainer takenContainer = ItemTagMatchContainer.create(element_crystal, getStringTagMap("ELEMENT", LibElements.TAKEN));
        taken_element_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CopyNBTTagRecipe.create(taken, takenContainer, takenContainer, takenContainer, takenContainer).shapeless(), r){
            @Override
            public IRecipeSerializer<?> getSerializer() {
                return taken_element_recipe;
            }
        }).setRegistryName("taken_element_recipe");
    }

    @SubscribeEvent
    public static void registerRecipes(final RegistryEvent.Register<IRecipeSerializer<?>> event)
    {
        init();
        event.getRegistry().registerAll(
                element_orb_recipe,
                arc_element_recipe,
                solar_element_recipe,
                void_element_recipe,
                stasis_element_recipe,
                wither_element_recipe,
                taken_element_recipe,
                element_wool_recipe,
                element_string_recipe
        );
    }

    public static HashMap<String, INBT> getStringTagMap(String key, String value)
    {
        HashMap<String, INBT> hashMap = new HashMap<String, INBT>();
        hashMap.put(key, StringNBT.valueOf(value));
        return hashMap;
    }
}
