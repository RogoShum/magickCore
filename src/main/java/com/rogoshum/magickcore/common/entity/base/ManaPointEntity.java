package com.rogoshum.magickcore.common.entity.base;

import com.rogoshum.magickcore.common.magick.MagickElement;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class ManaPointEntity extends ManaEntity {
    protected Vec3 point;
    public ManaPointEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }
    public ManaPointEntity(EntityType<?> entityTypeIn, Level worldIn, MagickElement element) {
        super(entityTypeIn, worldIn, element);
    }
    @Override
    public void tick() {
        super.tick();

        if(level.isClientSide) return;
        if(fixedPosition()) {
            if(point == null)
                this.point = this.position();
            else if(!point.equals(this.position())) {
                this.setPos(this.point.x, this.point.y, this.point.z);
            }
        }
    }

    protected boolean fixedPosition() {
        return true;
    }
}
