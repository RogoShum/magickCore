package com.rogoshum.magickcore.block;

import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.block.tileentity.MagickContainerTileEntity;
import com.rogoshum.magickcore.block.tileentity.MagickCraftingTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class MagickContainerBlock extends BaseBlock {
    public MagickContainerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new MagickContainerTileEntity();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(worldIn.isRemote) return ActionResultType.SUCCESS;

        MagickContainerTileEntity magickCrafting = (MagickContainerTileEntity) worldIn.getTileEntity(pos);

        if (player.isSneaking()) {
            magickCrafting.setPlayerUniqueId(player.getUniqueID());
            if (magickCrafting.getPlayerUniqueId().equals(player.getUniqueID()))
                magickCrafting.enableTrans();
        }
        else if(handIn == Hand.MAIN_HAND){
            if(player.getHeldItemMainhand() != null && !player.getHeldItemMainhand().isEmpty())
            {
                if(magickCrafting.putMaterialItem(player.getHeldItemMainhand().copy()))
                    player.getHeldItemMainhand().setCount(0);
            }
            else if(magickCrafting.getMaterialItem() != null && magickCrafting.getMaterialItem() != ItemStack.EMPTY)
            {
                player.inventory.addItemStackToInventory(magickCrafting.getMaterialItem());
                magickCrafting.clearMaterialItem();
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof ILightSourceEntity){
            return (int) ((ILightSourceEntity) tile).getSourceLight();
        }
        return 0;
    }
}
