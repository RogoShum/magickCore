package com.rogoshum.magickcore.common.block;

import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import com.rogoshum.magickcore.common.tileentity.DimensionInflateTileEntity;
import com.rogoshum.magickcore.common.tileentity.DimensionInflateTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DimensionInflateBlock extends BaseEntityBlock {
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);
    public DimensionInflateBlock(Properties p_49224_) {
        super(p_49224_);
    }

    public RenderShape getRenderShape(BlockState p_52986_) {
        return RenderShape.MODEL;
    }

    public VoxelShape getShape(BlockState p_52988_, BlockGetter p_52989_, BlockPos p_52990_, CollisionContext p_52991_) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new DimensionInflateTileEntity(p_153215_, p_153216_);
    }

    @Override
    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModTileEntities.DIMENSION_INFLATE_TILE_ENTITY.get(), DimensionInflateTileEntity::tick);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        Vec3 pos = builder.getOptionalParameter(LootContextParams.ORIGIN);
        if(pos == null) return super.getDrops(state, builder);
        BlockEntity tileentity = builder.getLevel().getBlockEntity(new BlockPos(pos));
        if (tileentity instanceof DimensionInflateTileEntity) {
            DimensionInflateTileEntity tile = (DimensionInflateTileEntity)tileentity;
            return tile.getDrops();
        }
        return super.getDrops(state, builder);
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof DimensionInflateTileEntity) {
            DimensionInflateTileEntity tile = (DimensionInflateTileEntity)tileentity;
            tile.dropItem();
            tile.dropThis();
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    public InteractionResult use(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {
        BlockEntity tile = p_60504_.getBlockEntity(p_60505_);
        if(p_60507_ == InteractionHand.MAIN_HAND && !p_60504_.isClientSide() && tile instanceof DimensionInflateTileEntity) {
            ItemStack stack = p_60506_.getItemInHand(p_60507_);
            ItemStack copy = stack.copy();
            copy.setCount(1);
            stack.shrink(1);
            ((DimensionInflateTileEntity) tile).setItemStack(copy);
            return InteractionResult.PASS;
        }
        return InteractionResult.CONSUME;
    }
}
