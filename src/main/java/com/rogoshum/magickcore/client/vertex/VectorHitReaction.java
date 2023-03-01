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
        this.hitReaction = 0.5f;
        this.reactionDecline = 0.1f;
    }

    public VectorHitReaction(Vec3 vector, float hitReaction, float reactionDecline) {
        this.vec = vector;
        this.maxReaction = hitReaction*0.25f;
        this.reactionDecline = reactionDecline*0.25f;
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

    /*
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

     */

    public float IsHit(Vec3 vector) {
        double uMagnitude = Math.sqrt(vector.x*vector.x + vector.y*vector.y + vector.z*vector.z);
        double vMagnitude = Math.sqrt(this.vec.x*this.vec.x + this.vec.y*this.vec.y + this.vec.z*this.vec.z);

        double dotProduct = vector.x*this.vec.x + vector.y*this.vec.y + vector.z*this.vec.z;

        float similarity = (float) ((dotProduct / (uMagnitude * vMagnitude) + 1) / 2);

        return similarity < 0.9f ? 0 : similarity * this.hitReaction;
    }

    public float getHitReaction() {
        return this.hitReaction;
    }

    public boolean isInvalid() {
        return this.hitReaction < 0;
    }
}
