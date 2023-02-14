package com.rogoshum.magickcore.common.block;

import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.common.tileentity.MaterialJarTileEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class MaterialJarBlock extends BaseBlock implements EntityBlock {
    protected static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
    public MaterialJarBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MaterialJarTileEntity(pos, state);
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
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (!player.isCreative() && tileentity instanceof MaterialJarTileEntity) {
            MaterialJarTileEntity tile = (MaterialJarTileEntity)tileentity;
            tile.dropItem();
        }

        super.playerWillDestroy(worldIn, pos, state, player);
    }
}
