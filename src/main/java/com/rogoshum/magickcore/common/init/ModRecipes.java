package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IItemContainer;
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
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = MagickCore.MOD_ID)
public class ModRecipes {
    //Explosion Recipes(easy recipe)
    private static final HashMap<String, MagickCraftingTransformRecipe> ExplosionRecipesMap = new HashMap<>();

    public static void registerExplosionRecipe(IItemContainer input, ItemStack output){
        String name = output.getItem().getRegistryName().toString();
        if(!ExplosionRecipesMap.containsKey(name))
            ExplosionRecipesMap.put(name, new MagickCraftingTransformRecipe(name, input, output));
        else try {
            throw new Exception("Containing same input on the map = [" + input.toString() +"]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void registerExplosionRecipe(String name, IItemContainer input, ItemStack output){
        if(!ExplosionRecipesMap.containsKey(name))
            ExplosionRecipesMap.put(name, new MagickCraftingTransformRecipe(name, input, output));
        else try {
            throw new Exception("Containing same input on the map = [" + input.toString() +"]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, MagickCraftingTransformRecipe> getExplosionRecipes() {
        return ExplosionRecipesMap;
    }

    //Event registry MagickLogicEvent::onExplosion

    //////////////////////////////
    public static final IRecipeSerializer<?> NBT_RECIPE = NBTRecipe.Serializer.INSTANCE;
    public static final IRecipeSerializer<?> MAGICK_WORKBENCH_RECIPE = MagickWorkbenchRecipe.Serializer.INSTANCE;
    public static final ManaItemContextRecipe MANA_ITEM_CONTEXT_RECIPE = new ManaItemContextRecipe(new ResourceLocation("context_tool_recipe"));
    public static final BlockConditionRecipe BLOCK_CONDITION_RECIPE = new BlockConditionRecipe(new ResourceLocation("block_condition_recipe"));
    public static final ManaItemMaterialRecipe MANA_ITEM_MATERIAL_RECIPE = new ManaItemMaterialRecipe(new ResourceLocation("mana_item_material_recipe"));
    public static final ElementToolRecipe ELEMENT_TOOL_RECIPE = new ElementToolRecipe(new ResourceLocation("element_tool_recipe"));

    public static final MultiBlockUtil.PlaceableEntityPattern EMPTY = new MultiBlockUtil.PlaceableEntityPattern("", Items.AIR);

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
    }

    public static void init() {
        LivingLootsEvent.init();
    }

    @SubscribeEvent
    public static void registerRecipes(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
        init();
        event.getRegistry().register(NBT_RECIPE);
        event.getRegistry().register(MAGICK_WORKBENCH_RECIPE);
        event.getRegistry().register(MANA_ITEM_CONTEXT_RECIPE.getSerializer());
        event.getRegistry().register(BLOCK_CONDITION_RECIPE.getSerializer());
        event.getRegistry().register(MANA_ITEM_MATERIAL_RECIPE.getSerializer());
        event.getRegistry().register(ELEMENT_TOOL_RECIPE.getSerializer());
        CraftingHelper.register(new ResourceLocation(MagickCore.MOD_ID, "nbt"), NBTIngredient.Serializer.INSTANCE);
    }

    public static HashMap<String, INBT> getStringTagMap(String key, String value) {
        HashMap<String, INBT> hashMap = new HashMap<String, INBT>();
        hashMap.put(key, StringNBT.valueOf(value));
        return hashMap;
    }
}
