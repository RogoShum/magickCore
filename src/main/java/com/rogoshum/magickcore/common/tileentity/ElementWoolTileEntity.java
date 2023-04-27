package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.client.tileentity.easyrender.ElementWoolRenderer;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.Util;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElementWoolTileEntity extends BlockEntity implements ILightSourceEntity {
    public String eType = LibElements.ORIGIN;
    public ElementWoolTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.ELEMENT_WOOL_TILE_ENTITY.get(), pos, state);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }

    @Override
    public boolean spawnGlowBlock() {
        return true;
    }
    public void dropItem() {
        if(this.level.isClientSide) return;
        ItemStack stack = new ItemStack(ModItems.ELEMENT_WOOL.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("ELEMENT", eType);
        stack.setTag(tag);

        ItemEntity entity = new ItemEntity(level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, stack);
        entity.setPickUpDelay(20);
        level.addFreshEntity(entity);
    }

    public List<ItemStack> getDrops() {
        if(this.level.isClientSide) return Collections.emptyList();
        ItemStack stack = new ItemStack(ModItems.ELEMENT_WOOL.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("ELEMENT", eType);
        stack.setTag(tag);
        return Util.make(new ArrayList<>(), (list) -> list.add(stack));
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleUpdateTag(pkt.getTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundNBT = super.getUpdateTag();
        compoundNBT.putString("TYPE", this.eType);
        return compoundNBT;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.eType = tag.getString("TYPE");
    }

    @Override
    public void load(CompoundTag compound) {
        this.eType = compound.getString("TYPE");
        super.load(compound);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.putString("TYPE", this.eType);
    }

    @Override
    public float getSourceLight() {
        return 5;
    }

    @Override
    public boolean alive() {
        return !this.remove;
    }

    @Override
    public Vec3 positionVec() {
        return Vec3.atCenterOf(this.getBlockPos());
    }

    @Override
    public AABB boundingBox() {
        return getRenderBoundingBox();
    }

    @Override
    public Level world() {
        return this.getLevel();
    }

    @Override
    public float eyeHeight() {
        return 0.5f;
    }

    @Override
    public Color getColor() {
        return MagickCore.proxy.getElementRender(this.eType).getPrimaryColor();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        MagickCore.proxy.addRenderer(() -> new ElementWoolRenderer(this));
        //EntityLightSourceManager.addLightSource(this);
    }
}
