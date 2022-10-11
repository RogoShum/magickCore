package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.client.item.MagickCraftingItemStackTileEntityRenderer;
import com.rogoshum.magickcore.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.world.server.ServerWorld;

public class MagickCraftingItem extends BlockItem {
    public MagickCraftingItem() {
        super(ModBlocks.magick_crafting.get(), BaseItem.properties().maxStackSize(1).setISTER(() -> MagickCraftingItemStackTileEntityRenderer::new));
    }
}
