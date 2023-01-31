package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.block.ILoadBlockEntity;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ElementWoolTileEntity extends BlockEntity implements ILightSourceEntity, BlockEntityClientSerializable, ILoadBlockEntity {
    public String eType = LibElements.ORIGIN;
    public ElementWoolTileEntity() {
        super(ModTileEntities.ELEMENT_WOOL_TILE_ENTITY.get());
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

        ItemEntity entity = new ItemEntity(level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5);
        entity.setItem(stack);
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

    @Override
    public void load(BlockState state, CompoundTag compound) {
        this.eType = compound.getString("TYPE");
        super.load(state, compound);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putString("TYPE", this.eType);
        return super.save(compound);
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
        return new AABB(this.getBlockPos());
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
        return MagickCore.proxy.getElementRender(this.eType).getColor();
    }

    @Override
    public void onLoad() {
        //MagickCore.proxy.addRenderer(new ElementWoolRenderer(this));
        EntityLightSourceManager.addLightSource(this);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.eType = tag.getString("TYPE");
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        tag.putString("TYPE", this.eType);
        return tag;
    }
}
