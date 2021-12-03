package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.client.item.MagickContainerItemStackTileEntityRenderer;
import com.rogoshum.magickcore.client.item.MagickCraftingItemStackTileEntityRenderer;
import com.rogoshum.magickcore.init.ModBlocks;
import net.minecraft.item.BlockItem;

public class MagickContainerItem extends BlockItem {
    public MagickContainerItem() {
        super(ModBlocks.magick_container.get(), BaseItem.properties().maxStackSize(1).setISTER(() -> MagickContainerItemStackTileEntityRenderer::new));
    }
}
