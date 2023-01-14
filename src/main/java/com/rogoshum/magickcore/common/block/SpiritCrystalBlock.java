package com.rogoshum.magickcore.common.block;

import com.rogoshum.magickcore.common.tileentity.SpiritCrystalTileEntity;
import com.rogoshum.magickcore.common.init.ModBlocks;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.BlockMatcher;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.AbstractBlock.Properties;

public class SpiritCrystalBlock extends BaseBlock {
    protected static final VoxelShape SHAPE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 6.0D, 10.0D);
    public final BlockPattern MAGICK_CRAFTING =
            BlockPatternBuilder.start().aisle("sas", "aaa", "sas")
                    .where('s', CachedBlockInfo.hasState(BlockMatcher.forBlock(this)))
                    .where('a', CachedBlockInfo.hasState((state -> state.getBlock() instanceof AirBlock || state.getBlock() instanceof MagickCraftingBlock)))
                    .build();

    public SpiritCrystalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SpiritCrystalTileEntity();
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        BlockPattern.PatternHelper patternHelper = MAGICK_CRAFTING.find(worldIn, pos);
        if(patternHelper != null) {
            BlockPos target = patternHelper.getBlock(1, 1, 0).getPos();
            worldIn.setBlockAndUpdate(target, ModBlocks.MAGICK_CRAFTING.get().defaultBlockState());
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return super.getDrops(state, builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }
}
