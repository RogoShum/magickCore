package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.block.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MagickCore.MOD_ID);
    public static RegistryObject<Block> magick_crafting = BLOCKS.register("magick_crafting", () -> new MagickCraftingBlock(AbstractBlock.Properties.create(Material.AIR).notSolid().hardnessAndResistance(3)));
    public static RegistryObject<Block> element_crystal = BLOCKS.register("element_crystal", () -> new ElementCrystalBlock(AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().zeroHardnessAndResistance().sound(SoundType.GLASS).notSolid()));
    public static RegistryObject<Block> element_wool = BLOCKS.register("element_wool", () -> new ElementWoolBlock(AbstractBlock.Properties.create(Material.WOOL).sound(SoundType.CLOTH).hardnessAndResistance(1f).tickRandomly()));
    public static RegistryObject<SpiritCrystalBlock> spirit_crystal = BLOCKS.register("spirit_crystal", () -> new SpiritCrystalBlock(AbstractBlock.Properties.create(Material.ICE).sound(SoundType.GLASS).notSolid().hardnessAndResistance(3f)));
    public static RegistryObject<Block> MATERIAL_JAR = BLOCKS.register("material_jar", () -> new MaterialJarBlock(AbstractBlock.Properties.create(Material.GLASS).sound(SoundType.GLASS).hardnessAndResistance(3).notSolid()));
    public static RegistryObject<FakeAirBlock> fake_air = BLOCKS.register("fake_air", () -> new FakeAirBlock(Blocks.AIR.getDefaultState(), AbstractBlock.Properties.from(Blocks.AIR)));
    public static RegistryObject<FakeAirBlock> fake_cave_air = BLOCKS.register("fake_cave_air", () -> new FakeAirBlock(Blocks.CAVE_AIR.getDefaultState(), AbstractBlock.Properties.from(Blocks.CAVE_AIR)));
    public static RegistryObject<FakeFluidBlock> fake_water = BLOCKS.register("fake_water", () -> new FakeFluidBlock(Fluids.WATER, AbstractBlock.Properties.from(Blocks.WATER).tickRandomly()));
    public static RegistryObject<Block> SPIRIT_ORE = BLOCKS.register("spirit_ore", () -> new OreBlock(AbstractBlock.Properties.create(Material.ROCK).setRequiresTool().hardnessAndResistance(2.0F, 3.0F)));
}
