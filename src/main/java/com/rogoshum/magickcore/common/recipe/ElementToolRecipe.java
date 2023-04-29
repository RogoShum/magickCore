package com.rogoshum.magickcore.common.recipe;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.item.ElementStringItem;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class ElementToolRecipe extends CustomRecipe {
    private final RecipeSerializer<?> SERIALIZER;

    public ElementToolRecipe(ResourceLocation idIn) {
        super(new ResourceLocation(MagickCore.MOD_ID, idIn.getPath()));
        SERIALIZER = new SimpleRecipeSerializer<>(res -> this);
        SERIALIZER.setRegistryName(new ResourceLocation(MagickCore.MOD_ID, idIn.getPath()));
    }

    public static boolean isTool(ItemStack itemStack) {
        return itemStack.isDamageableItem() || itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE).size() > 0 || Mob.getEquipmentSlotForItem(itemStack).getType().equals(EquipmentSlot.Type.ARMOR);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        ItemStack stack = ItemStack.EMPTY;
        String element = null;
        int count = 0;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack itemStack = inv.getItem(i);
            if(isTool(itemStack))
                stack = itemStack.copy();
            else if(itemStack.getItem() instanceof ElementStringItem && NBTTagHelper.hasElement(itemStack)) {
                String e = NBTTagHelper.getElement(itemStack);
                if(element == null) {
                    element = e;
                }
                if(element.equals(e))
                    count++;
                else
                    return false;
            }

            if(NBTTagHelper.getToolElementTable(stack).getAllKeys().size() >= 2)
                return false;
        }

        return !stack.isEmpty() && count == 3;
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack stack = ItemStack.EMPTY;
        String element = LibElements.ORIGIN;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack itemStack = inv.getItem(i);
            if(isTool(itemStack))
                stack = itemStack.copy();
            else if(itemStack.getItem() instanceof ElementStringItem && NBTTagHelper.hasElement(itemStack)) {
                element = NBTTagHelper.getElement(itemStack);
            }

            if(NBTTagHelper.getToolElementTable(stack).getAllKeys().size() >= 2)
                return ItemStack.EMPTY;
        }

        NBTTagHelper.putElementOnTool(stack, element);
        return stack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)  {
        return width * height >= 4;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }
}
