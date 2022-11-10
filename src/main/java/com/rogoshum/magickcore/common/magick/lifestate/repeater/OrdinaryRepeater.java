package com.rogoshum.magickcore.common.magick.lifestate.repeater;

import com.rogoshum.magickcore.common.tileentity.MagickRepeaterTileEntity;
import com.rogoshum.magickcore.common.entity.projectile.LifeStateEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class OrdinaryRepeater extends LifeRepeater {

    @Override
    public void input(MagickRepeaterTileEntity tile, LifeStateEntity oldLife, LifeStateEntity newLife, @Nullable Direction direction) {
        newLife.remove(true);
    }

    @Override
    public void output(MagickRepeaterTileEntity tile, LifeStateEntity oldLife, LifeStateEntity newLife, @Nullable Direction direction) {
        if (!tile.getDirection().equals(Vector3d.ZERO))
            newLife.setMotion(tile.getDirection());
        if(!newLife.isAlive()) {
            newLife.revive();
        }
    }

    @Override
    public boolean useDirection() {
        return false;
    }

    @Override
    public boolean useTileVector() {
        return true;
    }

    @Override
    public ItemStack dropItem() {
        return ItemStack.EMPTY;
    }
}
