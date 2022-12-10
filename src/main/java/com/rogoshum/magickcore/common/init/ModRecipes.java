package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IItemContainer;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.lib.LibConditions;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.recipe.*;
import com.rogoshum.magickcore.common.recipe.container.*;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.registry.ObjectRegistry;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.event.magickevent.LivingLootsEvent;
import com.rogoshum.magickcore.common.lib.LibMagickCraftingRecipes;
import com.rogoshum.magickcore.common.util.MultiBlockUtil;
import com.rogoshum.magickcore.common.lib.LibElements;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = MagickCore.MOD_ID)
public class ModRecipes {
    //Explosion Recipes(easy recipe)
    private static final HashMap<IItemContainer, ItemStack> ExplosionRecipesMap = new HashMap<>();
    private static final HashMap<ResourceLocation, IRecipe<?>> RECIPES = new HashMap<>();
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

    public static void registerRecipe(IRecipe<?> recipe) {
        RECIPES.put(recipe.getId(), recipe);
    }

    public static HashMap<ResourceLocation, IRecipe<?>> getRecipes() {
        return new HashMap<>(RECIPES);
    }

    //Event registry MagickLogicEvent::onExplosion

    //////////////////////////////
    public static final String ELEMENT_CRYSTAL_SEEDS = MagickCore.MOD_ID + ":element_crystal_seeds";
    public static final String ELEMENT_WOOL = MagickCore.MOD_ID + ":element_wool";
    public static final String ELEMENT_STRING = MagickCore.MOD_ID + ":element_string";
    public static final String MINECRAFT_STRING = "string";
    public static final String ORB_BOTTLE = MagickCore.MOD_ID + ":orb_bottle";
    public static final String ELEMENT_CRYSTAL = MagickCore.MOD_ID + ":element_crystal";
    public static final String ARC = MagickCore.MOD_ID + ":arc";
    public static final String VOID_ITEM = MagickCore.MOD_ID + ":void";
    public static final String SOLAR = MagickCore.MOD_ID + ":solar";
    public static final String STASIS = MagickCore.MOD_ID + ":stasis";
    public static final String WITHER = MagickCore.MOD_ID + ":wither";
    public static final String TAKEN = MagickCore.MOD_ID + ":taken";

    public static final ManaItemContextRecipe MANA_ITEM_CONTEXT_RECIPE = new ManaItemContextRecipe(new ResourceLocation("context_tool_recipe"));
    public static final ManaItemMaterialRecipe MANA_ITEM_MATERIAL_RECIPE = new ManaItemMaterialRecipe(new ResourceLocation("mana_item_material_recipe"));
    public static final NBTRecipe ELEMENT_ORB_RECIPE = new NBTRecipe(CopyTagContainer.create(ELEMENT_CRYSTAL_SEEDS, NBTRecipeContainer.ItemMatcher.create(ORB_BOTTLE, "ELEMENT"), NBTRecipeContainer.ItemMatcher.create("seed")).shapeless(), new ResourceLocation("element_orb_recipe"));
    public static final NBTRecipe ELEMENT_WOOL_RECIPE = new NBTRecipe(CopyTagContainer.create(ELEMENT_WOOL, NBTRecipeContainer.ItemMatcher.create(ORB_BOTTLE, "ELEMENT"), NBTRecipeContainer.ItemMatcher.create("wool")).shapeless(), new ResourceLocation("element_wool_recipe"));
    public static final NBTRecipe ELEMENT_WOOL_RECIPE_2 = new NBTRecipe(CopyTagContainer.create(ELEMENT_WOOL, NBTRecipeContainer.ItemMatcher.create(ELEMENT_CRYSTAL, "ELEMENT"), NBTRecipeContainer.ItemMatcher.create("wool")).shapeless(), new ResourceLocation("element_wool_recipe_2"));
    public static final NBTRecipe ELEMENT_STRING_RECIPE = new NBTRecipe(CopyTagContainer.create(ELEMENT_STRING, 4, NBTRecipeContainer.ItemMatcher.create("element_wool", "ELEMENT")).shapeless(), new ResourceLocation("element_string_recipe"));
    public static final NBTRecipe ELEMENT_STRING_RECIPE_2 = new NBTRecipe(CopyTagContainer.create(ELEMENT_STRING, NBTRecipeContainer.ItemMatcher.create(ORB_BOTTLE, "ELEMENT"), NBTRecipeContainer.ItemMatcher.create(MINECRAFT_STRING)).shapeless(), new ResourceLocation("element_string_recipe_2"));

