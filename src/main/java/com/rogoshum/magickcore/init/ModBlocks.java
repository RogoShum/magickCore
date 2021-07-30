package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.block.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MagickCore.MOD_ID);
    public static RegistryObject<Block> magick_crafting = BLOCKS.register("magick_crafting", () -> new MagickCraftingBlock(AbstractBlock.Properties.create(Material.ICE).notSolid().hardnessAndResistance(3)));
    public static RegistryObject<Block> magick_container = BLOCKS.register("magick_container", () -> new MagickContainerBlock(AbstractBlock.Properties.create(Material.ICE).notSolid().hardnessAndResistance(3)));
    public static RegistryObject<Block> element_crystal = BLOCKS.register("element_crystal", () -> new ElementCrystalBlock(AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().zeroHardnessAndResistance().sound(SoundType.GLASS).notSolid()));
    public static RegistryObject<Block> element_wool = BLOCKS.register("element_wool", () -> new ElementWoolBlock(AbstractBlock.Properties.create(Material.WOOL).hardnessAndResistance(1.2f).tickRandomly()));
    public static RegistryObject<Block> magick_barrier = BLOCKS.register("magick_barrier", () -> new MagickBarrierBlock(AbstractBlock.Properties.create(Material.ICE).notSolid().hardnessAndResistance(3)));
}
