package com.rogoshum.magickcore.entity.base;

import com.rogoshum.magickcore.magick.MagickElement;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public abstract class ManaPointEntity extends ManaEntity {
    private Vector3d point;
    public ManaPointEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }
    public ManaPointEntity(EntityType<?> entityTypeIn, World worldIn, MagickElement element) {
        super(entityTypeIn, worldIn, element);
    }
    @Override
    public void tick() {
        super.tick();
        if(point == null)
            this.point = this.getPositionVec();
        else
            this.setPosition(this.point.x, this.point.y, this.point.z);
    }
}