    public static final NBTRecipe ELEMENT_STRING_RECIPE_3 = new NBTRecipe(CopyTagContainer.create(ELEMENT_STRING, NBTRecipeContainer.ItemMatcher.create(ELEMENT_CRYSTAL, "ELEMENT"), NBTRecipeContainer.ItemMatcher.create(MINECRAFT_STRING)).shapeless(), new ResourceLocation("element_string_recipe_3"));
    public static final IItemContainer STRING = NBTRecipeContainer.ItemMatcher.create("element_string", "ELEMENT");
    public static final NBTRecipe ELEMENT_TOOL = new NBTRecipe(ElementOnToolContainer.create(NBTRecipeContainer.ItemMatcher.create(":"), STRING, STRING, STRING).shapeless(), new ResourceLocation("element_any_recipe"));

    public static final MultiBlockUtil.PlaceableEntityPattern EMPTY = new MultiBlockUtil.PlaceableEntityPattern("", Items.AIR);

    public static final IItemContainer CRYSTAL_CONTAINER = TagItemMatcher.create(ELEMENT_CRYSTAL, ModRecipes.getStringTagMap("ELEMENT", LibElements.ORIGIN));
    public static final IItemContainer EMPTY_CONTAINER = NBTRecipeContainer.ItemMatcher.create("minecraft:air");

    public static NBTRecipe recipe_9 = new NBTRecipe(CreateStackContainer.create(ORB_BOTTLE,
            EMPTY_CONTAINER, EMPTY_CONTAINER, EMPTY_CONTAINER
            , CRYSTAL_CONTAINER, EMPTY_CONTAINER, CRYSTAL_CONTAINER
            , EMPTY_CONTAINER, CRYSTAL_CONTAINER, EMPTY_CONTAINER), new ResourceLocation("orb_bottle_recipe"));

