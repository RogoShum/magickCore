package com.rogoshum.magickcore.mixin.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.client.tileentity.*;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class MixinBlockRender {

    @Shadow protected abstract <E extends BlockEntity> void register(BlockEntityType<E> blockEntityType, BlockEntityRenderer<E> blockEntityRenderer);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstruct(CallbackInfo ci) {
        this.register(ModTileEntities.MAGICK_CRAFTING_TILE_ENTITY.get(), MagickCraftingRenderer::new);
        this.register(ModTileEntities.SPIRIT_CRYSTAL_TILE_ENTITY.get(), SpiritCrystalRenderer::new);
        this.register(ModTileEntities.MATERIAL_JAR_TILE_ENTITY.get(), MaterialJarRenderer::new);
        this.register(ModTileEntities.ELEMENT_CRYSTAL_TILE_ENTITY.get(), ElementCrystalRenderer::new);
        this.register(ModTileEntities.ITEM_EXTRACTOR_TILE_ENTITY.get(), ItemExtractorRenderer::new);
        this.register(ModTileEntities.ELEMENT_WOOL_TILE_ENTITY.get(), ElementWoolRenderer::new);
    }

    protected <E extends BlockEntity> void register(BlockEntityType<E> blockEntityType, Function<BlockEntityRenderDispatcher, BlockEntityRenderer<E>> blockEntityRenderer) {
        this.register(blockEntityType, blockEntityRenderer.apply((BlockEntityRenderDispatcher)(Object)this));
    }
}
