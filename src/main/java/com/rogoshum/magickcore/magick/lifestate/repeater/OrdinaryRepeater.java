package com.rogoshum.magickcore.magick.lifestate.repeater;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.block.tileentity.MagickRepeaterTileEntity;
import com.rogoshum.magickcore.entity.LifeStateEntity;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.magick.lifestate.EntityLifeState;
import com.rogoshum.magickcore.magick.lifestate.LifeState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.List;

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
        return new ItemStack(ModItems.ordinary_repeater.get());
    }
}
