package com.rogoshum.magickcore.magick.lifestate.repeater;

import com.rogoshum.magickcore.block.tileentity.MagickRepeaterTileEntity;
import com.rogoshum.magickcore.entity.projectile.LifeStateEntity;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.magick.lifestate.EntitySelectorLifeState;
import com.rogoshum.magickcore.magick.lifestate.LifeState;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class EntitySelector extends LifeRepeater{
    @Override
    public void input(MagickRepeaterTileEntity tile, LifeStateEntity oldLife, LifeStateEntity newLife, @Nullable Direction direction) {
        Entity player = getClosestPlayerFrom(newLife);
        Entity entity = getClosestEntityFrom(newLife, (pre) -> pre.isAlive() && MagickReleaseHelper.sameLikeOwner(player, pre));
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
            entity = getClosestEntityFrom(newLife, Entity::isAlive);
        else
            entity = getClosestEntityFrom(newLife, (pre) -> pre.isAlive() && !MagickReleaseHelper.sameLikeOwner(player, pre));

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
        return new ItemStack(ModItems.entity_selector.get());
    }

    public Entity getClosestEntityFrom(LifeStateEntity newLife, Predicate<? super Entity> predicate){
        Entity closest = null;
        List<Entity> list = newLife.world.getEntitiesInAABBexcluding(newLife, newLife.getBoundingBox().grow(16), predicate);
        for (Entity entity : list){
            if(closest == null || newLife.getDistanceSq(closest) > newLife.getDistanceSq(entity))
                closest = entity;
        }

        return closest;
    }

    public PlayerEntity getClosestPlayerFrom(LifeStateEntity newLife){
        return newLife.world.getClosestPlayer(newLife, Double.MAX_VALUE);
    }
}
