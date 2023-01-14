package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.common.block.ItemExtractorBlock;
import com.rogoshum.magickcore.common.init.ModBlocks;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.child.ItemContext;
import com.rogoshum.magickcore.common.util.ItemStackUtil;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Objects;

public class ItemExtractorTileEntity extends BlockEntity implements TickableBlockEntity {
    private ItemEntity item;
    private AABB aabb;
    public ItemExtractorTileEntity() {
        super(ModTileEntities.ITEM_EXTRACTOR_TILE_ENTITY.get());
    }

    @Override
    public void tick() {
        if(aabb == null)
            aabb = new AABB(worldPosition).inflate(0.25);

        BlockState state = level.getBlockState(worldPosition);
        BlockEntity tile = level.getBlockEntity(worldPosition.offset(state.getValue(ItemExtractorBlock.FACING).getOpposite().getNormal()));
        if(tile instanceof Container) {
            Container inventory = (Container) tile;
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack slot = inventory.getItem(i);
                if(!slot.isEmpty()) {
                    if(item == null || item.getItem().isEmpty()) {
                        ItemStack copy = slot.copy();
                        copy.setCount(1);
                        ItemEntity itemEntity = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.2, worldPosition.getZ() + 0.5, copy);
                        if(level.addFreshEntity(itemEntity)) {
                            item = itemEntity;
                            slot.shrink(1);
                            inventory.setChanged();
                            break;
                        }
                    } else if(ItemStackUtil.canMergeStacks(item.getItem(), slot)){
                        ItemStack copy = slot.copy();
                        copy.setCount(1);
                        ItemStack newItem = ItemStackUtil.mergeInventoryStacks(item.getItem(), copy, 64);
                        if(newItem.getCount() > item.getItem().getCount()) {
                            slot.shrink(1);
                            inventory.setChanged();
                            item.getItem().shrink(-1);
                            break;
                        }
                    }
                }
            }
        }

        if(item != null) {
            if(item.isAlive()) {
                item.setDeltaMovement(Vec3.ZERO);
                double y = worldPosition.getY() + 0.2;
                if(item.position().add(0, 0.3, 0).distanceTo(Vec3.atCenterOf(worldPosition)) > 1.0) {
                    item = null;
                    return;
                }
                item.setPos(worldPosition.getX() + 0.5, y, worldPosition.getZ() + 0.5);
                if(ItemStackUtil.getItemEntityAge(item) > 120)
                    ItemStackUtil.setItemEntityAge(item, 0);
                ItemExtractorBlock.updatePower(level, worldPosition, level.getBlockState(worldPosition), Math.max(1, (int) (item.getItem().getCount() * 0.25)));
                tile = level.getBlockEntity(worldPosition.offset(state.getValue(ItemExtractorBlock.FACING).getNormal()));
                if(tile instanceof Container) {
                    Container inventory = (Container) tile;
                    ItemStack copy = item.getItem().copy();
                    copy.setCount(1);
                    for (int i = 0; i < inventory.getContainerSize(); i++) {
                        ItemStack slot = inventory.getItem(i);
                        if(slot.isEmpty()) {
                            inventory.setItem(i, copy);
                            item.getItem().shrink(1);
                            inventory.setChanged();
                            break;
                        } else if(ItemStackUtil.canMergeStacks(slot, copy)) {
                            ItemStack newSlot = ItemStackUtil.mergeInventoryStacks(slot, copy, Math.min(inventory.getMaxStackSize(), slot.getMaxStackSize()));
                            inventory.setItem(i, newSlot);
                            item.getItem().shrink(1);
                            inventory.setChanged();
                            break;
                        }
                    }
                }
            } else
                item = null;
            return;
        }
        ItemExtractorBlock.updatePower(level, worldPosition, level.getBlockState(worldPosition), 0);
        List<ItemEntity> items = level.getEntities(EntityType.ITEM, aabb, Entity::isAlive);
        if(!items.isEmpty()) {
            ItemEntity entity = items.get(0);
            if(entity != null && entity.isAlive())
                item = entity;
        }
    }
}
