package com.rogoshum.magickcore.block;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaItem;
import com.rogoshum.magickcore.block.tileentity.MagickCraftingTileEntity;
import com.rogoshum.magickcore.entity.ManaItemEntity;
import com.rogoshum.magickcore.item.ManaItem;
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

public class MagickCraftingBlock extends BaseBlock{
    public MagickCraftingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new MagickCraftingTileEntity();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(worldIn.isRemote) return ActionResultType.SUCCESS;
        MagickCraftingTileEntity magickCrafting = (MagickCraftingTileEntity) worldIn.getTileEntity(pos);

        if (player.isSneaking()) {
            magickCrafting.setPlayerUniqueId(player.getUniqueID());
            if (magickCrafting.getPlayerUniqueId().equals(player.getUniqueID()))
                magickCrafting.enableTrans();
        }
        else if (handIn == Hand.MAIN_HAND) {
            if(player.getHeldItemMainhand() != null && !player.getHeldItemMainhand().isEmpty())
            {
                if(magickCrafting.putManaItem(player.getHeldItemMainhand().copy()))
                    player.getHeldItemMainhand().setCount(0);
            }
            else if(magickCrafting.getMainItem() != null && magickCrafting.getMainItem() != ItemStack.EMPTY)
            {
                //ManaItemEntity entity = new ManaItemEntity(worldIn, player.getPosX(), player.getPosY(), player.getPosZ(), magickCrafting.getMainItem().copy());
                //worldIn.addEntity(entity);
                player.inventory.addItemStackToInventory(magickCrafting.getMainItem());
                magickCrafting.clearMainItem();
            }
        }
        return ActionResultType.SUCCESS;
    }
}
