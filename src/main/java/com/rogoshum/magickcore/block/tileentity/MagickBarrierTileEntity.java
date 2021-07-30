package com.rogoshum.magickcore.block.tileentity;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.event.RenderEvent;
import com.rogoshum.magickcore.helper.PanelHelper;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModTileEntities;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;

public class MagickBarrierTileEntity extends CanSeeTileEntity implements ITickableTileEntity, IPanelTileEntity{
    private MagickBarrierTileEntity panelInputFirst;
    private MagickBarrierTileEntity panelInputSecond;
    private MagickBarrierTileEntity panelOutputFirst;
    private MagickBarrierTileEntity panelOutputSecond;

    public MagickBarrierTileEntity() {
        super(ModTileEntities.magick_barrier_tileentity.get());
    }

    @Override
    public void tick() {
        ticksExisted++;
        boolean flag = false;
        if(this.panelInputFirst != null && this.panelInputFirst.removed)
            flag = true;

        if(this.panelInputSecond != null && this.panelInputSecond.removed)
            flag = true;

        if(this.panelOutputFirst != null && this.panelOutputFirst.removed)
            flag = true;

        if(this.panelOutputSecond != null && this.panelOutputSecond.removed)
            flag = true;

        if(flag)
        {
            this.panelInputFirst = null;
            this.panelInputSecond = null;
            this.panelOutputFirst = null;
            this.panelOutputSecond = null;
        }

        if(!this.isClosed())
            tryLinksOthers();
        else{
            doPlaneThings();
        }
    }

    private boolean suitableForLink(TileEntity tile)
    {
        boolean flag = tile != this && tile instanceof MagickBarrierTileEntity && !((MagickBarrierTileEntity) tile).isClosed() && distanceTile(tile) <= 8 &&
                ((MagickBarrierTileEntity) tile).panelOutputFirst != this && ((MagickBarrierTileEntity) tile).panelOutputSecond != this;

        if(flag)
        {
            if(this.panelInputFirst != null && (this.panelInputFirst.panelInputFirst == tile || this.panelInputFirst.panelInputSecond == tile))
                return false;

            if(this.panelInputSecond != null && (this.panelInputSecond.panelInputFirst == tile || this.panelInputSecond.panelInputSecond == tile))
                return false;
        }

        return flag;
    }

    private void tryLinksOthers() {
        this.world.tickableTileEntities.forEach((tile) -> {
            if(suitableForLink(tile)) {
                if (this.panelOutputFirst == null && this.panelOutputSecond != tile) {
                    if (((MagickBarrierTileEntity) tile).panelInputFirst == null) {
                        ((MagickBarrierTileEntity) tile).panelInputFirst = this;
                        this.panelOutputFirst = (MagickBarrierTileEntity) tile;
                    } else if (((MagickBarrierTileEntity) tile).panelInputSecond == null) {
                        ((MagickBarrierTileEntity) tile).panelInputSecond = this;
                        this.panelOutputFirst = (MagickBarrierTileEntity) tile;
                    }
                }

                if (this.panelOutputSecond == null && this.panelOutputFirst != tile) {
                    if (((MagickBarrierTileEntity) tile).panelInputFirst == null) {
                        ((MagickBarrierTileEntity) tile).panelInputFirst = this;
                        this.panelOutputSecond = (MagickBarrierTileEntity) tile;
                    } else if (((MagickBarrierTileEntity) tile).panelInputSecond == null) {
                        ((MagickBarrierTileEntity) tile).panelInputSecond = this;
                        this.panelOutputSecond = (MagickBarrierTileEntity) tile;
                    }
                }
            }
        });
    }

    public double distanceTile(TileEntity tileEntity)
    {
        float f = (float)(this.pos.getX() - tileEntity.getPos().getX());
        float f1 = (float)(this.pos.getY() - tileEntity.getPos().getY());
        float f2 = (float)(this.pos.getZ() - tileEntity.getPos().getZ());
        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    private void doPlaneThings() {
        PanelHelper.Plane plane = new PanelHelper.Plane(this.pos, this.panelOutputFirst.pos, this.panelOutputSecond.pos);
        double maxX = Math.max(Math.max(plane.p1.x, plane.p2.x), plane.p3.x);
        double maxY = Math.max(Math.max(plane.p1.y, plane.p2.y), plane.p3.y);
        double maxZ = Math.max(Math.max(plane.p1.z, plane.p2.z), plane.p3.z);

        double minX = Math.min(Math.min(plane.p1.x, plane.p2.x), plane.p3.x);
        double minY = Math.min(Math.min(plane.p1.y, plane.p2.y), plane.p3.y);
        double minZ = Math.min(Math.min(plane.p1.z, plane.p2.z), plane.p3.z);

        AxisAlignedBB center = new AxisAlignedBB(maxX, maxY, maxZ, minX, minY, minZ);
        List<Entity> entityList = this.world.getEntitiesWithinAABBExcludingEntity(null, center);
        entityList.forEach((entity -> {
            if(PanelHelper.isEntityTouchPanel(entity, this.pos, this.panelOutputFirst.pos, this.panelOutputSecond.pos, 0.5f))
            {
                ModBuff.applyBuff(entity, LibBuff.FREEZE, 20, 1, true);
            }
        }));

        if(this.world.isRemote && !RenderEvent.isTileEntityActivated(this)) {
            RenderEvent.activeTileEntityRender(this);
        }
    }

    @Override
    public IPanelTileEntity getInputFirst() {
        return panelInputFirst;
    }

    @Override
    public IPanelTileEntity getInputSecond() {
        return panelInputSecond;
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
}
