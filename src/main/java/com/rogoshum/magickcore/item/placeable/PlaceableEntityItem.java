package com.rogoshum.magickcore.item.placeable;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.entity.PlaceableItemEntity;
import com.rogoshum.magickcore.init.ModEntities;
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
        placeEntity(context.getWorld(), new ItemStack(this), direction, pos);
    }

    public static PlaceableItemEntity placeEntity(World world, ItemStack stack, Direction direction, Vector3d pos) {
        if(world.isRemote) return null;
        if(!(stack.getItem() instanceof PlaceableEntityItem)) return null;
        PlaceableItemEntity itemEntity = ModEntities.placeable_entity.get().create(world);
        stack = stack.copy();
        stack.setCount(1);
        itemEntity.setItemStack(stack);
        itemEntity.setDirection(direction);
        itemEntity.setPosition(pos.x, pos.y, pos.z);
        itemEntity.setHeight(((PlaceableEntityItem) stack.getItem()).HEIGHT);
        itemEntity.setWidth(((PlaceableEntityItem) stack.getItem()).WIDTH);
        itemEntity.recalculateSize();
        world.addEntity(itemEntity);
        return itemEntity;
    }
}
