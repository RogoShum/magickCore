package com.rogoshum.magickcore.common.recipe;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IMaterialLimit;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.common.init.ManaMaterials;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.item.ContextCoreItem;
import com.rogoshum.magickcore.common.item.ElementStringItem;
import com.rogoshum.magickcore.common.item.material.ManaMaterialItem;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.lib.LibMaterial;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.materials.Material;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ElementToolRecipe extends SpecialRecipe {
    private final IRecipeSerializer<?> SERIALIZER;

    public ElementToolRecipe(ResourceLocation idIn) {
        super(new ResourceLocation(MagickCore.MOD_ID, idIn.getPath()));
        SERIALIZER = new SpecialRecipeSerializer<>(res -> this);
        SERIALIZER.setRegistryName(new ResourceLocation(MagickCore.MOD_ID, idIn.getPath()));
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        ItemStack stack = ItemStack.EMPTY;
        String element = null;
        int count = 0;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack itemStack = inv.getItem(i);
            if(itemStack.isDamageableItem() || itemStack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE).size() > 0 || MobEntity.getEquipmentSlotForItem(itemStack).getType().equals(EquipmentSlotType.Group.ARMOR))
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
    public ItemStack assemble(CraftingInventory inv) {
        ItemStack stack = ItemStack.EMPTY;
        String element = LibElements.ORIGIN;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack itemStack = inv.getItem(i);
            if(itemStack.isDamageableItem() || itemStack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE).size() > 0 || MobEntity.getEquipmentSlotForItem(itemStack).getType().equals(EquipmentSlotType.Group.ARMOR))
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
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return IRecipeType.CRAFTING;
    }
}
