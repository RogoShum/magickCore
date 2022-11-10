package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.common.init.ModBlocks;
import net.minecraft.item.BlockItem;

public class MagickCraftingItem extends BlockItem {
    public MagickCraftingItem() {
        super(ModBlocks.magick_crafting.get(), BaseItem.properties().maxStackSize(1));
    }
}
