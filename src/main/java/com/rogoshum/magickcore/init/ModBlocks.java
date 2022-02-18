package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.block.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MagickCore.MOD_ID);
    public static RegistryObject<Block> magick_crafting = BLOCKS.register("magick_crafting", () -> new MagickCraftingBlock(AbstractBlock.Properties.create(Material.ICE).notSolid().hardnessAndResistance(3)));
    public static RegistryObject<Block> magick_container = BLOCKS.register("magick_container", () -> new MagickContainerBlock(AbstractBlock.Properties.create(Material.ICE).notSolid().hardnessAndResistance(3)));
    public static RegistryObject<Block> element_crystal = BLOCKS.register("element_crystal", () -> new ElementCrystalBlock(AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().zeroHardnessAndResistance().sound(SoundType.GLASS).notSolid()));
    public static RegistryObject<Block> element_wool = BLOCKS.register("element_wool", () -> new ElementWoolBlock(AbstractBlock.Properties.create(Material.WOOL).hardnessAndResistance(1f).tickRandomly()));
    public static RegistryObject<Block> magick_barrier = BLOCKS.register("magick_barrier", () -> new MagickBarrierBlock(AbstractBlock.Properties.create(Material.ICE).doesNotBlockMovement().hardnessAndResistance(0.3f)));
    public static RegistryObject<Block> magick_supplier = BLOCKS.register("magick_supplier", () -> new MagickSupplierBlock(AbstractBlock.Properties.create(Material.WOOD).notSolid().hardnessAndResistance(1f)));
    public static RegistryObject<Block> magick_repeater = BLOCKS.register("magick_repeater", () -> new MagickRepeaterBlock(AbstractBlock.Properties.create(Material.WOOD).notSolid().hardnessAndResistance(1f)));
    public static RegistryObject<Block> void_sphere = BLOCKS.register("void_sphere", () -> new VoidSphereBlock(AbstractBlock.Properties.create(Material.ICE).doesNotBlockMovement().hardnessAndResistance(0.3f)));

    public static RegistryObject<FakeAirBlock> fake_air = BLOCKS.register("fake_air", () -> new FakeAirBlock(Blocks.AIR.getDefaultState(), AbstractBlock.Properties.from(Blocks.AIR).tickRandomly()));
    public static RegistryObject<FakeAirBlock> fake_cave_air = BLOCKS.register("fake_cave_air", () -> new FakeAirBlock(Blocks.CAVE_AIR.getDefaultState(), AbstractBlock.Properties.from(Blocks.CAVE_AIR).tickRandomly()));
    public static RegistryObject<FakeFluidBlock> fake_water = BLOCKS.register("fake_water", () -> new FakeFluidBlock(Fluids.WATER, AbstractBlock.Properties.from(Blocks.WATER).tickRandomly()));
}
