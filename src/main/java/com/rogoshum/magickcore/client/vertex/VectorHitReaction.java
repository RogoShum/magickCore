package com.rogoshum.magickcore.client.vertex;

import net.minecraft.world.phys.Vec3;

public class VectorHitReaction {
    private final Vec3 vec;
    private float hitReaction;
    private float maxReaction;
    private final float reactionDecline;

    private boolean touched;

    public VectorHitReaction(Vec3 vector) {
        this.vec = vector;
        this.hitReaction = 1f;
        this.reactionDecline = 0.2f;
    }

    public VectorHitReaction(Vec3 vector, float hitReaction, float reactionDecline) {
        this.vec = vector;
        this.maxReaction = hitReaction;
        this.reactionDecline = reactionDecline;
    }

    public VectorHitReaction touch() {
        this.touched = true;
        this.hitReaction = this.maxReaction;
        return this;
    }

    public void tick() {
        if (this.hitReaction >= this.maxReaction)
            touched = true;

        if (!touched)
            this.hitReaction += this.reactionDecline;

        if (touched && this.hitReaction >= 0)
            this.hitReaction -= this.reactionDecline;
    }

    public float IsHit(Vec3 vector) {
        Vec3 sub = vector.normalize().subtract(this.vec);
        float reaction = 0f;
        float dis = 0.25f;
        float add = (float) (Math.abs(sub.x) + Math.abs(sub.y) + Math.abs(sub.z));
        if (add <= dis * 3) {
            float distance = dis - add / 3.1f;
            if (distance <= 0)
                distance -= distance * 2;
            reaction += this.hitReaction * distance / dis;
        }

        return reaction;
    }

    public float getHitReaction() {
        return this.hitReaction;
    }

    public boolean isInvalid() {
        return this.hitReaction < 0;
    }
}
