package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.block.*;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MagickCore.MOD_ID);
    public static RegistryObject<Block> MAGICK_CRAFTING = BLOCKS.register("magick_crafting", () -> new MagickCraftingBlock(BlockBehaviour.Properties.of(Material.AIR).noOcclusion().strength(3)));
    public static RegistryObject<Block> ELEMENT_CRYSTAL = BLOCKS.register("element_crystal", () -> new ElementCrystalBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GLASS).noOcclusion()));
    public static RegistryObject<Block> ELEMENT_WOOL = BLOCKS.register("element_wool", () -> new ElementWoolBlock(BlockBehaviour.Properties.of(Material.WOOL).sound(SoundType.WOOL).strength(1f).randomTicks()));
    public static RegistryObject<SpiritCrystalBlock> SPIRIT_CRYSTAL = BLOCKS.register("spirit_crystal", () -> new SpiritCrystalBlock(BlockBehaviour.Properties.of(Material.ICE).sound(SoundType.GLASS).noOcclusion().strength(3f)));
    public static RegistryObject<Block> MATERIAL_JAR = BLOCKS.register("material_jar", () -> new MaterialJarBlock(BlockBehaviour.Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(2).noOcclusion()));
    public static RegistryObject<FakeAirBlock> FAKE_AIR = BLOCKS.register("fake_air", () -> new FakeAirBlock(Blocks.AIR.defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.AIR)));
    public static RegistryObject<FakeAirBlock> FAKE_CAVE_AIR = BLOCKS.register("fake_cave_air", () -> new FakeAirBlock(Blocks.CAVE_AIR.defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.CAVE_AIR)));
    public static RegistryObject<FakeFluidBlock> FAKE_WATER = BLOCKS.register("fake_water", () -> new FakeFluidBlock(Fluids.WATER, BlockBehaviour.Properties.copy(Blocks.WATER).randomTicks()));
    public static RegistryObject<Block> DEEP_SPIRIT_ORE = BLOCKS.register("deepslate_spirit_ore", () -> new OreBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(2.0F, 3.0F)));

    public static RegistryObject<Block> SPIRIT_ORE = BLOCKS.register("spirit_ore", () -> new OreBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(2.0F, 3.0F)));
    public static RegistryObject<ItemExtractorBlock> ITEM_EXTRACTOR = BLOCKS.register("item_extractor", () -> new ItemExtractorBlock(BlockBehaviour.Properties.of(Material.GLASS).noCollission().noOcclusion().strength(2)));
    public static RegistryObject<Block> RADIANCE_CRYSTAL = BLOCKS.register("radiance_crystal", () -> new RadianceCrystalBlock(BlockBehaviour.Properties.of(Material.GLASS).noCollission().randomTicks().instabreak().sound(SoundType.GLASS).noOcclusion()));
    public static RegistryObject<Block> DIMENSION_INFLATE = BLOCKS.register("dimension_inflate", () -> new DimensionInflateBlock(BlockBehaviour.Properties.of(Material.STONE).strength(2).sound(SoundType.STONE).noOcclusion()));
}
