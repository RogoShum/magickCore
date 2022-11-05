package com.rogoshum.magickcore.block;

import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.block.tileentity.MaterialJarTileEntity;
import com.rogoshum.magickcore.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.5D, 0.0D, 5.5D, 10.5D, 6.0D, 10.5D);
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
        if (tileentity instanceof MaterialJarTileEntity) {
            MaterialJarTileEntity tile = (MaterialJarTileEntity)tileentity;
            if (!worldIn.isRemote && tile.getCount() > 0 && !tile.getStack().isEmpty()) {
                ItemStack itemstack = new ItemStack(ModItems.MATERIAL_JAR.get());
                CompoundNBT compoundnbt = tile.write(new CompoundNBT());
                if (!compoundnbt.isEmpty()) {
                    itemstack.setTagInfo("BlockEntityTag", compoundnbt);
                }

                ItemEntity itementity = new ItemEntity(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
                itementity.setDefaultPickupDelay();
                worldIn.addEntity(itementity);
            }
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }
}