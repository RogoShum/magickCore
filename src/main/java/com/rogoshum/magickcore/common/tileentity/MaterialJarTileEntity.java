package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MaterialJarTileEntity extends BlockEntity implements BlockEntityClientSerializable {
    private ItemStack stack = ItemStack.EMPTY;
    private int count;
    public MaterialJarTileEntity() {
        super(ModTileEntities.MATERIAL_JAR_TILE_ENTITY.get());
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundNBT = super.getUpdateTag();
        storageTag(compoundNBT);
        return compoundNBT;
    }

    @Override
    public void load(BlockState state, CompoundTag compound) {
        extractTag(compound);
        super.load(state, compound);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        storageTag(compound);
        return super.save(compound);
    }

    public void extractTag(CompoundTag compound) {
        stack = ItemStack.of(compound.getCompound("stack"));
        count = compound.getInt("count");
    }

    public void storageTag(CompoundTag compound) {
        compound.put("stack", stack.save(new CompoundTag()));
        compound.putInt("count", count);
    }

    public void putStack(ItemStack stack) {
        putStack(stack, stack.getCount());
    }

    public void putStack(ItemStack stack, int count) {
        //if(!(stack.getItem() instanceof IManaMaterial)) return;
        boolean stack1 = !stack.hasTag() || (stack.hasTag() && stack.getTag().isEmpty());
        boolean stack2 = !this.stack.hasTag() || (this.stack.hasTag() && this.stack.getTag().isEmpty());
        if(count > stack.getCount()) return;
        if(this.stack.isEmpty()) {
            this.stack = stack.copy();
            this.stack.setCount(1);
            this.count = count;
            stack.shrink(count);
            updateInfo();
        } else if (this.stack.sameItem(stack) && ((stack1 && stack2) || ItemStack.tagMatches(stack, this.stack))) {
            this.count += count;
            stack.shrink(count);
            updateInfo();
        }
    }

    public ItemStack takeStack(int count) {
        if(this.count > 0 && !stack.isEmpty() && count <= this.count) {
            this.count -= count;
            ItemStack copy = stack.copy();
            copy.setCount(count);
            if(this.count <= 0)
                this.stack.shrink(1);
            updateInfo();
            return copy;
        } else {
            this.stack.shrink(1);
            return ItemStack.EMPTY;
        }
    }

    public int getCount() {
        return count;
    }

    public ItemStack getStack() {
        return stack;
    }

    protected void updateInfo() {
        if (!level.isClientSide)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
    }

    public void dropItem() {
        if (!level.isClientSide && getCount() > 0 && !getStack().isEmpty()) {
            ItemStack itemstack = new ItemStack(ModItems.MATERIAL_JAR.get());
            CompoundTag compoundnbt = save(new CompoundTag());
            if (!compoundnbt.isEmpty()) {
                itemstack.addTagElement("BlockEntityTag", compoundnbt);
            }

            ItemEntity itementity = new ItemEntity(level, (double)worldPosition.getX() + 0.5D, (double)worldPosition.getY() + 0.5D, (double)worldPosition.getZ() + 0.5D, itemstack);
            itementity.setDefaultPickUpDelay();
            level.addFreshEntity(itementity);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        extractTag(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        storageTag(tag);
        return tag;
    }
}
