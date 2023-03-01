package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.common.init.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockTagsProvider.class)
public abstract class MixinBlockTagsProvider extends TagsProvider<Block>{
    protected MixinBlockTagsProvider(DataGenerator p_126546_, Registry<Block> p_126547_) {
        super(p_126546_, p_126547_);
    }

    @Inject(method = "addTags", at = @At(value = "RETURN"))
    public void onAddTags(CallbackInfo ci) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.SPIRIT_ORE.get());
        this.tag(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.SPIRIT_ORE.get());
    }
}
