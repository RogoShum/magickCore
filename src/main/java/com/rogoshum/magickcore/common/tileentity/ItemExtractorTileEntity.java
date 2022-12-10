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
            aabb = new AxisAlignedBB(pos).grow(0.25);

        BlockState state = world.getBlockState(pos);
        TileEntity tile = world.getTileEntity(pos.add(state.get(ItemExtractorBlock.FACING).getOpposite().getDirectionVec()));
        if(tile instanceof IInventory) {
            IInventory inventory = (IInventory) tile;
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack slot = inventory.getStackInSlot(i);
                if(!slot.isEmpty()) {
                    if(item == null || item.getItem().isEmpty()) {
                        ItemStack copy = slot.copy();
                        copy.setCount(1);
                        ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, copy);
                        if(world.addEntity(itemEntity)) {
                            item = itemEntity;
                            slot.shrink(1);
                            inventory.markDirty();
                            break;
                        }
                    } else if(ItemStackUtil.canMergeStacks(item.getItem(), slot)){
                        ItemStack copy = slot.copy();
                        copy.setCount(1);
                        ItemStack newItem = ItemStackUtil.mergeInventoryStacks(item.getItem(), copy, 64);
                        if(newItem.getCount() > item.getItem().getCount()) {
                            slot.shrink(1);
                            inventory.markDirty();
                            item.getItem().shrink(-1);
                            break;
                        }
                    }
                }
            }
        }

        if(item != null) {
            if(item.isAlive()) {
                item.setMotion(Vector3d.ZERO);
                double y = pos.getY() + 0.2;
                if(item.getPositionVec().add(0, 0.3, 0).distanceTo(Vector3d.copyCentered(pos)) > 1.0) {
                    item = null;
                    return;
                }
                item.setPosition(pos.getX() + 0.5, y, pos.getZ() + 0.5);
                if(ItemStackUtil.getItemEntityAge(item) > 120)
                    ItemStackUtil.setItemEntityAge(item, 0);
                ItemExtractorBlock.updatePower(world, pos, world.getBlockState(pos), Math.max(1, (int) (item.getItem().getCount() * 0.25)));
                tile = world.getTileEntity(pos.add(state.get(ItemExtractorBlock.FACING).getDirectionVec()));
                if(tile instanceof IInventory) {
                    IInventory inventory = (IInventory) tile;
                    ItemStack copy = item.getItem().copy();
                    copy.setCount(1);
                    for (int i = 0; i < inventory.getSizeInventory(); i++) {
                        ItemStack slot = inventory.getStackInSlot(i);
                        if(slot.isEmpty()) {
                            inventory.setInventorySlotContents(i, copy);
                            item.getItem().shrink(1);
                            inventory.markDirty();
                            break;
                        } else if(ItemStackUtil.canMergeStacks(slot, copy)) {
                            ItemStack newSlot = ItemStackUtil.mergeInventoryStacks(slot, copy, Math.min(inventory.getInventoryStackLimit(), slot.getMaxStackSize()));
                            inventory.setInventorySlotContents(i, newSlot);
                            item.getItem().shrink(1);
                            inventory.markDirty();
                            break;
                        }
                    }
                }
            } else
                item = null;
            return;
        }
        ItemExtractorBlock.updatePower(world, pos, world.getBlockState(pos), 0);
        List<ItemEntity> items = world.getEntitiesWithinAABB(EntityType.ITEM, aabb, Entity::isAlive);
        if(!items.isEmpty()) {
            ItemEntity entity = items.get(0);
            if(entity != null && entity.isAlive())
                item = entity;
        }
    }
}
