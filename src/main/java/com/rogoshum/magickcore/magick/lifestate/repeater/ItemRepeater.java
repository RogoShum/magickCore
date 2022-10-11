package com.rogoshum.magickcore.magick.lifestate.repeater;

import com.rogoshum.magickcore.block.tileentity.MagickRepeaterTileEntity;
import com.rogoshum.magickcore.entity.projectile.LifeStateEntity;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.magick.lifestate.ItemStackLifeState;
import com.rogoshum.magickcore.magick.lifestate.LifeState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;

public class ItemRepeater extends LifeRepeater{
    @Override
    public void input(MagickRepeaterTileEntity tile, LifeStateEntity oldLife, LifeStateEntity newLife, @Nullable Direction direction) {
        if (newLife.getCarrier().getState(LifeState.ITEM) != null && newLife.getCarrier().getState(LifeState.ITEM).getValue() != null) return;
        TileEntity tileEntity = tile.getWorld().getTileEntity(tile.getPos().add(direction.getDirectionVec()));
        if(tileEntity instanceof LockableLootTileEntity)
        {
            LockableLootTileEntity loots = (LockableLootTileEntity) tileEntity;
            for(int i = 0; i < loots.getSizeInventory(); ++i){
                ItemStack stack = loots.getStackInSlot(i);
                if(!stack.isEmpty()){
                    loots.removeStackFromSlot(i);
                    ItemStackLifeState item = (ItemStackLifeState) LifeState.createByName(LifeState.ITEM);
                    item.setValue(stack);
                    newLife.getCarrier().addState(LifeState.ITEM, item);
                    return;
                }
            }
        }
    }

    @Override
    public void output(MagickRepeaterTileEntity tile, LifeStateEntity oldLife, LifeStateEntity newLife, @Nullable Direction direction) {
        if (oldLife.getCarrier().getState(LifeState.ITEM) != null && oldLife.getCarrier().getState(LifeState.ITEM).getValue() != null) {
            ItemStack itemStack = (ItemStack) oldLife.getCarrier().getState(LifeState.ITEM).getValue();
            TileEntity tileEntity = tile.getWorld().getTileEntity(tile.getPos().add(direction.getDirectionVec()));
            if(tileEntity instanceof LockableLootTileEntity)
            {
                LockableLootTileEntity loots = (LockableLootTileEntity) tileEntity;
                for(int i = 0; i < loots.getSizeInventory(); ++i){
                    ItemStack stack = loots.getStackInSlot(i);
                    if(stack.isEmpty()){
                        loots.setInventorySlotContents(i, itemStack);
                        oldLife.getCarrier().removeState(LifeState.ITEM);
                        newLife.getCarrier().removeState(LifeState.ITEM);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public boolean useDirection() {
        return true;
    }

    @Override
    public ItemStack dropItem() {
        return new ItemStack(ModItems.item_repeater.get());
    }
}
