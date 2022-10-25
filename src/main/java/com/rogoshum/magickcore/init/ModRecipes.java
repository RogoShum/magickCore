package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IItemContainer;
import com.rogoshum.magickcore.api.INBTRecipe;
import com.rogoshum.magickcore.entity.PlaceableItemEntity;
import com.rogoshum.magickcore.event.magickevent.LivingLootsEvent;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.lib.LibMagickCraftingRecipes;
import com.rogoshum.magickcore.lib.LibRegistry;
import com.rogoshum.magickcore.magick.context.child.*;
import com.rogoshum.magickcore.recipes.SpawnContext;
import com.rogoshum.magickcore.registry.ObjectRegistry;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import com.rogoshum.magickcore.tool.MultiBlockHelper;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.recipes.*;
import com.rogoshum.magickcore.recipes.recipe.ElementItemRecipes;
import com.rogoshum.magickcore.recipes.recipe.ElementToolRecipes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Callable;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = MagickCore.MOD_ID)
public class ModRecipes {
    //Explosion Recipes(easy recipe)
    private static final HashMap<IItemContainer, ItemStack> ExplosionRecipesMap = new HashMap<>();

    public static void registerExplosionRecipe(IItemContainer input, ItemStack output) {
        if(!ExplosionRecipesMap.containsKey(input))
            ExplosionRecipesMap.put(input, output);
        else try {
            throw new Exception("Containing same input on the map = [" + input.toString() +"]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ItemStack findExplosionOutput(ItemStack stack) {
        for(IItemContainer container : ExplosionRecipesMap.keySet()) {
            if(container.matches(stack)) {
                return ExplosionRecipesMap.get(container);
            }
        }

        return ItemStack.EMPTY;
    }

    //Event registry MagickLogicEvent::onExplosion

    //////////////////////////////
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

    public static final INBTRecipe elementOrbTag = CopyTagContainer.create(element_crystal_seeds, NBTRecipeContainer.ItemContainer.create(orb_bottle, "ELEMENT"), NBTRecipeContainer.ItemContainer.create("seed")).shapeless();
    public static final SpecialRecipeSerializer<?> element_orb_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementOrbTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_orb_recipe;
        }
    }).setRegistryName("element_orb_recipe");

    public static final INBTRecipe elementWoolTag = CopyTagContainer.create(element_wool, NBTRecipeContainer.ItemContainer.create(orb_bottle, "ELEMENT"), NBTRecipeContainer.ItemContainer.create("wool")).shapeless();
    public static final SpecialRecipeSerializer<?> element_wool_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(elementWoolTag, r){
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return element_wool_recipe;
        }
    }).setRegistryName("element_wool_recipe");

    public static final INBTRecipe elementStringTag = CopyTagContainer.create(element_string, 4, NBTRecipeContainer.ItemContainer.create("element_wool", "ELEMENT")).shapeless();
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

    public static final SpecialRecipeSerializer<?> context_tool_recipe = (SpecialRecipeSerializer<?>)new SpecialRecipeSerializer<>(ManaItemContextRecipe::new).setRegistryName("context_tool_recipe");

    public static final MultiBlockHelper.PredicatePattern<PlaceableItemEntity> EMPTY = new MultiBlockHelper.PredicatePattern<PlaceableItemEntity>("",
            Objects::isNull);

    public static final MultiBlockHelper.PredicatePattern<PlaceableItemEntity> SPIRIT_WOOD_STICK = new MultiBlockHelper.PredicatePattern<PlaceableItemEntity>("sws",
            (type) -> type != null && type.getItemStack() != null && type.getItemStack().getItem() == ModItems.spirit_wood_stick.get());

    public static final MultiBlockHelper.PredicatePattern<PlaceableItemEntity> SPIRIT_CRYSTAL = new MultiBlockHelper.PredicatePattern<PlaceableItemEntity>("sc",
            (type) -> type != null && type.getItemStack() != null && type.getItemStack().getItem() == ModItems.spirit_crystal.get());

    public static void init()
    {
        TagMatchItemContainer arcContainer = TagMatchItemContainer.create(element_crystal, getStringTagMap("ELEMENT", LibElements.ARC));
        arc_element_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CopyTagContainer.create(arc, arcContainer, arcContainer, arcContainer, arcContainer).shapeless(), r){
            @Override
            public IRecipeSerializer<?> getSerializer() {
                return arc_element_recipe;
            }
        }).setRegistryName("arc_element_recipe");

        TagMatchItemContainer solarContainer = TagMatchItemContainer.create(element_crystal, getStringTagMap("ELEMENT", LibElements.SOLAR));
        solar_element_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CopyTagContainer.create(solar, solarContainer, solarContainer, solarContainer, solarContainer).shapeless(), r){
            @Override
            public IRecipeSerializer<?> getSerializer() {
                return solar_element_recipe;
            }
        }).setRegistryName("solar_element_recipe");

        TagMatchItemContainer voidContainer = TagMatchItemContainer.create(element_crystal, getStringTagMap("ELEMENT", LibElements.VOID));
        void_element_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CopyTagContainer.create(voidItem, voidContainer, voidContainer, voidContainer, voidContainer).shapeless(), r){
            @Override
            public IRecipeSerializer<?> getSerializer() {
                return void_element_recipe;
            }
        }).setRegistryName("void_element_recipe");

        TagMatchItemContainer stasisContainer = TagMatchItemContainer.create(element_crystal, getStringTagMap("ELEMENT", LibElements.STASIS));
        stasis_element_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CopyTagContainer.create(stasis, stasisContainer, stasisContainer, stasisContainer, stasisContainer).shapeless(), r){
            @Override
            public IRecipeSerializer<?> getSerializer() {
                return stasis_element_recipe;
            }
        }).setRegistryName("stasis_element_recipe");

        TagMatchItemContainer witherContainer = TagMatchItemContainer.create(element_crystal, getStringTagMap("ELEMENT", LibElements.WITHER));
        wither_element_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CopyTagContainer.create(wither, witherContainer, witherContainer, witherContainer, witherContainer).shapeless(), r){
            @Override
            public IRecipeSerializer<?> getSerializer() {
                return wither_element_recipe;
            }
        }).setRegistryName("wither_element_recipe");

        TagMatchItemContainer takenContainer = TagMatchItemContainer.create(element_crystal, getStringTagMap("ELEMENT", LibElements.TAKEN));
        taken_element_recipe = (SpecialRecipeSerializer<?>) new SpecialRecipeSerializer<>((r) -> new NBTRecipe(CopyTagContainer.create(taken, takenContainer, takenContainer, takenContainer, takenContainer).shapeless(), r){
            @Override
            public IRecipeSerializer<?> getSerializer() {
                return taken_element_recipe;
            }
        }).setRegistryName("taken_element_recipe");

        ItemStack manaEnergy = new ItemStack(ModItems.MANA_ENERGY.get());
        ItemStack rangeEnergy = manaEnergy.copy();
        ExtraDataHelper.itemManaData(rangeEnergy, (data) -> data.spellContext().range(0.5f));

        ItemStack forceEnergy = manaEnergy.copy();
        ExtraDataHelper.itemManaData(forceEnergy, (data) -> data.spellContext().force(0.5f));

        ItemStack tickEnergy = manaEnergy.copy();
        ExtraDataHelper.itemManaData(tickEnergy, (data) -> data.spellContext().tick(20));
        registerExplosionRecipe(TagMatchItemContainer.create(Items.DRAGON_BREATH.toString()), new ItemStack(ModItems.mana_dragon_breath.get()));
        registerExplosionRecipe(TagMatchItemContainer.create(Items.GLOWSTONE_DUST.toString()), forceEnergy);
        registerExplosionRecipe(TagMatchItemContainer.create(Items.BLAZE_ROD.toString()), rangeEnergy);
        registerExplosionRecipe(TagMatchItemContainer.create(Items.GUNPOWDER.toString()), new ItemStack(ModItems.mana_gunpowder.get()));
        registerExplosionRecipe(TagMatchItemContainer.create(Items.REDSTONE.toString()), tickEnergy);
        registerExplosionRecipe(TagMatchItemContainer.create(Items.SPIDER_EYE.toString()), new ItemStack(ModItems.mana_spider_eye.get()));
        registerExplosionRecipe(TagMatchItemContainer.create(Items.FERMENTED_SPIDER_EYE.toString()), new ItemStack(ModItems.mana_spider_eye.get()));
        registerExplosionRecipe(TagMatchItemContainer.create(Items.NETHER_WART.toString()), new ItemStack(ModItems.mana_nether_wart.get()));
        registerExplosionRecipe(TagMatchItemContainer.create(Items.QUARTZ.toString()), NBTTagHelper.setElement(new ItemStack(ModItems.element_crystal.get()), LibElements.ORIGIN));

        registerExplosionRecipe(TagMatchItemContainer.create(ModItems.solar.get().toString()), NBTTagHelper.setElement(new ItemStack(ModItems.orb_bottle.get()), LibElements.SOLAR));
        registerExplosionRecipe(TagMatchItemContainer.create(ModItems.arc.get().toString()), NBTTagHelper.setElement(new ItemStack(ModItems.orb_bottle.get()), LibElements.ARC));
        registerExplosionRecipe(TagMatchItemContainer.create(ModItems.voidE.get().toString()), NBTTagHelper.setElement(new ItemStack(ModItems.orb_bottle.get()), LibElements.VOID));

        registerExplosionRecipe(TagMatchItemContainer.create(ModItems.stasis.get().toString()), NBTTagHelper.setElement(new ItemStack(ModItems.orb_bottle.get()), LibElements.STASIS));
        registerExplosionRecipe(TagMatchItemContainer.create(ModItems.wither.get().toString()), NBTTagHelper.setElement(new ItemStack(ModItems.orb_bottle.get()), LibElements.WITHER));
        registerExplosionRecipe(TagMatchItemContainer.create(ModItems.taken.get().toString()), NBTTagHelper.setElement(new ItemStack(ModItems.orb_bottle.get()), LibElements.TAKEN));
        registerExplosionRecipe(TagMatchItemContainer.create(Items.NETHER_STAR.toString()), new ItemStack(ModItems.nether_star_material.get()));

        ObjectRegistry<MagickCraftingRecipe> magickCrafting = new ObjectRegistry<>(LibRegistry.MAGICK_CRAFTING);
        String[][][] recipe = new String[][][]
                {
                    {
                        {"", "", ""},
                        {"", "sws", ""},
                        {"", "", ""}
                    },
                    {
                        {"", "sc", ""},
                        {"sc", "sws", "sc"},
                        {"", "sc", ""}
                    },
                    {
                        {"", "", ""},
                        {"", "sc", ""},
                        {"", "", ""}
                    }
                };
        MultiBlockHelper.PredicatePattern<PlaceableItemEntity>[] pattern = new MultiBlockHelper.PredicatePattern[3];

        pattern[0] = EMPTY;
        pattern[1] = SPIRIT_WOOD_STICK;
        pattern[2] = SPIRIT_CRYSTAL;
        magickCrafting.register(LibMagickCraftingRecipes.WAND, new MagickCraftingRecipe(recipe, pattern, SpawnResult.create((spawnContext) -> {
            ItemEntity itemEntity = new ItemEntity(spawnContext.living.world, spawnContext.vec.x, spawnContext.vec.y, spawnContext.vec.z, new ItemStack(ModItems.STAFF.get()));
            spawnContext.living.world.addEntity(itemEntity);
        })));

        recipe = new String[][][]
                {
                        {
                                {"", "sc", ""},
                                {"sws", "sws", "sws"},
                                {"", "sc", ""}
                        },
                        {
                                {"", "sws", ""},
                                {"", "sws", ""},
                                {"", "sws", ""}
                        },
                        {
                                {"", "", ""},
                                {"", "", ""},
                                {"", "sc", ""}
                        }
                };
        magickCrafting.register("diamond", new MagickCraftingRecipe(recipe, pattern, SpawnResult.create((spawnContext) -> {
            ItemEntity itemEntity = new ItemEntity(spawnContext.living.world, spawnContext.vec.x, spawnContext.vec.y, spawnContext.vec.z, new ItemStack(Items.DIAMOND));
            spawnContext.living.world.addEntity(itemEntity);
        })));

        recipe = new String[][][]
                {
                        {
                                {"sc", "sc"},
                                {"sc", ""},
                                {"", "sc"}
                        },
                        {
                                {"sc", " "},
                                {"", "sws"},
                                {"", "sc"}
                        },
                        {
                                {"", ""},
                                {"", "sc"},
                                {"", ""}
                        }
                };
        magickCrafting.register("sword", new MagickCraftingRecipe(recipe, pattern, SpawnResult.create((spawnContext) -> {
            ItemEntity itemEntity = new ItemEntity(spawnContext.living.world, spawnContext.vec.x, spawnContext.vec.y, spawnContext.vec.z, new ItemStack(Items.DIAMOND_SWORD));
            spawnContext.living.world.addEntity(itemEntity);
        })));

        Item book = ForgeRegistries.ITEMS.getValue(new ResourceLocation("patchouli:guide_book"));
        if(book != null) {
            ItemStack stack = new ItemStack(book);
            NBTTagHelper.getStackTag(stack).putString("patchouli:book", "magickcore:magickcore");
            registerExplosionRecipe(TagMatchItemContainer.create(Items.BOOK.toString()), stack);
        }
    }

    @SubscribeEvent
    public static void registerRecipes(final RegistryEvent.Register<IRecipeSerializer<?>> event)
    {
        init();
        LivingLootsEvent.init();
        event.getRegistry().registerAll(
                context_tool_recipe,
                element_orb_recipe,
                arc_element_recipe,
                solar_element_recipe,
                void_element_recipe,
                stasis_element_recipe,
                wither_element_recipe,
                taken_element_recipe,
                element_wool_recipe,
                element_string_recipe,
                ElementToolRecipes.element_any_recipe,
                ElementItemRecipes.recipe_0,
                ElementItemRecipes.recipe_1,
                ElementItemRecipes.recipe_2,
                ElementItemRecipes.recipe_3,
                ElementItemRecipes.recipe_4,
                ElementItemRecipes.recipe_5,
                ElementItemRecipes.recipe_6,
                ElementItemRecipes.recipe_7,
                ElementItemRecipes.recipe_8,
                ElementItemRecipes.recipe_9
        );
    }

    public static HashMap<String, INBT> getStringTagMap(String key, String value)
    {
        HashMap<String, INBT> hashMap = new HashMap<String, INBT>();
        hashMap.put(key, StringNBT.valueOf(value));
        return hashMap;
    }
}