    public static void registerMagickRecipes() {
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
        MultiBlockUtil.PlaceableEntityPattern[] pattern = new MultiBlockUtil.PlaceableEntityPattern[3];
        MultiBlockUtil.PlaceableEntityPattern SPIRIT_WOOD_STICK = new MultiBlockUtil.PlaceableEntityPattern("sws", ModItems.SPIRIT_WOOD_STICK.get());
        MultiBlockUtil.PlaceableEntityPattern SPIRIT_CRYSTAL = new MultiBlockUtil.PlaceableEntityPattern("sc", ModItems.SPIRIT_CRYSTAL.get());
        pattern[0] = EMPTY;
        pattern[1] = SPIRIT_WOOD_STICK;
        pattern[2] = SPIRIT_CRYSTAL;
        magickCrafting.register(LibMagickCraftingRecipes.CRYSTAL_STAFF, new MagickCraftingRecipe(recipe, pattern, SpawnResult.create((spawnContext) -> {
            ItemEntity itemEntity = new ItemEntity(spawnContext.living.world, spawnContext.vec.x, spawnContext.vec.y, spawnContext.vec.z, new ItemStack(ModItems.SPIRIT_CRYSTAL_STAFF.get()));
            spawnContext.living.world.addEntity(itemEntity);
        })));

        recipe = new String[][][]
                {
                        {
                                {"sws"}
                        },
                        {
                                {"sws"}
                        },
                        {
                                {"sws"}
                        }
                };
        magickCrafting.register(LibMagickCraftingRecipes.WOOD_STAFF, new MagickCraftingRecipe(recipe, pattern, SpawnResult.create((spawnContext) -> {
            ItemEntity itemEntity = new ItemEntity(spawnContext.living.world, spawnContext.vec.x, spawnContext.vec.y, spawnContext.vec.z, new ItemStack(ModItems.STAFF.get()));
            spawnContext.living.world.addEntity(itemEntity);
        })));

        recipe = new String[][][]
                {
                        {
                                {"sws"}
                        },
                        {
                                {"sws"}
                        },
                        {
                                {"sc"}
                        }
                };
        magickCrafting.register(LibMagickCraftingRecipes.SWORD, new MagickCraftingRecipe(recipe, pattern, SpawnResult.create((spawnContext) -> {
            ItemEntity itemEntity = new ItemEntity(spawnContext.living.world, spawnContext.vec.x, spawnContext.vec.y, spawnContext.vec.z, new ItemStack(ModItems.SPIRIT_SWORD.get()));
            spawnContext.living.world.addEntity(itemEntity);
        })));

        recipe = new String[][][]
                {
                        {
                                {"", "sc", "sws"}
                        },
                        {
                                {"sc", "", "sws"}
                        },
                        {
                                {"", "sc", "sws"}
                        }
                };
        magickCrafting.register(LibMagickCraftingRecipes.BOW, new MagickCraftingRecipe(recipe, pattern, SpawnResult.create((spawnContext) -> {
            ItemEntity itemEntity = new ItemEntity(spawnContext.living.world, spawnContext.vec.x, spawnContext.vec.y, spawnContext.vec.z, new ItemStack(ModItems.SPIRIT_BOW.get()));
            spawnContext.living.world.addEntity(itemEntity);
        })));

        recipe = new String[][][]
                {
                        {
                                {"sc", "sc"},
                                {"sc", "sc"}
                        },
                        {
                                {"sc", "sc"},
                                {"sc", "sc"}
                        }
                };
        magickCrafting.register(LibMagickCraftingRecipes.JAR, new MagickCraftingRecipe(recipe, pattern, SpawnResult.create((spawnContext) -> {
            ItemEntity itemEntity = new ItemEntity(spawnContext.living.world, spawnContext.vec.x, spawnContext.vec.y, spawnContext.vec.z, new ItemStack(ModItems.MATERIAL_JAR.get()));
            spawnContext.living.world.addEntity(itemEntity);
        })));

        recipe = new String[][][]
                {
                        {
                                {"", "sc", ""},
                                {"sc", "sc", "sc"},
                                {"", "sc", ""}
                        }
                };
        magickCrafting.register(LibMagickCraftingRecipes.SUPER, new MagickCraftingRecipe(recipe, pattern, SpawnResult.create((spawnContext) -> {
            ItemEntity itemEntity = new ItemEntity(spawnContext.living.world, spawnContext.vec.x, spawnContext.vec.y, spawnContext.vec.z, new ItemStack(ModItems.SUPER_SPAWNER.get()));
            spawnContext.living.world.addEntity(itemEntity);
        })));

        recipe = new String[][][]
                {
                        {
                                {"sc", "sc", "sc"},
                                {"sc", "sc", "sc"},
                                {"sc", "sc", "sc"}
                        },
                        {
                                {"sc", "sc", "sc"},
                                {"sc", "", "sc"},
                                {"sc", "sc", "sc"}
                        },
                        {
                                {"sc", "sc", "sc"},
                                {"sc", "sc", "sc"},
                                {"sc", "sc", "sc"}
                        }
                };
        magickCrafting.register(LibMagickCraftingRecipes.CAPACITY, new MagickCraftingRecipe(recipe, pattern, SpawnResult.create((spawnContext) -> {
            ItemEntity itemEntity = new ItemEntity(spawnContext.living.world, spawnContext.vec.x, spawnContext.vec.y, spawnContext.vec.z, new ItemStack(ModItems.MAGICK_CONTAINER.get()));
            spawnContext.living.world.addEntity(itemEntity);
        })));

        recipe = new String[][][]
                {
                        {
                                {"sws", "sc"},
                                {"sc", "sws"}
                        },
                        {
                                {"sc", "sws"},
                                {"sws", "sc"}
                        }
                };
        magickCrafting.register(LibMagickCraftingRecipes.CONTEXT, new MagickCraftingRecipe(recipe, pattern, SpawnResult.create((spawnContext) -> {
            ItemEntity itemEntity = new ItemEntity(spawnContext.living.world, spawnContext.vec.x, spawnContext.vec.y, spawnContext.vec.z, new ItemStack(ModItems.CONTEXT_CORE.get()));
            spawnContext.living.world.addEntity(itemEntity);
        })));

        recipe = new String[][][]
                {
                        {
                                {"sc", "sc"},
                                {"sc", "sc"}
                        },
                        {
                                {"sws", "sws"},
                                {"sws", "sws"}
                        },
                        {
                                {"sc", "sc"},
                                {"sc", "sc"}
                        }
                };
        magickCrafting.register(LibMagickCraftingRecipes.POINTER, new MagickCraftingRecipe(recipe, pattern, SpawnResult.create((spawnContext) -> {
            ItemEntity itemEntity = new ItemEntity(spawnContext.living.world, spawnContext.vec.x, spawnContext.vec.y, spawnContext.vec.z, new ItemStack(ModItems.CONTEXT_POINTER.get()));
            spawnContext.living.world.addEntity(itemEntity);
        })));
    }

