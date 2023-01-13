package com.rogoshum.magickcore.common.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModTileEntities;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.util.EntityLightSourceManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ElementWoolTileEntity extends TileEntity implements ILightSourceEntity {
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
        CompoundNBT tag = new CompoundNBT();
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
        CompoundNBT tag = new CompoundNBT();
        tag.putString("ELEMENT", eType);
        stack.setTag(tag);
        return Util.make(new ArrayList<>(), (list) -> list.add(stack));
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

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT compoundNBT = super.getUpdateTag();
        compoundNBT.putString("TYPE", this.eType);
        return compoundNBT;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.eType = tag.getString("TYPE");
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        this.eType = compound.getString("TYPE");
        super.load(state, compound);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
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
    public Vector3d positionVec() {
        return Vector3d.atCenterOf(this.getBlockPos());
    }

    @Override
    public AxisAlignedBB boundingBox() {
        return getRenderBoundingBox();
    }

    @Override
    public World world() {
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
        super.onLoad();
        //MagickCore.proxy.addRenderer(new ElementWoolRenderer(this));
        EntityLightSourceManager.addLightSource(this);
    }
}
