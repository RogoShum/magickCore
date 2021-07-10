package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.block.tileentity.ElementCrystalTileEntity;
import com.rogoshum.magickcore.block.tileentity.ElementWoolTileEntity;
import com.rogoshum.magickcore.block.tileentity.MagickContainerTileEntity;
import com.rogoshum.magickcore.block.tileentity.MagickCraftingTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MagickCore.MOD_ID);
    public static RegistryObject<TileEntityType<MagickCraftingTileEntity>> magick_crafting_tileentity = TILE_ENTITY.register("magick_crafting_tileentity"
            , () -> TileEntityType.Builder.create(MagickCraftingTileEntity::new
                    , ModBlocks.magick_crafting.get()).build(null));

    public static RegistryObject<TileEntityType<MagickContainerTileEntity>> magick_container_tileentity = TILE_ENTITY.register("magick_container_tileentity"
            , () -> TileEntityType.Builder.create(MagickContainerTileEntity::new
                    , ModBlocks.magick_container.get()).build(null));

    public static RegistryObject<TileEntityType<ElementCrystalTileEntity>> element_crystal_tileentity = TILE_ENTITY.register("element_crystal_tileentity"
            , () -> TileEntityType.Builder.create(ElementCrystalTileEntity::new
                    , ModBlocks.element_crystal.get()).build(null));

    public static RegistryObject<TileEntityType<ElementWoolTileEntity>> element_wool_tileentity = TILE_ENTITY.register("element_wool"
            , () -> TileEntityType.Builder.create(ElementWoolTileEntity::new
                    , ModBlocks.element_wool.get()).build(null));
}
