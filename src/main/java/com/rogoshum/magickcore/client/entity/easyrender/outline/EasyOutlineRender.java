package com.rogoshum.magickcore.client.entity.easyrender.outline;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.entity.baseEntity.ManaEntity;
import com.rogoshum.magickcore.entity.baseEntity.ManaProjectileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

public abstract class EasyOutlineRender<T extends Entity> extends EasyRenderer<Entity> {

    public void preRender(T entity, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks)
    {if(entity instanceof ManaEntity && !((ManaEntity) entity).cansee)
        return;

        if(entity instanceof ManaProjectileEntity && !((ManaProjectileEntity) entity).cansee)
            return;

        matrixStackIn.push();
        double x = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * (double) partialTicks;
        double y = entity.lastTickPosY + (entity.getPosY() - entity.lastTickPosY) * (double) partialTicks;
        double z = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * (double) partialTicks;

        Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        matrixStackIn.translate(x - camX, y - camY + entity.getHeight() / 2, z - camZ);
        render(entity, matrixStackIn, bufferIn, partialTicks);
        matrixStackIn.pop();
    }

    @Override
    public void preRender(Entity entity, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {}

    @Override
    public void render(Entity entity, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {}

    @Override
    public void postRender(Entity entity, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {}

    public abstract void render(T entity, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks);

    private static void posVertex(MatrixStack.Entry matrixEntryIn, BufferBuilder bufferIn, VectorHitReaction[] hitReaction, float[] color, float scale, float alphaIn, float xIn, float yIn, float zIn, float texU, float texV) {
        bufferIn.pos(matrixEntryIn.getMatrix(), xIn, yIn, zIn).color(color[0], color[1], color[2], alphaIn).tex(texU, texV).overlay(OverlayTexture.NO_OVERLAY).lightmap(15728880).normal(matrixEntryIn.getNormal(), xIn, yIn, zIn).endVertex();
    }
}
