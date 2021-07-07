package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.block.MagickContainerBlock;
import com.rogoshum.magickcore.block.MagickCraftingBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MagickCore.MOD_ID);
    public static RegistryObject<Block> magick_crafting = BLOCKS.register("magick_crafting", () -> new MagickCraftingBlock(AbstractBlock.Properties.create(Material.ICE).notSolid()));
    public static RegistryObject<Block> magick_container = BLOCKS.register("magick_container", () -> new MagickContainerBlock(AbstractBlock.Properties.create(Material.ICE).notSolid()));

}
