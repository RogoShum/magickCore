package com.rogoshum.magickcore.magick.lifestate.repeater;

import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.block.tileentity.MagickRepeaterTileEntity;
import com.rogoshum.magickcore.entity.projectile.LifeStateEntity;
import com.rogoshum.magickcore.magick.lifestate.LifeState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;

public class MaterialRepeater extends LifeRepeater{
    @Override
    public void input(MagickRepeaterTileEntity tile, LifeStateEntity oldLife, LifeStateEntity newLife, @Nullable Direction direction) {
        TileEntity tileEntity = tile.getWorld().getTileEntity(tile.getPos().add(direction.getDirectionVec()));
        if(tileEntity instanceof LockableLootTileEntity)
        {
            LockableLootTileEntity loots = (LockableLootTileEntity) tileEntity;
            for(int i = 0; i < loots.getSizeInventory(); ++i){
                ItemStack stack = loots.getStackInSlot(i);
                if(!stack.isEmpty() && stack.getItem() instanceof IManaMaterial){
                    IManaMaterial material = (IManaMaterial) stack.getItem();
                    float manaNeed = material.getManaNeed(stack) / 500f;
                    if(oldLife.getSupplierBlock().supplyMana(manaNeed) >= manaNeed)
                        material.upgradeManaItem(stack, newLife);

                    if(!newLife.getCarrier().hasState(LifeState.MANA_STATE))
                        newLife.getCarrier().addState(LifeState.MANA_STATE, LifeState.createByName(LifeState.MANA_STATE));
                    return;
                }
            }
        }
    }

    @Override
    public void output(MagickRepeaterTileEntity tile, LifeStateEntity oldLife, LifeStateEntity newLife, @Nullable Direction direction) {
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
