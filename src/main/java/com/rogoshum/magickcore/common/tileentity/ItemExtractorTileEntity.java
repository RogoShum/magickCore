package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.common.block.ItemExtractorBlock;
import com.rogoshum.magickcore.common.init.ModBlocks;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.child.ItemContext;
import com.rogoshum.magickcore.common.util.ItemStackUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Objects;

public class ItemExtractorTileEntity extends TileEntity implements ITickableTileEntity {
    private ItemEntity item;
    private AxisAlignedBB aabb;
    public ItemExtractorTileEntity() {
        super(ModTileEntities.ITEM_EXTRACTOR_TILE_ENTITY.get());
    }

    @Override
    public void tick() {
        if(aabb == null)
            aabb = new AxisAlignedBB(worldPosition).inflate(0.25);

        BlockState state = level.getBlockState(worldPosition);
        TileEntity tile = level.getBlockEntity(worldPosition.offset(state.getValue(ItemExtractorBlock.FACING).getOpposite().getNormal()));
        if(tile instanceof IInventory) {
            IInventory inventory = (IInventory) tile;
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
                item.setDeltaMovement(Vector3d.ZERO);
                double y = worldPosition.getY() + 0.2;
                if(item.position().add(0, 0.3, 0).distanceTo(Vector3d.atCenterOf(worldPosition)) > 1.0) {
                    item = null;
                    return;
                }
                item.setPos(worldPosition.getX() + 0.5, y, worldPosition.getZ() + 0.5);
                if(ItemStackUtil.getItemEntityAge(item) > 120)
                    ItemStackUtil.setItemEntityAge(item, 0);
                ItemExtractorBlock.updatePower(level, worldPosition, level.getBlockState(worldPosition), Math.max(1, (int) (item.getItem().getCount() * 0.25)));
                tile = level.getBlockEntity(worldPosition.offset(state.getValue(ItemExtractorBlock.FACING).getNormal()));
                if(tile instanceof IInventory) {
                    IInventory inventory = (IInventory) tile;
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
