package com.rogoshum.magickcore.block.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.block.IElementDataTile;
import com.rogoshum.magickcore.api.block.ILifeStateTile;
import com.rogoshum.magickcore.capability.IManaData;
import com.rogoshum.magickcore.capability.IManaItemData;
import com.rogoshum.magickcore.capability.ManaDataHandler;
import com.rogoshum.magickcore.capability.ManaItemDataHandler;
import com.rogoshum.magickcore.entity.LifeStateEntity;
import com.rogoshum.magickcore.enums.EnumManaType;
import com.rogoshum.magickcore.event.RenderEvent;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.ReleaseAttribute;
import com.rogoshum.magickcore.magick.lifestate.repeater.LifeRepeater;
import com.rogoshum.magickcore.tool.MagickReleaseHelper;
import com.rogoshum.magickcore.tool.PanelHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModTileEntities;
import com.rogoshum.magickcore.lib.LibBuff;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MagickBarrierTileEntity extends CanSeeTileEntity implements ITickableTileEntity, IPanelTileEntity, ILifeStateTile, IElementDataTile {
    private MagickBarrierTileEntity panelOutputFirst;
    private MagickBarrierTileEntity panelOutputSecond;
    public AxisAlignedBB center;
    public float mana;
    public float requiredMana;
    public LifeStateEntity lifeState;
    private static final float REQUIRE = 0.01f;
    private final IManaData element_data = ManaDataHandler.createDate();

    public MagickBarrierTileEntity() {
        super(ModTileEntities.magick_barrier_tileentity.get());
    }

    @Override
    public void tick() {
        ticksExisted++;
        boolean flag = false;

        if (mana <= 0) {
            flag = true;
            this.mana = 0;
            this.requiredMana = 0;
            element_data.setElement(ModElements.getElement(LibElements.ORIGIN));
            element_data.setRange(0);
            element_data.setForce(0);
            element_data.setManaType(EnumManaType.NONE);
            element_data.setTickTime(0);
            if(mana < 0 && !world.isRemote())
                updateInfo();
        }

        if (this.panelOutputFirst != null && this.panelOutputFirst.removed)
            flag = true;

        if (this.panelOutputSecond != null && this.panelOutputSecond.removed)
            flag = true;

        if (flag) {
            this.panelOutputFirst = null;
            this.panelOutputSecond = null;
            this.center = null;
        }

        if (mana > 0)
            tryLinksOthers();

        if (this.isClosed()) {
            doPlaneThings();
        }
    }

    private boolean suitableForLink(TileEntity tile) {
        return tile != this && tile instanceof MagickBarrierTileEntity && distanceTile(tile) <= 8 && this.panelOutputFirst != tile && this.panelOutputSecond != tile;
    }

    private void tryLinksOthers() {
        AtomicReference<MagickBarrierTileEntity> barrier = new AtomicReference<>(null);
        this.world.tickableTileEntities.forEach((tile) -> {
            if (suitableForLink(tile) && (barrier.get() == null || barrier.get().distanceTile(this) > ((MagickBarrierTileEntity) tile).distanceTile(this))) {
                barrier.set((MagickBarrierTileEntity) tile);
            }
        });

        if (barrier.get() == null) return;

        MagickBarrierTileEntity tile = barrier.get();

        boolean linkedThis = tile.panelOutputFirst == this || tile.panelOutputSecond == this;
        if (tile.isClosed() && linkedThis) {
            if (this.panelOutputFirst == tile)
                this.panelOutputFirst = null;

            if (this.panelOutputSecond == tile)
                this.panelOutputSecond = null;
        }

        if (linkedThis) return;

        if (this.panelOutputFirst == null) {
            this.panelOutputFirst = tile;
        } else if (this.panelOutputSecond == null) {
            this.panelOutputSecond = tile;
        }
    }

    public double distanceTile(TileEntity tileEntity) {
        float f = (float) (this.pos.getX() - tileEntity.getPos().getX());
        float f1 = (float) (this.pos.getY() - tileEntity.getPos().getY());
        float f2 = (float) (this.pos.getZ() - tileEntity.getPos().getZ());
        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    private void doPlaneThings() {

        if (this.lifeState != null && !this.lifeState.isAlive()) {
            this.lifeState.split(this, this.panelOutputFirst.pos);
            this.lifeState.split(this, this.panelOutputSecond.pos);
            this.lifeState = null;
        }

        if (center == null) {
            PanelHelper.Plane plane = new PanelHelper.Plane(this.pos, this.panelOutputFirst.pos, this.panelOutputSecond.pos);
            double maxX = Math.max(Math.max(plane.p1.x, plane.p2.x), plane.p3.x);
            double maxY = Math.max(Math.max(plane.p1.y, plane.p2.y), plane.p3.y);
            double maxZ = Math.max(Math.max(plane.p1.z, plane.p2.z), plane.p3.z);

            double minX = Math.min(Math.min(plane.p1.x, plane.p2.x), plane.p3.x);
            double minY = Math.min(Math.min(plane.p1.y, plane.p2.y), plane.p3.y);
            double minZ = Math.min(Math.min(plane.p1.z, plane.p2.z), plane.p3.z);

            center = new AxisAlignedBB(maxX, maxY, maxZ, minX, minY, minZ);
        }
        List<Entity> entityList = this.world.getEntitiesWithinAABBExcludingEntity(null, center);

        entityList.forEach((entity -> {
            if (PanelHelper.isEntityTouchPanel(entity, this.pos, this.panelOutputFirst.pos, this.panelOutputSecond.pos, 0.5f)) {
                ReleaseAttribute attribute = new ReleaseAttribute(null, null, entity, element_data.getTickTime(), element_data.getForce());
                MagickReleaseHelper.applyElementFunction(element_data.getElement(), element_data.getManaType(), attribute);
            }
        }));

        if (this.world.isRemote && !RenderEvent.isTileEntityActivated(this)) {
            RenderEvent.activeTileEntityRender(this);
        }

        this.mana -= REQUIRE;
    }

    @Override
    public IPanelTileEntity getOutputFirst() {
        return panelOutputFirst;
    }

    @Override
    public IPanelTileEntity getOutputSecond() {
        return panelOutputSecond;
    }

    @Override
    public boolean isClosed() {
        return PanelHelper.isPanelClosed(this);
    }

    @Override
    public void touch(@Nonnull LifeStateEntity entity) {
        requiredMana = entity.getElementData().getMana() + mana;
        mana = requiredMana;
        lifeState = entity;
        element_data.setElement(entity.getElementData().getElement());
        element_data.setRange(entity.getElementData().getRange());
        element_data.setForce(entity.getElementData().getForce());
        element_data.setManaType(entity.getElementData().getManaType());
        element_data.setTickTime(entity.getElementData().getTickTime());
        updateInfo();
    }

    protected void updateInfo() {
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
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
        storageTag(compoundNBT);
        return compoundNBT;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        extractTag(tag);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        extractTag(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        storageTag(compound);
        return super.write(compound);
    }

    public void extractTag(CompoundNBT compound) {
        this.requiredMana = compound.getFloat("requiredMana");
        this.mana = compound.getFloat("mana");
        ManaDataHandler.deserializeData(compound, getElementData());
    }

    public void storageTag(CompoundNBT compound) {
        compound.putFloat("requiredMana", this.requiredMana);
        compound.putFloat("mana", this.mana);
        ManaDataHandler.serializeData(compound, getElementData());
    }

    @Override
    public IManaData getElementData() {
        return element_data;
    }
}
