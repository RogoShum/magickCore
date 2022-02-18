package com.rogoshum.magickcore.api.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Event;

public class RenderWorldEvent {
    public static class PreRenderMagickEvent extends Event
    {
        private final WorldRenderer context;
        private final MatrixStack mat;
        private final float partialTicks;
        private final Matrix4f projectionMatrix;

        public PreRenderMagickEvent(WorldRenderer context, MatrixStack mat, float partialTicks, Matrix4f projectionMatrix)
        {
            this.context = context;
            this.mat = mat;
            this.partialTicks = partialTicks;
            this.projectionMatrix = projectionMatrix;
        }

        public WorldRenderer getContext()
        {
            return context;
        }

        public MatrixStack getMatrixStack()
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
        private final WorldRenderer context;
        private final MatrixStack mat;
        private final float partialTicks;
        private final Matrix4f projectionMatrix;

        public RenderMagickEvent(WorldRenderer context, MatrixStack mat, float partialTicks, Matrix4f projectionMatrix)
        {
            this.context = context;
            this.mat = mat;
            this.partialTicks = partialTicks;
            this.projectionMatrix = projectionMatrix;
        }

        public WorldRenderer getContext()
        {
            return context;
        }

        public MatrixStack getMatrixStack()
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
        private final WorldRenderer context;
        private final MatrixStack mat;
        private final float partialTicks;
        private final Matrix4f projectionMatrix;

        public PostRenderMagickEvent(WorldRenderer context, MatrixStack mat, float partialTicks, Matrix4f projectionMatrix)
        {
            this.context = context;
            this.mat = mat;
            this.partialTicks = partialTicks;
            this.projectionMatrix = projectionMatrix;
        }

        public WorldRenderer getContext()
        {
            return context;
        }

        public MatrixStack getMatrixStack()
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
