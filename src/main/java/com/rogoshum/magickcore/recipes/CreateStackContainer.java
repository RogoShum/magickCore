package com.rogoshum.magickcore.recipes;

import com.rogoshum.magickcore.api.IItemContainer;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class CreateStackContainer extends NBTRecipeContainer {
    private String item;

    public static CreateStackContainer create(String item, IItemContainer... containers)
    {
        return new CreateStackContainer(item, containers);
    }

    private CreateStackContainer(String item, IItemContainer... containers)
    {
        super(containers);
        this.item = item;
    }
    @Override
    public ItemStack getResultStack(CraftingInventory inv) {
        return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item)));
    }
}
