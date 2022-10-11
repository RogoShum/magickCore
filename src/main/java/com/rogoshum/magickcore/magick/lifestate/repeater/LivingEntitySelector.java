package com.rogoshum.magickcore.magick.lifestate.repeater;

import com.rogoshum.magickcore.block.tileentity.MagickRepeaterTileEntity;
import com.rogoshum.magickcore.entity.projectile.LifeStateEntity;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.magick.lifestate.EntitySelectorLifeState;
import com.rogoshum.magickcore.magick.lifestate.LifeState;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;

public class LivingEntitySelector extends EntitySelector{
    @Override
    public void input(MagickRepeaterTileEntity tile, LifeStateEntity oldLife, LifeStateEntity newLife, @Nullable Direction direction) {
        Entity player = getClosestPlayerFrom(newLife);
        Entity entity = getClosestEntityFrom(newLife, (pre) -> pre instanceof LivingEntity && pre.isAlive() && MagickReleaseHelper.sameLikeOwner(player, pre));
        if(entity != null){
            newLife.setMotion(entity.getPositionVec().add(0, entity.getHeight() / 2, 0).subtract(newLife.getPositionVec()).normalize().scale(0.1));

            EntitySelectorLifeState state = (EntitySelectorLifeState) LifeState.createByName(LifeState.ENTITY_SELECTOR);
            newLife.getCarrier().addState(LifeState.ENTITY_SELECTOR, state.setValue(entity));
            oldLife.getCarrier().addState("empty_entity_selector", state);
        }
    }

    @Override
    public void output(MagickRepeaterTileEntity tile, LifeStateEntity oldLife, LifeStateEntity newLife, @Nullable Direction direction) {
        Entity player = getClosestPlayerFrom(newLife);
        Entity entity;
        if(oldLife.getCarrier().hasState("empty_entity_selector"))
            entity = getClosestEntityFrom(newLife, (pre) -> pre instanceof LivingEntity && pre.isAlive());
        else
            entity = getClosestEntityFrom(newLife, (pre) -> pre instanceof LivingEntity && pre.isAlive() && !MagickReleaseHelper.sameLikeOwner(player, pre));

        if(entity != null){
            newLife.setMotion(entity.getPositionVec().add(0, entity.getHeight() / 2, 0).subtract(newLife.getPositionVec()).normalize().scale(0.1));

            EntitySelectorLifeState state = (EntitySelectorLifeState) LifeState.createByName(LifeState.ENTITY_SELECTOR);
            newLife.getCarrier().addState(LifeState.ENTITY_SELECTOR, state.setValue(entity));
        }
    }

    @Override
    public boolean useDirection() {
        return false;
    }

    @Override
    public ItemStack dropItem() {
        return new ItemStack(ModItems.living_entity_selector.get());
    }
}
