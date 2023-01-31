package com.rogoshum.magickcore.common.block;

import com.rogoshum.magickcore.common.tileentity.ElementWoolTileEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Random;

public class ElementWoolBlock extends BaseBlock implements EntityBlock {
    public ElementWoolBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
        super.tick(state, worldIn, pos, rand);
    }

    @Override
    public int getLightBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return 10;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        Vec3 pos = builder.getOptionalParameter(LootContextParams.ORIGIN);
        if(pos == null) return super.getDrops(state, builder);
        BlockPos blockPos = new BlockPos(pos.x, pos.y, pos.z);
        BlockEntity tileentity = builder.getLevel().getBlockEntity(blockPos);
        if (tileentity instanceof ElementWoolTileEntity) {
            ElementWoolTileEntity tile = (ElementWoolTileEntity)tileentity;
            return tile.getDrops();
        }
        return super.getDrops(state, builder);
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (!player.isCreative() && tileentity instanceof ElementWoolTileEntity) {
            ElementWoolTileEntity tile = (ElementWoolTileEntity)tileentity;
            tile.dropItem();
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockGetter blockGetter) {
        return new ElementWoolTileEntity();
    }
}
