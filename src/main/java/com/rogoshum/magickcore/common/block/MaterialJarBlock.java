package com.rogoshum.magickcore.common.block;

import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.common.tileentity.MaterialJarTileEntity;
import net.minecraft.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

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
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if(worldIn.isClientSide) return InteractionResult.SUCCESS;
        MaterialJarTileEntity jar = (MaterialJarTileEntity) worldIn.getBlockEntity(pos);

        if (player.getMainHandItem().isEmpty()) {
            if(player.isShiftKeyDown())
                player.spawnAtLocation(jar.takeStack(Math.min(jar.getCount(), player.getMainHandItem().getMaxStackSize())));
            else
                player.spawnAtLocation(jar.takeStack(1));
            return InteractionResult.SUCCESS;
        }

        if (handIn == InteractionHand.MAIN_HAND) {

            if(player.getMainHandItem().getItem() instanceof IManaMaterial) {
                jar.putStack(player.getMainHandItem());
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        TileEntity tileentity = worldIn.getBlockEntity(pos);
        if (!player.isCreative() && tileentity instanceof MaterialJarTileEntity) {
            MaterialJarTileEntity tile = (MaterialJarTileEntity)tileentity;
            tile.dropItem();
        }

        super.playerWillDestroy(worldIn, pos, state, player);
    }
}
