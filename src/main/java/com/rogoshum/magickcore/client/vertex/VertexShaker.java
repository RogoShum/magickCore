package com.rogoshum.magickcore.client.vertex;

import com.rogoshum.magickcore.MagickCore;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Iterator;

public class VertexShaker {
    protected Vec3 point;
    private float limit;
    private Vec3 position;
    private Vec3 motion;
    private int tick;

    public VertexShaker(Vec3 point, float limit)
    {
        this.point = point;
        this.position = point;
        this.limit = limit;
        this.motion = new Vec3(0, 0, 0);
    }

    public void update()
    {
        if(tick % 5 == 0)
            this.motion = this.motion.add(MagickCore.getNegativeToOne() * limit * 0.03, MagickCore.getNegativeToOne() * limit * 0.03, MagickCore.getNegativeToOne() * limit * 0.03);

        if(this.motion.x > this.limit)
            this.motion.subtract(this.motion.x - MagickCore.getNegativeToOne() * limit * 0.03, 0, 0);

        if(this.motion.x < -this.limit)
            this.motion.add(MagickCore.getNegativeToOne() * limit * 0.03 - this.motion.x, 0, 0);

        if(this.motion.y > this.limit)
            this.motion.subtract(0, this.motion.y - MagickCore.getNegativeToOne() * limit * 0.03, 0);

        if(this.motion.y < -this.limit)
            this.motion.add(0, MagickCore.getNegativeToOne() * limit * 0.03 - this.motion.y, 0);

        if(this.motion.z > this.limit)
            this.motion.subtract(0, 0, this.motion.z - MagickCore.getNegativeToOne() * limit * 0.03);

        if(this.motion.z < -this.limit)
            this.motion.add(0, 0, MagickCore.getNegativeToOne() * limit * 0.03 - this.motion.z);

        this.position = this.point.add(this.motion);

        tick++;
    }

    public Vec3 getPositionVec()
    {
        return this.position;
    }
}
