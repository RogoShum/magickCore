package com.rogoshum.magickcore.common.recipes.container;

import com.google.gson.JsonObject;
import com.rogoshum.magickcore.common.api.IItemContainer;
import com.rogoshum.magickcore.common.api.INBTRecipe;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.lib.LibElements;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class ElementOnToolContainer extends NBTRecipeContainer {
    public static ElementOnToolContainer create(IItemContainer... containers) {
        return new ElementOnToolContainer(containers);
    }

    protected ElementOnToolContainer(IItemContainer... containers) {
        super(containers);

    }

    @Override
    public ItemStack getResultStack(CraftingInventory inv) {
        ItemStack stack = ItemStack.EMPTY;
        String element = LibElements.ORIGIN;

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemStack = inv.getStackInSlot(i);
            if(itemStack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE).size() > 0 || MobEntity.getSlotForItemStack(itemStack).getSlotType().equals(EquipmentSlotType.Group.ARMOR))
                stack = itemStack.copy();
            else if(NBTTagHelper.hasElement(itemStack)) {
                element = NBTTagHelper.getElement(itemStack);
            }

            if(NBTTagHelper.getToolElementTable(stack).keySet().size() >= 2)
                return ItemStack.EMPTY;
        }

        NBTTagHelper.putElementOnTool(stack, element);
        return stack;
    }

    @Override
    public INBTRecipe read(JsonObject json) {
        return null;
    }

    @Override
    public INBTRecipe read(PacketBuffer buffer) {
        return null;
    }

    @Override
    public void write(PacketBuffer buffer, INBTRecipe recipe) {

    }
}
