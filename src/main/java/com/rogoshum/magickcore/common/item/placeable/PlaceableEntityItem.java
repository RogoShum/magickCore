package com.rogoshum.magickcore.common.item.placeable;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.entity.PlaceableItemEntity;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModSounds;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class PlaceableEntityItem extends EntityItem {
    public final float WIDTH;
    public final float HEIGHT;

    public PlaceableEntityItem(Properties properties, float width, float height) {
        super(properties);
        WIDTH = width;
        HEIGHT = height;
    }

    @Override
    public void placeEntity(BlockItemUseContext context) {
        Vector3d pos = context.getHitVec();
        Direction direction = context.getFace();
        Vector3d offset = Vector3d.copy(direction.getDirectionVec()).scale(WIDTH);
        if(direction.getAxis().isVertical()) {
            if(direction.getAxisDirection().equals(Direction.AxisDirection.POSITIVE))
                offset = Vector3d.ZERO;
            else
                offset = Vector3d.copy(direction.getDirectionVec()).scale(HEIGHT);
        }
        pos.add(offset);
        PlaceableItemEntity entity = placeEntity(context.getWorld(), context.getItem(), direction, pos);
        if(entity != null) {
            context.getWorld().addEntity(entity);
        }
    }

    public static PlaceableItemEntity placeEntity(World world, ItemStack stack, Direction direction, Vector3d pos) {
        if(world.isRemote) return null;
        if(!(stack.getItem() instanceof PlaceableEntityItem)) return null;
        PlaceableItemEntity itemEntity = ModEntities.PLACEABLE_ENTITY.get().create(world);
        ItemStack entityStack = stack.copy();
        stack.shrink(1);
        entityStack.setCount(1);
        itemEntity.setItemStack(entityStack);
        itemEntity.setDirection(direction);
        itemEntity.setPosition(pos.x, pos.y, pos.z);
        itemEntity.setHeight(((PlaceableEntityItem) entityStack.getItem()).HEIGHT);
        itemEntity.setWidth(((PlaceableEntityItem) entityStack.getItem()).WIDTH);
        itemEntity.recalculateSize();
        itemEntity.playSound(ModSounds.place.get(), 0.25f, 1+MagickCore.rand.nextFloat());
        return itemEntity;
    }
}
