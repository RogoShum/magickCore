package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.tileentity.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MagickCore.MOD_ID);
    public static RegistryObject<TileEntityType<MagickCraftingTileEntity>> MAGICK_CRAFTING_TILE_ENTITY = TILE_ENTITY.register("magick_crafting_tile_entity"
            , () -> TileEntityType.Builder.of(MagickCraftingTileEntity::new
                    , ModBlocks.MAGICK_CRAFTING.get()).build(null));

    public static RegistryObject<TileEntityType<SpiritCrystalTileEntity>> SPIRIT_CRYSTAL_TILE_ENTITY = TILE_ENTITY.register("spirit_crystal_tile_entity"
            , () -> TileEntityType.Builder.of(SpiritCrystalTileEntity::new
                    , ModBlocks.SPIRIT_CRYSTAL.get()).build(null));
    public static RegistryObject<TileEntityType<ElementCrystalTileEntity>> ELEMENT_CRYSTAL_TILE_ENTITY = TILE_ENTITY.register("element_crystal_tile_entity"
            , () -> TileEntityType.Builder.of(ElementCrystalTileEntity::new
                    , ModBlocks.ELEMENT_CRYSTAL.get()).build(null));

    public static RegistryObject<TileEntityType<ElementWoolTileEntity>> ELEMENT_WOOL_TILE_ENTITY = TILE_ENTITY.register("element_wool_tile_entity"
            , () -> TileEntityType.Builder.of(ElementWoolTileEntity::new
                    , ModBlocks.ELEMENT_WOOL.get()).build(null));
    public static RegistryObject<TileEntityType<GlowAirTileEntity>> GLOW_AIR_TILE_ENTITY = TILE_ENTITY.register("glow_air_tile_entity"
            , () -> TileEntityType.Builder.of(GlowAirTileEntity::new
                    , ModBlocks.FAKE_AIR.get(), ModBlocks.FAKE_CAVE_AIR.get(), ModBlocks.FAKE_WATER.get()).build(null));

    public static RegistryObject<TileEntityType<MaterialJarTileEntity>> MATERIAL_JAR_TILE_ENTITY = TILE_ENTITY.register("material_jar_tile_entity"
            , () -> TileEntityType.Builder.of(MaterialJarTileEntity::new
                    , ModBlocks.MATERIAL_JAR.get()).build(null));

    public static RegistryObject<TileEntityType<ItemExtractorTileEntity>> ITEM_EXTRACTOR_TILE_ENTITY = TILE_ENTITY.register("item_extractor_tile_entity"
            , () -> TileEntityType.Builder.of(ItemExtractorTileEntity::new
                    , ModBlocks.ITEM_EXTRACTOR.get()).build(null));
}
