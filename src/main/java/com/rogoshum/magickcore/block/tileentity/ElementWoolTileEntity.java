package com.rogoshum.magickcore.block.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.ILightSourceEntity;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.init.ModItems;
import com.rogoshum.magickcore.init.ModTileEntities;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.Color;
import com.rogoshum.magickcore.tool.EntityLightSourceHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class ElementWoolTileEntity extends CanSeeTileEntity implements ILightSourceEntity {
    public String eType = LibElements.ORIGIN;
    public ElementWoolTileEntity() {
        super(ModTileEntities.element_wool_tileentity.get());
    }

    @Override
    public void remove() {
        dropWoolItem();
        super.remove();
    }

    @Override
    public boolean spawnGlowBlock() {
        return true;
    }
    private void dropWoolItem()
    {
        if(this.world.isRemote) return;
        ItemStack stack = new ItemStack(ModItems.element_wool.get());
        CompoundNBT tag = new CompoundNBT();
        tag.putString("ELEMENT", eType);
        stack.setTag(tag);

        ItemEntity entity = new ItemEntity(world, this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5);
        entity.setItem(stack);
        entity.setPickupDelay(20);
        world.addEntity(entity);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(world.getBlockState(pkt.getPos()), pkt.getNbtCompound());
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
    public void read(BlockState state, CompoundNBT compound) {
        this.eType = compound.getString("TYPE");
        super.read(state, compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putString("TYPE", this.eType);
        return super.write(compound);
    }

    @Override
    public float getSourceLight() {
        return 5;
    }

    @Override
    public boolean alive() {
        return !this.removed;
    }

    @Override
    public Vector3d positionVec() {
        return Vector3d.copyCentered(this.getPos());
    }

    @Override
    public AxisAlignedBB boundingBox() {
        return getRenderBoundingBox();
    }

    @Override
    public World world() {
        return this.getWorld();
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
    }
}
