package com.rogoshum.magickcore.common.item.placeable;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.entity.PlaceableItemEntity;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModSounds;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PlaceableEntityItem extends EntityItem {
    public final float WIDTH;
    public final float HEIGHT;

    public PlaceableEntityItem(Properties properties, float width, float height) {
        super(properties);
        WIDTH = width;
        HEIGHT = height;
    }

    @Override
    public void placeEntity(BlockPlaceContext context) {
        Vec3 pos = context.getClickLocation();
        Direction direction = context.getClickedFace();
        Vec3 offset = Vec3.atLowerCornerOf(direction.getNormal()).scale(WIDTH);
        if(direction.getAxis().isVertical()) {
            if(direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE))
                offset = Vec3.ZERO;
            else
                offset = Vec3.atLowerCornerOf(direction.getNormal()).scale(HEIGHT);
        }
        pos.add(offset);
        PlaceableItemEntity entity = placeEntity(context.getLevel(), context.getItemInHand(), direction, pos);
        if(entity != null) {
            context.getLevel().addFreshEntity(entity);
        }
    }

    public static PlaceableItemEntity placeEntity(Level world, ItemStack stack, Direction direction, Vec3 pos) {
        if(world.isClientSide) return null;
        if(!(stack.getItem() instanceof PlaceableEntityItem)) return null;
        PlaceableItemEntity itemEntity = ModEntities.PLACEABLE_ENTITY.get().create(world);
        ItemStack entityStack = stack.copy();
        stack.shrink(1);
        entityStack.setCount(1);
        itemEntity.setItemStack(entityStack);
        itemEntity.setDirection(direction);
        itemEntity.setPos(pos.x, pos.y, pos.z);
        itemEntity.setHeight(((PlaceableEntityItem) entityStack.getItem()).HEIGHT);
        itemEntity.setWidth(((PlaceableEntityItem) entityStack.getItem()).WIDTH);
        itemEntity.refreshDimensions();
        itemEntity.playSound(ModSounds.place.get(), 0.25f, 1+MagickCore.rand.nextFloat());
        return itemEntity;
    }
}
