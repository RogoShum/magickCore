package com.rogoshum.magickcore.api.event;

import com.mojang.blaze3d.vertex.PoseStack;

public class RenderGameOverlayEvent extends Event {
    private final PoseStack poseStack;
    private final float f;
    public RenderGameOverlayEvent(PoseStack poseStack, float f) {
        this.poseStack = poseStack;
        this.f = f;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public float getF() {
        return f;
    }
}
