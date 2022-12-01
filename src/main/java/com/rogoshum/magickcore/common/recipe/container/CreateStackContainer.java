package com.rogoshum.magickcore.common.recipe.container;

import com.google.gson.JsonObject;
import com.rogoshum.magickcore.api.IItemContainer;
import com.rogoshum.magickcore.api.INBTRecipe;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class CreateStackContainer extends NBTRecipeContainer {
    private final String item;

    public static CreateStackContainer create(String item, IItemContainer... containers) {
        return new CreateStackContainer(item, containers);
    }

    private CreateStackContainer(String item, IItemContainer... containers) {
        super(containers);
        this.item = item;
    }
    @Override
    public ItemStack getResultStack(CraftingInventory inv) {
        return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item)));
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
