package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.block.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MagickCore.MOD_ID);
    public static RegistryObject<Block> magick_crafting = BLOCKS.register("magick_crafting", () -> new MagickCraftingBlock(AbstractBlock.Properties.create(Material.AIR).notSolid().hardnessAndResistance(3)));
    public static RegistryObject<Block> magick_container = BLOCKS.register("magick_container", () -> new MagickContainerBlock(AbstractBlock.Properties.create(Material.ICE).notSolid().hardnessAndResistance(3)));
    public static RegistryObject<Block> element_crystal = BLOCKS.register("element_crystal", () -> new ElementCrystalBlock(AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().zeroHardnessAndResistance().sound(SoundType.GLASS).notSolid()));
    public static RegistryObject<Block> element_wool = BLOCKS.register("element_wool", () -> new ElementWoolBlock(AbstractBlock.Properties.create(Material.WOOL).hardnessAndResistance(1f).tickRandomly()));
    public static RegistryObject<Block> magick_barrier = BLOCKS.register("magick_barrier", () -> new MagickBarrierBlock(AbstractBlock.Properties.create(Material.ICE).doesNotBlockMovement().hardnessAndResistance(0.3f)));
    public static RegistryObject<Block> magick_supplier = BLOCKS.register("magick_supplier", () -> new MagickSupplierBlock(AbstractBlock.Properties.create(Material.WOOD).notSolid().hardnessAndResistance(1f)));
    public static RegistryObject<Block> magick_repeater = BLOCKS.register("magick_repeater", () -> new MagickRepeaterBlock(AbstractBlock.Properties.create(Material.WOOD).notSolid().hardnessAndResistance(1f)));
    public static RegistryObject<Block> void_sphere = BLOCKS.register("void_sphere", () -> new VoidSphereBlock(AbstractBlock.Properties.create(Material.ICE).doesNotBlockMovement().hardnessAndResistance(0.3f)));
    public static RegistryObject<SpiritCrystalBlock> spirit_crystal = BLOCKS.register("spirit_crystal", () -> new SpiritCrystalBlock(AbstractBlock.Properties.create(Material.ICE).notSolid().hardnessAndResistance(3f)));
    public static RegistryObject<Block> birch_planks = BLOCKS.register("spirit_planks", () -> new Block(AbstractBlock.Properties.create(Material.SNOW, MaterialColor.ICE).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static RegistryObject<Block> spirit_log = BLOCKS.register("spirit_log", () -> createLogBlock(MaterialColor.ICE, MaterialColor.SNOW));
    public static RegistryObject<Block> stripped_spirit_log = BLOCKS.register("stripped_spirit_log", () -> createLogBlock(MaterialColor.ICE, MaterialColor.ICE));
    public static RegistryObject<Block> spirit_wood = BLOCKS.register("spirit_wood", () -> new RotatedPillarBlock(AbstractBlock.Properties.create(Material.SNOW, MaterialColor.ICE).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
    public static RegistryObject<Block> stripped_spirit_wood = BLOCKS.register("stripped_spirit_wood", () -> new RotatedPillarBlock(AbstractBlock.Properties.create(Material.SNOW, MaterialColor.ICE).hardnessAndResistance(2.0F).sound(SoundType.WOOD)));
    public static RegistryObject<Block> spirit_leaves = BLOCKS.register("spirit_leaves", ModBlocks::createLeavesBlock);
    public static RegistryObject<FakeAirBlock> fake_air = BLOCKS.register("fake_air", () -> new FakeAirBlock(Blocks.AIR.getDefaultState(), AbstractBlock.Properties.from(Blocks.AIR)));
    public static RegistryObject<FakeAirBlock> fake_cave_air = BLOCKS.register("fake_cave_air", () -> new FakeAirBlock(Blocks.CAVE_AIR.getDefaultState(), AbstractBlock.Properties.from(Blocks.CAVE_AIR)));
    public static RegistryObject<FakeFluidBlock> fake_water = BLOCKS.register("fake_water", () -> new FakeFluidBlock(Fluids.WATER, AbstractBlock.Properties.from(Blocks.WATER).tickRandomly()));

    private static RotatedPillarBlock createLogBlock(MaterialColor topColor, MaterialColor barkColor) {
        return new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD, (state) -> {
            return state.get(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? topColor : barkColor;
        }).hardnessAndResistance(2.0F).sound(SoundType.WOOD));
    }

    private static LeavesBlock createLeavesBlock() {
        return new LeavesBlock(AbstractBlock.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT).notSolid().setAllowsSpawn(ModBlocks::allowsSpawnOnLeaves).setSuffocates(ModBlocks::isntSolid).setBlocksVision(ModBlocks::isntSolid));
    }
    private static Boolean allowsSpawnOnLeaves(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> entity) {
        return entity == EntityType.OCELOT || entity == EntityType.PARROT;
    }

    private static boolean isntSolid(BlockState state, IBlockReader reader, BlockPos pos) {
        return false;
    }
}
