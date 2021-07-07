package com.rogoshum.magickcore.entity.baseEntity;

import com.rogoshum.magickcore.magick.element.MagickElement;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.HashMap;

public abstract class ManaPointEntity extends ManaEntity{
    private Vector3d point;
    private static HashMap<Vector3d, Vector2f> riftPoint = new HashMap<Vector3d, Vector2f>();

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

    public HashMap<Vector3d, Vector2f> getRiftPoint() { return riftPoint; }
}
