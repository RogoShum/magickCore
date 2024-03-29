package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.registry.DeferredRegister;
import com.rogoshum.magickcore.common.registry.RegistryObject;
import com.rogoshum.magickcore.common.tileentity.*;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModTileEntities {
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY = DeferredRegister.create(Registry.BLOCK_ENTITY_TYPE, MagickCore.MOD_ID);
    public static RegistryObject<BlockEntityType<MagickCraftingTileEntity>> MAGICK_CRAFTING_TILE_ENTITY = TILE_ENTITY.register("magick_crafting_tile_entity"
            , () -> BlockEntityType.Builder.of(MagickCraftingTileEntity::new
                    , ModBlocks.MAGICK_CRAFTING.get()).build(null));

    public static RegistryObject<BlockEntityType<SpiritCrystalTileEntity>> SPIRIT_CRYSTAL_TILE_ENTITY = TILE_ENTITY.register("spirit_crystal_tile_entity"
            , () -> BlockEntityType.Builder.of(SpiritCrystalTileEntity::new
                    , ModBlocks.SPIRIT_CRYSTAL.get()).build(null));
    public static RegistryObject<BlockEntityType<ElementCrystalTileEntity>> ELEMENT_CRYSTAL_TILE_ENTITY = TILE_ENTITY.register("element_crystal_tile_entity"
            , () -> BlockEntityType.Builder.of(ElementCrystalTileEntity::new
                    , ModBlocks.ELEMENT_CRYSTAL.get()).build(null));

    public static RegistryObject<BlockEntityType<ElementWoolTileEntity>> ELEMENT_WOOL_TILE_ENTITY = TILE_ENTITY.register("element_wool_tile_entity"
            , () -> BlockEntityType.Builder.of(ElementWoolTileEntity::new
                    , ModBlocks.ELEMENT_WOOL.get()).build(null));
    public static RegistryObject<BlockEntityType<GlowAirTileEntity>> GLOW_AIR_TILE_ENTITY = TILE_ENTITY.register("glow_air_tile_entity"
            , () -> BlockEntityType.Builder.of(GlowAirTileEntity::new
                    , ModBlocks.FAKE_AIR.get(), ModBlocks.FAKE_CAVE_AIR.get(), ModBlocks.FAKE_WATER.get()).build(null));

    public static RegistryObject<BlockEntityType<MaterialJarTileEntity>> MATERIAL_JAR_TILE_ENTITY = TILE_ENTITY.register("material_jar_tile_entity"
            , () -> BlockEntityType.Builder.of(MaterialJarTileEntity::new
                    , ModBlocks.MATERIAL_JAR.get()).build(null));

    public static RegistryObject<BlockEntityType<ItemExtractorTileEntity>> ITEM_EXTRACTOR_TILE_ENTITY = TILE_ENTITY.register("item_extractor_tile_entity"
            , () -> BlockEntityType.Builder.of(ItemExtractorTileEntity::new
                    , ModBlocks.ITEM_EXTRACTOR.get()).build(null));
}
