package com.rogoshum.magickcore.magick.lifestate.repeater;

import com.rogoshum.magickcore.block.tileentity.MagickRepeaterTileEntity;
import com.rogoshum.magickcore.entity.projectile.LifeStateEntity;
import com.rogoshum.magickcore.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;

public class ManaExtractRepeater extends LifeRepeater {

    @Override
    public void input(MagickRepeaterTileEntity tile, LifeStateEntity oldLife, LifeStateEntity newLife, @Nullable Direction direction) {
        newLife.manaCapacity().setMana(newLife.manaCapacity().getMana() + newLife.getSupplierBlock().supplyMana(0.2f));
    }

    @Override
    public void output(MagickRepeaterTileEntity tile, LifeStateEntity oldLife, LifeStateEntity newLife, @Nullable Direction direction) {
    }

    @Override
    public boolean useDirection() {
        return true;
    }

    @Override
    public boolean useTileVector() {
        return false;
    }

    @Override
    public ItemStack dropItem() {
        return new ItemStack(ModItems.mana_extract_repeater.get());
    }
}