    public static void registerExplosionRecipes() {
        ItemStack manaEnergy = new ItemStack(ModItems.MANA_ENERGY.get());

        ItemStack rangeEnergy = manaEnergy.copy();
        ExtraDataUtil.itemManaData(rangeEnergy, (data) -> data.spellContext().range(0.5f));

        ItemStack rangeEnergy_1 = manaEnergy.copy();
        ExtraDataUtil.itemManaData(rangeEnergy_1, (data) -> data.spellContext().range(1.0f));

        ItemStack forceEnergy = manaEnergy.copy();
        ExtraDataUtil.itemManaData(forceEnergy, (data) -> data.spellContext().force(0.5f));

        ItemStack tickEnergy = manaEnergy.copy();
        ExtraDataUtil.itemManaData(tickEnergy, (data) -> data.spellContext().tick(10));

        ItemStack condition_block = new ItemStack(ModItems.CONDITION_BLOCK.get());
        SpellContext spellContext = ExtraDataUtil.itemManaData(condition_block).spellContext();
        ConditionContext context = ConditionContext.create(MagickRegistry.getCondition(LibConditions.BLOCK_ONLY));
        spellContext.addChild(context);

        ItemStack condition_living = new ItemStack(ModItems.CONDITION_LIVING.get());
        spellContext = ExtraDataUtil.itemManaData(condition_living).spellContext();
        context = ConditionContext.create(MagickRegistry.getCondition(LibConditions.LIVING_ENTITY));
        spellContext.addChild(context);

        ItemStack condition_non_living = new ItemStack(ModItems.CONDITION_NON_LIVING.get());
        spellContext = ExtraDataUtil.itemManaData(condition_non_living).spellContext();
        context = ConditionContext.create(MagickRegistry.getCondition(LibConditions.NON_LIVING_ENTITY));
        spellContext.addChild(context);

        registerExplosionRecipe(TagItemMatcher.create(Items.FEATHER.toString()), condition_living);
        registerExplosionRecipe(TagItemMatcher.create(Items.COAL.toString()), condition_non_living);
        registerExplosionRecipe(TagItemMatcher.create(Items.COBBLESTONE.toString()), condition_block);
        registerExplosionRecipe(TagItemMatcher.create(Items.ENDER_PEARL.toString()), new ItemStack(ModItems.POSITION_MEMORY.get()));
        registerExplosionRecipe(TagItemMatcher.create(Items.GOLD_INGOT.toString()), new ItemStack(ModItems.DIRECTION_MEMORY.get()));
        registerExplosionRecipe(TagItemMatcher.create(Items.IRON_INGOT.toString()), new ItemStack(ModItems.OFFSET_MEMORY.get()));
        registerExplosionRecipe(TagItemMatcher.create(Items.DIAMOND.toString()), new ItemStack(ModItems.COMPLETELY_SELF.get()));
        registerExplosionRecipe(TagItemMatcher.create(Items.GOLD_NUGGET.toString()), new ItemStack(ModItems.REVERSE.get()));
        registerExplosionRecipe(TagItemMatcher.create(Items.BONE.toString()), new ItemStack(ModItems.MANA_BONE.get()));
        registerExplosionRecipe(TagItemMatcher.create(Items.ROTTEN_FLESH.toString()), new ItemStack(ModItems.MANA_FLESH.get()));
        registerExplosionRecipe(TagItemMatcher.create(Items.DRAGON_BREATH.toString()), new ItemStack(ModItems.MANA_DRAGON_BREATH.get()));
        registerExplosionRecipe(TagItemMatcher.create(Items.GLOWSTONE_DUST.toString()), forceEnergy);
        registerExplosionRecipe(TagItemMatcher.create(Items.BLAZE_ROD.toString()), rangeEnergy_1);
        registerExplosionRecipe(TagItemMatcher.create(Items.BLAZE_POWDER.toString()), rangeEnergy);
        registerExplosionRecipe(TagItemMatcher.create(Items.GUNPOWDER.toString()), new ItemStack(ModItems.MANA_GUNPOWDER.get()));
        registerExplosionRecipe(TagItemMatcher.create(Items.REDSTONE.toString()), tickEnergy);
        registerExplosionRecipe(TagItemMatcher.create(Items.SPIDER_EYE.toString()), new ItemStack(ModItems.MANA_SPIDER_EYE.get()));
        registerExplosionRecipe(TagItemMatcher.create(Items.FERMENTED_SPIDER_EYE.toString()), new ItemStack(ModItems.MANA_SPIDER_EYE.get()));
        registerExplosionRecipe(TagItemMatcher.create(Items.NETHER_WART.toString()), new ItemStack(ModItems.MANA_NETHER_WART.get()));
        registerExplosionRecipe(TagItemMatcher.create(Items.STICK.toString()), new ItemStack(ModItems.SPIRIT_WOOD_STICK.get()));
        registerExplosionRecipe(TagItemMatcher.create(Items.QUARTZ.toString()), NBTTagHelper.setElement(new ItemStack(ModItems.ELEMENT_CRYSTAL.get()), LibElements.ORIGIN));

        registerExplosionRecipe(TagItemMatcher.create(ModItems.SOLAR.get().toString()), NBTTagHelper.setElement(new ItemStack(ModItems.ORB_BOTTLE.get()), LibElements.SOLAR));
        registerExplosionRecipe(TagItemMatcher.create(ModItems.ARC.get().toString()), NBTTagHelper.setElement(new ItemStack(ModItems.ORB_BOTTLE.get()), LibElements.ARC));
        registerExplosionRecipe(TagItemMatcher.create(ModItems.VOID.get().toString()), NBTTagHelper.setElement(new ItemStack(ModItems.ORB_BOTTLE.get()), LibElements.VOID));

        registerExplosionRecipe(TagItemMatcher.create(ModItems.STASIS.get().toString()), NBTTagHelper.setElement(new ItemStack(ModItems.ORB_BOTTLE.get()), LibElements.STASIS));
        registerExplosionRecipe(TagItemMatcher.create(ModItems.WITHER.get().toString()), NBTTagHelper.setElement(new ItemStack(ModItems.ORB_BOTTLE.get()), LibElements.WITHER));
        registerExplosionRecipe(TagItemMatcher.create(ModItems.TAKEN.get().toString()), NBTTagHelper.setElement(new ItemStack(ModItems.ORB_BOTTLE.get()), LibElements.TAKEN));
        registerExplosionRecipe(TagItemMatcher.create(Items.NETHER_STAR.toString()), new ItemStack(ModItems.NETHER_STAR_MATERIAL.get()));

        Item book = ForgeRegistries.ITEMS.getValue(new ResourceLocation("patchouli:guide_book"));
        if(book != null) {
            ItemStack stack = new ItemStack(book);
            NBTTagHelper.getStackTag(stack).putString("patchouli:book", "magickcore:magickcore");
            registerExplosionRecipe(TagItemMatcher.create(Items.BOOK.toString()), stack);
        }
    }

