package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.client.item.SpiritCrystalItemRenderer;
import com.rogoshum.magickcore.init.ModBlocks;
import net.minecraft.item.BlockItem;

public class MagickCraftingItem extends BlockItem {
    public MagickCraftingItem() {
        super(ModBlocks.magick_crafting.get(), BaseItem.properties().maxStackSize(1));
    }
}
