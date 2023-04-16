package com.rogoshum.magickcore.common.block;

import com.rogoshum.magickcore.common.init.ModTileEntities;
import com.rogoshum.magickcore.common.tileentity.ElementCrystalTileEntity;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.tileentity.ElementWoolTileEntity;
import com.rogoshum.magickcore.common.tileentity.GlowAirTileEntity;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class ElementCrystalBlock extends CropBlock implements EntityBlock {
    public ElementCrystalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForAge(int age) {
        return super.getStateForAge(age);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ElementCrystalTileEntity(pos, state);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.ELEMENT_CRYSTAL_SEEDS.get();
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModTileEntities.ELEMENT_CRYSTAL_TILE_ENTITY.get(), ElementCrystalTileEntity::tick);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> p_152133_, BlockEntityType<E> p_152134_, BlockEntityTicker<? super E> p_152135_) {
        return p_152134_ == p_152133_ ? (BlockEntityTicker<A>)p_152135_ : null;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 10;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        Vec3 pos = builder.getOptionalParameter(LootContextParams.ORIGIN);
        if(pos == null) return super.getDrops(state, builder);
        BlockEntity tileentity = builder.getLevel().getBlockEntity(new BlockPos(pos));
        if (tileentity instanceof ElementCrystalTileEntity) {
            ElementCrystalTileEntity tile = (ElementCrystalTileEntity)tileentity;
            return tile.getDrops(getAge(state));
        }
        return super.getDrops(state, builder);
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof ElementCrystalTileEntity) {
            ElementCrystalTileEntity tile = (ElementCrystalTileEntity)tileentity;
            tile.dropItem();
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if(this.isMaxAge(state)) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof ElementCrystalTileEntity tile) {
                tile.dropItem();
            }
            worldIn.setBlockAndUpdate(pos, defaultBlockState());
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }
}
