package com.rogoshum.magickcore.common.block;

import com.rogoshum.magickcore.common.tileentity.ElementWoolTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import net.minecraft.block.AbstractBlock.Properties;

public class ElementWoolBlock extends BaseBlock{
    public ElementWoolBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        super.tick(state, worldIn, pos, rand);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ElementWoolTileEntity();
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return 10;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        Vector3d pos = builder.getOptionalParameter(LootParameters.ORIGIN);
        if(pos == null) return super.getDrops(state, builder);
        BlockPos blockPos = new BlockPos(pos.x, pos.y, pos.z);
        TileEntity tileentity = builder.getLevel().getBlockEntity(blockPos);
        if (tileentity instanceof ElementWoolTileEntity) {
            ElementWoolTileEntity tile = (ElementWoolTileEntity)tileentity;
            return tile.getDrops();
        }
        return super.getDrops(state, builder);
    }

    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity tileentity = worldIn.getBlockEntity(pos);
        if (!player.isCreative() && tileentity instanceof ElementWoolTileEntity) {
            ElementWoolTileEntity tile = (ElementWoolTileEntity)tileentity;
            tile.dropItem();
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }
}
