package com.rogoshum.magickcore.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.util.Objects;

public class ItemStackUtil {
    public static void dropItem(Level level, ItemStack stack, BlockPos pos) {
        dropItem(level, stack, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5);
    }

    public static void dropItem(Level level, ItemStack stack, Vec3 vec3) {
        dropItem(level, stack, vec3.x, vec3.y, vec3.z);
    }

    public static void dropItem(Level level, ItemStack stack, double x, double y, double z) {
        ItemEntity item = new ItemEntity(level, x, y, z, stack);
        if(!level.isClientSide())
            level.addFreshEntity(item);
    }

    public static void storeTEInStack(ItemStack stack, BlockEntity te) {
        CompoundTag compoundnbt = te.saveWithId();
        if (stack.getItem() instanceof PlayerHeadItem && compoundnbt.contains("SkullOwner")) {
            CompoundTag compoundnbt2 = compoundnbt.getCompound("SkullOwner");
            stack.getOrCreateTag().put("SkullOwner", compoundnbt2);
        } else {
            stack.addTagElement("BlockEntityTag", compoundnbt);
        }
    }

    public static void mergeItemEntity(ItemEntity item, ItemEntity other) {
        ItemStack itemstack = item.getItem();
        ItemStack itemstack1 = other.getItem();
        if (Objects.equals(item.getOwner(), other.getOwner()) && canMergeStacks(itemstack, itemstack1)) {
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
        return ItemEntity.merge(stack1, stack2, maxCount);
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
        entity1.setPickUpDelay(Math.max(getItemEntityPickupDelay(entity1), getItemEntityPickupDelay(entity2)));
        setItemEntityAge(entity1, Math.min(getItemEntityAge(entity1), getItemEntityAge(entity2)));
        if (stack2.isEmpty()) {
            entity2.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    public static int getItemEntityAge(ItemEntity entity) {
        return ObfuscationReflectionHelper.getPrivateValue(ItemEntity.class, entity, "f_31985_");
    }

    public static int getItemEntityPickupDelay(ItemEntity entity) {
        return ObfuscationReflectionHelper.getPrivateValue(ItemEntity.class, entity, "f_31986_");
    }

    public static void setItemEntityAge(ItemEntity entity, int age) {
        ObfuscationReflectionHelper.setPrivateValue(ItemEntity.class, entity, age, "f_31985_");
    }
}
