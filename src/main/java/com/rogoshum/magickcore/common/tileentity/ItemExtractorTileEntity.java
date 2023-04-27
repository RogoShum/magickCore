package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.common.block.ItemExtractorBlock;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import com.rogoshum.magickcore.api.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.util.ItemStackUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ItemExtractorTileEntity extends BlockEntity{
    private final ConditionContext condition = ConditionContext.create();
    private ItemEntity item;
    private AABB aabb;
    public ItemExtractorTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.ITEM_EXTRACTOR_TILE_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos p_155254_, BlockState p_155255_, ItemExtractorTileEntity me) {
        if(me.aabb == null)
            me.aabb = new AABB(me.worldPosition).inflate(0.25);

        BlockState state = level.getBlockState(me.worldPosition);
        BlockEntity tile = level.getBlockEntity(me.worldPosition.offset(state.getValue(ItemExtractorBlock.FACING).getOpposite().getNormal()));
        if(tile instanceof Container inventory) {
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack slot = inventory.getItem(i);
                if(!slot.isEmpty()) {
                    if(me.item == null || me.item.getItem().isEmpty()) {
                        ItemStack copy = slot.copy();
                        copy.setCount(1);
                        ItemEntity itemEntity = new ItemEntity(level, me.worldPosition.getX() + 0.5, me.worldPosition.getY() + 0.2, me.worldPosition.getZ() + 0.5, copy);
                        if(level.addFreshEntity(itemEntity)) {
                            me.item = itemEntity;
                            slot.shrink(1);
                            inventory.setChanged();
                            break;
                        }
                    } else if(ItemStackUtil.canMergeStacks(me.item.getItem(), slot)){
                        ItemStack copy = slot.copy();
                        copy.setCount(1);
                        ItemStack newItem = ItemStackUtil.mergeInventoryStacks(me.item.getItem(), copy, 64);
                        if(newItem.getCount() > me.item.getItem().getCount()) {
                            slot.shrink(1);
                            inventory.setChanged();
                            me.item.getItem().shrink(-1);
                            break;
                        }
                    }
                }
            }
        }

        if(me.item != null) {
            if(me.item.isAlive()) {
                me.item.setDeltaMovement(Vec3.ZERO);
                double y = me.worldPosition.getY() + 0.2;
                if(me.item.position().add(0, 0.3, 0).distanceTo(Vec3.atCenterOf(me.worldPosition)) > 1.0) {
                    me.item = null;
                    return;
                }
                me.item.setPos(me.worldPosition.getX() + 0.5, y, me.worldPosition.getZ() + 0.5);
                if(ItemStackUtil.getItemEntityAge(me.item) > 120)
                    ItemStackUtil.setItemEntityAge(me.item, 0);
                ItemExtractorBlock.updatePower(level, me.worldPosition, level.getBlockState(me.worldPosition), Math.max(1, (int) (me.item.getItem().getCount() * 0.25)));
                tile = level.getBlockEntity(me.worldPosition.offset(state.getValue(ItemExtractorBlock.FACING).getNormal()));
                if(tile instanceof Container) {
                    Container inventory = (Container) tile;
                    ItemStack copy = me.item.getItem().copy();
                    copy.setCount(1);
                    for (int i = 0; i < inventory.getContainerSize(); i++) {
                        ItemStack slot = inventory.getItem(i);
                        if(slot.isEmpty()) {
                            inventory.setItem(i, copy);
                            me.item.getItem().shrink(1);
                            inventory.setChanged();
                            break;
                        } else if(ItemStackUtil.canMergeStacks(slot, copy)) {
                            ItemStack newSlot = ItemStackUtil.mergeInventoryStacks(slot, copy, Math.min(inventory.getMaxStackSize(), slot.getMaxStackSize()));
                            inventory.setItem(i, newSlot);
                            me.item.getItem().shrink(1);
                            inventory.setChanged();
                            break;
                        }
                    }
                }
            } else
                me.item = null;
            return;
        }
        ItemExtractorBlock.updatePower(level, me.worldPosition, level.getBlockState(me.worldPosition), 0);
        List<ItemEntity> items = level.getEntities(EntityType.ITEM, me.aabb, Entity::isAlive);
        if(!items.isEmpty()) {
            ItemEntity entity = items.get(0);
            if(entity != null && entity.isAlive())
                me.item = entity;
        }
    }
}
