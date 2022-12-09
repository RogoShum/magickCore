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

public class MaterialJarBlock extends BaseBlock{
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(3.0D, 0.0D, 5.5D, 13.0D, 16.0D, 13.0D);
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
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(worldIn.isRemote) return ActionResultType.SUCCESS;
        MaterialJarTileEntity jar = (MaterialJarTileEntity) worldIn.getTileEntity(pos);

        if (player.getHeldItemMainhand().isEmpty()) {
            if(player.isSneaking())
                player.entityDropItem(jar.takeStack(Math.min(jar.getCount(), player.getHeldItemMainhand().getMaxStackSize())));
            else
                player.entityDropItem(jar.takeStack(1));
            return ActionResultType.SUCCESS;
        }

        if (handIn == Hand.MAIN_HAND) {

            if(player.getHeldItemMainhand().getItem() instanceof IManaMaterial) {
                jar.putStack(player.getHeldItemMainhand());
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (!player.isCreative() && tileentity instanceof MaterialJarTileEntity) {
            MaterialJarTileEntity tile = (MaterialJarTileEntity)tileentity;
            tile.dropItem();
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }
}