    public static void init() {
        LivingLootsEvent.init();
        registerRecipe(MANA_ITEM_CONTEXT_RECIPE);
        registerRecipe(MANA_ITEM_MATERIAL_RECIPE);
        registerRecipe(ELEMENT_ORB_RECIPE);
        registerRecipe(ELEMENT_WOOL_RECIPE);
        registerRecipe(ELEMENT_WOOL_RECIPE_2);
        registerRecipe(ELEMENT_STRING_RECIPE);
        registerRecipe(ELEMENT_STRING_RECIPE_2);
        registerRecipe(ELEMENT_STRING_RECIPE_3);
        registerRecipe(recipe_9);
        registerRecipe(ELEMENT_TOOL);

        TagItemMatcher arcContainer = TagItemMatcher.create(ELEMENT_CRYSTAL, getStringTagMap("ELEMENT", LibElements.ARC));
        registerRecipe(new NBTRecipe(CopyTagContainer.create(ARC, arcContainer, arcContainer, arcContainer, arcContainer).shapeless(), new ResourceLocation("arc_element_recipe")));

        TagItemMatcher solarContainer = TagItemMatcher.create(ELEMENT_CRYSTAL, getStringTagMap("ELEMENT", LibElements.SOLAR));
        registerRecipe(new NBTRecipe(CopyTagContainer.create(SOLAR, solarContainer, solarContainer, solarContainer, solarContainer).shapeless(), new ResourceLocation("solar_element_recipe")));

        TagItemMatcher voidContainer = TagItemMatcher.create(ELEMENT_CRYSTAL, getStringTagMap("ELEMENT", LibElements.VOID));
        registerRecipe(new NBTRecipe(CopyTagContainer.create(VOID_ITEM, voidContainer, voidContainer, voidContainer, voidContainer).shapeless(), new ResourceLocation("void_element_recipe")));

        TagItemMatcher stasisContainer = TagItemMatcher.create(ELEMENT_CRYSTAL, getStringTagMap("ELEMENT", LibElements.STASIS));
        registerRecipe(new NBTRecipe(CopyTagContainer.create(STASIS, stasisContainer, stasisContainer, stasisContainer, stasisContainer).shapeless(), new ResourceLocation("stasis_element_recipe")));

        TagItemMatcher witherContainer = TagItemMatcher.create(ELEMENT_CRYSTAL, getStringTagMap("ELEMENT", LibElements.WITHER));
        registerRecipe(new NBTRecipe(CopyTagContainer.create(WITHER, witherContainer, witherContainer, witherContainer, witherContainer).shapeless(), new ResourceLocation("wither_element_recipe")));

        TagItemMatcher takenContainer = TagItemMatcher.create(ELEMENT_CRYSTAL, getStringTagMap("ELEMENT", LibElements.TAKEN));
        registerRecipe(new NBTRecipe(CopyTagContainer.create(TAKEN, takenContainer, takenContainer, takenContainer, takenContainer).shapeless(), new ResourceLocation("taken_element_recipe")));
    }

    @SubscribeEvent
    public static void registerRecipes(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
        init();
        getRecipes().forEach((r, recipe) -> event.getRegistry().register(recipe.getSerializer()));
    }

    public static HashMap<String, INBT> getStringTagMap(String key, String value) {
        HashMap<String, INBT> hashMap = new HashMap<String, INBT>();
        hashMap.put(key, StringNBT.valueOf(value));
        return hashMap;
    }
}
