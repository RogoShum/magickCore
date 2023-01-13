package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class MaterialJarTileEntity extends TileEntity {
    private ItemStack stack = ItemStack.EMPTY;
    private int count;
    public MaterialJarTileEntity() {
        super(ModTileEntities.MATERIAL_JAR_TILE_ENTITY.get());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT compoundNBT = super.getUpdateTag();
        storageTag(compoundNBT);
        return compoundNBT;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        extractTag(tag);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        extractTag(compound);
        super.load(state, compound);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        storageTag(compound);
        return super.save(compound);
    }

    public void extractTag(CompoundNBT compound) {
        stack = ItemStack.of(compound.getCompound("stack"));
        count = compound.getInt("count");
    }

    public void storageTag(CompoundNBT compound) {
        compound.put("stack", stack.save(new CompoundNBT()));
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
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(level.getBlockState(pkt.getPos()), pkt.getTag());
    }

    public void dropItem() {
        if (!level.isClientSide && getCount() > 0 && !getStack().isEmpty()) {
            ItemStack itemstack = new ItemStack(ModItems.MATERIAL_JAR.get());
            CompoundNBT compoundnbt = save(new CompoundNBT());
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
}
