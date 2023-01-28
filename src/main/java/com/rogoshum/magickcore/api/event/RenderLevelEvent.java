package com.rogoshum.magickcore.api.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.LevelRenderer;

public class RenderLevelEvent {
    public static class PreRenderMagickEvent extends Event {
        private final LevelRenderer context;
        private final PoseStack mat;
        private final float partialTicks;
        private final Matrix4f projectionMatrix;

        public PreRenderMagickEvent(LevelRenderer context, PoseStack mat, float partialTicks, Matrix4f projectionMatrix) {
            this.context = context;
            this.mat = mat;
            this.partialTicks = partialTicks;
            this.projectionMatrix = projectionMatrix;
        }

        public LevelRenderer getContext()
        {
            return context;
        }

        public PoseStack getPoseStack()
        {
            return mat;
        }

        public float getPartialTicks()
        {
            return partialTicks;
        }

        public Matrix4f getProjectionMatrix()
        {
            return projectionMatrix;
        }
    }

    public static class RenderMagickEvent extends Event
    {
        private final LevelRenderer context;
        private final PoseStack mat;
        private final float partialTicks;
        private final Matrix4f projectionMatrix;

        public RenderMagickEvent(LevelRenderer context, PoseStack mat, float partialTicks, Matrix4f projectionMatrix)
        {
            this.context = context;
            this.mat = mat;
            this.partialTicks = partialTicks;
            this.projectionMatrix = projectionMatrix;
        }

        public LevelRenderer getContext()
        {
            return context;
        }

        public PoseStack getPoseStack()
        {
            return mat;
        }

        public float getPartialTicks()
        {
            return partialTicks;
        }

        public Matrix4f getProjectionMatrix()
        {
            return projectionMatrix;
        }
    }

    public static class PostRenderMagickEvent extends Event
    {
        private final LevelRenderer context;
        private final PoseStack mat;
        private final float partialTicks;
        private final Matrix4f projectionMatrix;

        public PostRenderMagickEvent(LevelRenderer context, PoseStack mat, float partialTicks, Matrix4f projectionMatrix)
        {
            this.context = context;
            this.mat = mat;
            this.partialTicks = partialTicks;
            this.projectionMatrix = projectionMatrix;
        }

        public LevelRenderer getContext()
        {
            return context;
        }

        public PoseStack getPoseStack()
        {
            return mat;
        }

        public float getPartialTicks()
        {
            return partialTicks;
        }

        public Matrix4f getProjectionMatrix()
        {
            return projectionMatrix;
        }
    }
}
