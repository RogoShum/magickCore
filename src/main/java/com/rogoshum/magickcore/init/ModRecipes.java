package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.helper.NBTTagHelper;
import com.rogoshum.magickcore.item.ManaItem;
import com.rogoshum.magickcore.item.OrbBottleItem;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
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

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = MagickCore.MOD_ID)
public class ModRecipes {
    public static final SpecialRecipeSerializer<ElementOrbRecipe> elementOrb = (SpecialRecipeSerializer<ElementOrbRecipe>) new SpecialRecipeSerializer<>(ElementOrbRecipe::new).setRegistryName("element_orb_recipe");

    @SubscribeEvent
    public static void registerRecipes(final RegistryEvent.Register<IRecipeSerializer<?>> event)
    {
        event.getRegistry().registerAll(
                elementOrb
        );
    }

    public static class ElementOrbRecipe extends SpecialRecipe{

        public ElementOrbRecipe(ResourceLocation idIn) {
            super(idIn);
        }

        @Override
        public boolean matches(CraftingInventory inv, World worldIn) {
            ItemStack orb = null;
            ItemStack seed = null;

            for(int i = 0; i < inv.getSizeInventory(); ++i) {
                ItemStack stack = inv.getStackInSlot(i);
                if(!stack.isEmpty()) {
                    if (stack.getItem() instanceof OrbBottleItem) {
                        if (orb == null && NBTTagHelper.getStackTag(stack).contains("ELEMENT"))
                            orb = stack;
                        else
                            return false;
                    } else if (stack.getItem().getRegistryName().toString().contains("seeds")) {
                        if (seed == null)
                            seed = stack;
                        else
                            return false;
                    } else
                        return false;
                }
            }

            if(orb != null && seed != null)
                return true;
            return false;
        }

        @Override
        public ItemStack getCraftingResult(CraftingInventory inv) {
            ItemStack orb = null;
            for(int i = 0; i < inv.getSizeInventory(); ++i) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack.getItem() instanceof OrbBottleItem) {
                    if (orb == null)
                        orb = stack;
                    else
                        return ItemStack.EMPTY;
                }
            }
            if(orb != null && NBTTagHelper.getStackTag(orb).contains("ELEMENT")) {
                ItemStack stack = new ItemStack(ModItems.element_crystal_seeds.get());
                CompoundNBT tag = new CompoundNBT();
                tag.putString("ELEMENT", NBTTagHelper.getStackTag(orb).getString("ELEMENT"));
                stack.setTag(tag);
                return stack;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canFit(int width, int height) {
            return width * height >= 2;
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return elementOrb;
        }
    }
}
