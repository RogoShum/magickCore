package com.rogoshum.magickcore.recipes;

import com.rogoshum.magickcore.api.IItemContainer;
import com.rogoshum.magickcore.helper.NBTTagHelper;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class ElementOnToolContainer extends NBTRecipeContainer{
    private final String item;
    private final int count;
    private boolean equip;

    public static ElementOnToolContainer create(String item, IItemContainer... containers)
    {
        return new ElementOnToolContainer(item, 1, containers);
    }

    public static ElementOnToolContainer create(String item, int count, IItemContainer... containers)
    {
        return new ElementOnToolContainer(item, count, containers);
    }

    public ElementOnToolContainer equip()
    {
        equip = true;
        return this;
    }

    protected ElementOnToolContainer(String item, int count, IItemContainer... containers)
    {
        super(containers);
        this.item = item;
        this.count = count;
    }

    @Override
    public ItemStack getResultStack(CraftingInventory inv) {
        ItemStack stack = ItemStack.EMPTY;
        String element = LibElements.ORIGIN;

        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack itemStack = inv.getStackInSlot(i);

            if(itemStack.getItem().getRegistryName().toString().contains(this.item) && (!equip || MobEntity.getSlotForItemStack(itemStack).getSlotType().equals(EquipmentSlotType.Group.ARMOR)))
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
}
