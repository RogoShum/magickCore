package com.rogoshum.magickcore.api.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.GameRenderer;

public class RenderWorldEvent {
    public static class PreRenderMagickEvent extends Event {
        private final GameRenderer context;
        private final PoseStack mat;
        private final float partialTicks;
        private final Matrix4f projectionMatrix;

        public PreRenderMagickEvent(GameRenderer context, PoseStack mat, float partialTicks, Matrix4f projectionMatrix) {
            this.context = context;
            this.mat = mat;
            this.partialTicks = partialTicks;
            this.projectionMatrix = projectionMatrix;
        }

        public GameRenderer getContext()
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
        private final GameRenderer context;
        private final PoseStack mat;
        private final float partialTicks;
        private final Matrix4f projectionMatrix;

        public RenderMagickEvent(GameRenderer context, PoseStack mat, float partialTicks, Matrix4f projectionMatrix)
        {
            this.context = context;
            this.mat = mat;
            this.partialTicks = partialTicks;
            this.projectionMatrix = projectionMatrix;
        }

        public GameRenderer getContext()
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
        private final GameRenderer context;
        private final PoseStack mat;
        private final float partialTicks;
        private final Matrix4f projectionMatrix;

        public PostRenderMagickEvent(GameRenderer context, PoseStack mat, float partialTicks, Matrix4f projectionMatrix)
        {
            this.context = context;
            this.mat = mat;
            this.partialTicks = partialTicks;
            this.projectionMatrix = projectionMatrix;
        }

        public GameRenderer getContext()
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
