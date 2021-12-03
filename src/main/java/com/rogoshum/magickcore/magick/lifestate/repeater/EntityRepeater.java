package com.rogoshum.magickcore.magick.lifestate.repeater;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.block.tileentity.MagickRepeaterTileEntity;
import com.rogoshum.magickcore.entity.LifeStateEntity;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.magick.lifestate.EntityLifeState;
import com.rogoshum.magickcore.magick.lifestate.LifeState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.List;

public class EntityRepeater extends LifeRepeater{
    @Override
    public void input(MagickRepeaterTileEntity tile, LifeStateEntity oldLife, LifeStateEntity newLife, @Nullable Direction direction) {
        if (newLife.getCarrier().getState(LifeState.ENTITY) != null && newLife.getCarrier().getState(LifeState.ENTITY).getValue() != null) return;
        AxisAlignedBB bb = new AxisAlignedBB(tile.getPos().add(direction.getDirectionVec()));
        List<Entity> list = tile.getWorld().getEntitiesWithinAABBExcludingEntity(null, bb);
        for (Entity entity : list) {
            EntitySize size = entity.getSize(entity.getPose());
            if (!(entity instanceof PlayerEntity) && entity.isNonBoss() && size.height + size.width <= 2.5) {
                EntityLifeState state = (EntityLifeState) LifeState.createByName(LifeState.ENTITY);
                state.setValue(entity);
                newLife.getCarrier().addState(LifeState.ENTITY, state);
                entity.remove();
                return;
            }
        }
    }

    @Override
    public void output(MagickRepeaterTileEntity tile, LifeStateEntity oldLife, LifeStateEntity newLife, @Nullable Direction direction) {
        if (oldLife.getCarrier().getState(LifeState.ENTITY) != null && oldLife.getCarrier().getState(LifeState.ENTITY).getValue() != null) {
            Entity entity = (Entity) oldLife.getCarrier().getState(LifeState.ENTITY).getValue();
            Vector3d pos = Vector3d.copyCentered(tile.getPos()).add(Vector3d.copy(direction.getDirectionVec()));
            entity.setPosition(pos.x, pos.y, pos.z);
            if(!entity.isAlive())
                entity.revive();
            oldLife.world.addEntity(entity);
            oldLife.getCarrier().removeState(LifeState.ENTITY);
            newLife.getCarrier().removeState(LifeState.ENTITY);
        }
    }

    @Override
    public boolean useDirection() {
        return true;
    }

    @Override
    public ItemStack dropItem() {
        return new ItemStack(ModItems.entity_repeater.get());
    }
}
