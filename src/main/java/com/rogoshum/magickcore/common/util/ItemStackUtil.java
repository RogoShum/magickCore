package com.rogoshum.magickcore.common.util;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.Objects;

public class ItemStackUtil {

    public static void mergeItemEntity(ItemEntity item, ItemEntity other) {
        ItemStack itemstack = item.getItem();
        ItemStack itemstack1 = other.getItem();
        if (Objects.equals(item.getOwnerId(), other.getOwnerId()) && canMergeStacks(itemstack, itemstack1)) {
            if (itemstack1.getCount() < itemstack.getCount()) {
                mergeItemEntity(item, itemstack, other, itemstack1);
            } else {
                mergeItemEntity(other, itemstack1, item, itemstack);
            }
        }
    }

    public static boolean canMergeStacks(ItemStack stack1, ItemStack stack2) {
        if (stack2.getItem() != stack1.getItem()) {
            return false;
        } else if (stack1.getCount() >= stack1.getMaxStackSize()) {
            return false;
        } else if (stack2.hasTag() ^ stack1.hasTag()) {
            return false;
        } else if (!stack1.areCapsCompatible(stack2)) {
            return false;
        } else {
            return !stack2.hasTag() || stack2.getTag().equals(stack1.getTag());
        }
    }

    public static ItemStack mergeStacks(ItemStack stack1, ItemStack stack2, int maxCount) {
        return ItemEntity.mergeStacks(stack1, stack2, maxCount);
    }

    public static ItemStack mergeInventoryStacks(ItemStack stack1, ItemStack stack2, int maxCount) {
        int i = Math.min(maxCount - stack1.getCount(), stack2.getCount());
        ItemStack itemstack = stack1.copy();
        itemstack.grow(i);
        stack2.shrink(i);
        return itemstack;
    }

    public static void mergeItemEntity(ItemEntity entity, ItemStack stack1, ItemStack stack2) {
        ItemStack itemstack = mergeStacks(stack1, stack2, 64);
        entity.setItem(itemstack);
    }

    public static void mergeItemEntity(ItemEntity entity1, ItemStack stack1, ItemEntity entity2, ItemStack stack2) {
        mergeItemEntity(entity1, stack1, stack2);
        entity1.setPickupDelay(Math.max(getItemEntityPickupDelay(entity1), getItemEntityPickupDelay(entity2)));
        setItemEntityAge(entity1, Math.min(getItemEntityAge(entity1), getItemEntityAge(entity2)));
        if (stack2.isEmpty()) {
            entity2.remove();
        }
    }

    public static int getItemEntityAge(ItemEntity entity) {
        return ObfuscationReflectionHelper.getPrivateValue(ItemEntity.class, entity, "field_70292_b");
    }

    public static int getItemEntityPickupDelay(ItemEntity entity) {
        return ObfuscationReflectionHelper.getPrivateValue(ItemEntity.class, entity, "field_145804_b");
    }

    public static void setItemEntityAge(ItemEntity entity, int age) {
        ObfuscationReflectionHelper.setPrivateValue(ItemEntity.class, entity, age, "field_70292_b");
    }
}
