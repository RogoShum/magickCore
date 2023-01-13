package com.rogoshum.magickcore.common.block;

import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.common.tileentity.MaterialJarTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

public class MaterialJarBlock extends BaseBlock{
    protected static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
    public MaterialJarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new MaterialJarTileEntity();
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(worldIn.isClientSide) return ActionResultType.SUCCESS;
        MaterialJarTileEntity jar = (MaterialJarTileEntity) worldIn.getBlockEntity(pos);

        if (player.getMainHandItem().isEmpty()) {
            if(player.isShiftKeyDown())
                player.spawnAtLocation(jar.takeStack(Math.min(jar.getCount(), player.getMainHandItem().getMaxStackSize())));
            else
                player.spawnAtLocation(jar.takeStack(1));
            return ActionResultType.SUCCESS;
        }

        if (handIn == Hand.MAIN_HAND) {

            if(player.getMainHandItem().getItem() instanceof IManaMaterial) {
                jar.putStack(player.getMainHandItem());
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity tileentity = worldIn.getBlockEntity(pos);
        if (!player.isCreative() && tileentity instanceof MaterialJarTileEntity) {
            MaterialJarTileEntity tile = (MaterialJarTileEntity)tileentity;
            tile.dropItem();
        }

        super.playerWillDestroy(worldIn, pos, state, player);
    }
}
