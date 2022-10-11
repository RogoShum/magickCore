package com.rogoshum.magickcore.magick.lifestate.repeater;

import com.rogoshum.magickcore.block.tileentity.MagickRepeaterTileEntity;
import com.rogoshum.magickcore.entity.projectile.LifeStateEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.Arrays;

public abstract class LifeRepeater {
    public void touch(MagickRepeaterTileEntity tile, LifeStateEntity life) {
        LifeStateEntity newLife = life.split(tile);

        if (useDirection()) {
            Arrays.stream(tile.getPort()).filter(MagickRepeaterTileEntity.InterfaceDirection::isTurning).forEach(port -> {
                direction(tile, life, newLife, port.getDirection());
            });
        }
        else
            direction(tile, life, newLife, null);
    }

    private void direction(MagickRepeaterTileEntity tile, LifeStateEntity oldLife, LifeStateEntity newLife, Direction direction) {
        if (tile.getTouchMode() == MagickRepeaterTileEntity.TouchMode.DEFAULT) {
            input(tile, oldLife, newLife, direction);
            output(tile, oldLife, newLife, direction);
        } else if (tile.getTouchMode() == MagickRepeaterTileEntity.TouchMode.INPUT)
            input(tile, oldLife, newLife, direction);
        else
            output(tile, oldLife, newLife, direction);
    }

    public abstract void input(MagickRepeaterTileEntity tile, LifeStateEntity oldLife, LifeStateEntity newLife, @Nullable Direction direction);

    public abstract void output(MagickRepeaterTileEntity tile, LifeStateEntity oldLife, LifeStateEntity newLife, @Nullable Direction direction);

    public abstract boolean useDirection();

    public boolean useTileVector(){return false;}

    public abstract ItemStack dropItem();
}
