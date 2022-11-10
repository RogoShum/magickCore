package com.rogoshum.magickcore.common.magick.lifestate.repeater;

import com.rogoshum.magickcore.common.tileentity.MagickRepeaterTileEntity;
import com.rogoshum.magickcore.common.entity.projectile.LifeStateEntity;
import com.rogoshum.magickcore.common.magick.lifestate.ItemStackLifeState;
import com.rogoshum.magickcore.common.magick.lifestate.LifeState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;

public class PotionRepeater extends LifeRepeater{
    @Override
    public void input(MagickRepeaterTileEntity tile, LifeStateEntity oldLife, LifeStateEntity newLife, @Nullable Direction direction) {
        if (newLife.getCarrier().getState(LifeState.POTION) != null && newLife.getCarrier().getState(LifeState.POTION).getValue() != null) return;
        TileEntity tileEntity = tile.getWorld().getTileEntity(tile.getPos().add(direction.getDirectionVec()));
        if(tileEntity instanceof LockableLootTileEntity)
        {
            LockableLootTileEntity loots = (LockableLootTileEntity) tileEntity;
            for(int i = 0; i < loots.getSizeInventory(); ++i){
                ItemStack stack = loots.getStackInSlot(i);
                if(!stack.isEmpty() && stack.getItem() instanceof PotionItem && stack.getItem().hasEffect(stack)){
                    loots.removeStackFromSlot(i);
                    ItemStackLifeState item = (ItemStackLifeState) LifeState.createByName(LifeState.POTION);
                    item.setValue(stack);
                    newLife.getCarrier().addState(LifeState.POTION, item);
                    return;
                }
            }
        }
    }

    @Override
    public void output(MagickRepeaterTileEntity tile, LifeStateEntity oldLife, LifeStateEntity newLife, @Nullable Direction direction) {
        if (oldLife.getCarrier().getState(LifeState.POTION) != null && oldLife.getCarrier().getState(LifeState.POTION).getValue() != null) {
            ItemStack itemStack = (ItemStack) oldLife.getCarrier().getState(LifeState.POTION).getValue();
            TileEntity tileEntity = tile.getWorld().getTileEntity(tile.getPos().add(direction.getDirectionVec()));
            if(tileEntity instanceof LockableLootTileEntity)
            {
                LockableLootTileEntity loots = (LockableLootTileEntity) tileEntity;
                for(int i = 0; i < loots.getSizeInventory(); ++i){
                    ItemStack stack = loots.getStackInSlot(i);
                    if(stack.isEmpty()){
                        loots.setInventorySlotContents(i, itemStack);
                        oldLife.getCarrier().removeState(LifeState.POTION);
                        newLife.getCarrier().removeState(LifeState.POTION);
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
        return ItemStack.EMPTY;
    }
}
