package com.rogoshum.magickcore.common.recipe;

import com.rogoshum.magickcore.api.IItemContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

public class MagickCraftingTransformRecipe {
    private final String id;
    private final IItemContainer container;
    private final ItemStack output;

    public MagickCraftingTransformRecipe(String id, IItemContainer container, ItemStack output) {
        this.id = id;
        this.container = container;
        this.output = output;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public IItemContainer getContainer() {
        return container;
    }

    public ResourceLocation getId() {
        return new ResourceLocation("magick_recipe",id.replace(":", "_"));
    }
}
