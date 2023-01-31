package com.rogoshum.magickcore.common.block;

import com.rogoshum.magickcore.common.tileentity.SpiritCrystalTileEntity;
import com.rogoshum.magickcore.common.init.ModBlocks;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;
import java.util.List;

public class SpiritCrystalBlock extends BaseBlock implements EntityBlock {
    protected static final VoxelShape SHAPE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 6.0D, 10.0D);
    public final BlockPattern MAGICK_CRAFTING =
            BlockPatternBuilder.start().aisle("sas", "aaa", "sas")
                    .where('s', BlockInWorld.hasState(BlockStatePredicate.forBlock(this)))
                    .where('a', BlockInWorld.hasState((state -> state.getBlock() instanceof AirBlock || state.getBlock() instanceof MagickCraftingBlock)))
                    .build();

    public SpiritCrystalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        BlockPattern.BlockPatternMatch patternHelper = MAGICK_CRAFTING.find(worldIn, pos);
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
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockGetter blockGetter) {
        return new SpiritCrystalTileEntity();
    }
}
