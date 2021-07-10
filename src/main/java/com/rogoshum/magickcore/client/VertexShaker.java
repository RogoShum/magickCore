package com.rogoshum.magickcore.client;

import com.rogoshum.magickcore.MagickCore;
import net.minecraft.util.math.vector.Vector3d;

import java.util.HashMap;
import java.util.Iterator;

public class VertexShaker {
    protected Vector3d point;
    private float limit;
    private Vector3d position;
    private Vector3d motion;
    private int tick;

    public VertexShaker(Vector3d point, float limit)
    {
        this.point = point;
        this.position = point;
        this.limit = limit;
        this.motion = new Vector3d(0, 0, 0);
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

    public Vector3d getPositionVec()
    {
        return this.position;
    }
}
