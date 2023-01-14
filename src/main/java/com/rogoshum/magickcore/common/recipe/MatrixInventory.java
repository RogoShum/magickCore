package com.rogoshum.magickcore.common.recipe;

import com.rogoshum.magickcore.common.entity.PlaceableItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.Optional;

public class MatrixInventory implements IInventory {
    private final Optional<PlaceableItemEntity>[][][] matrix;
    private final int y;
    private final int x;
    private final int z;

    public MatrixInventory(Optional<PlaceableItemEntity>[][][] matrix) {
        this.matrix = matrix;
        if(matrix.length == 0) {
            y = 0;
        } else
            y = matrix.length;

        if(y > 0 && matrix[0].length > 0) {
            x = matrix[0].length;
        } else
            x = 0;

        if(x > 0 && matrix[0][0].length > 0) {
            z = matrix[0][0].length;
        } else
            z = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    public Optional<PlaceableItemEntity>[][][] getMatrix() {
        return matrix;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int index, ItemStack stack) {

    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return false;
    }

    @Override
    public void clearContent() {

    }
}
