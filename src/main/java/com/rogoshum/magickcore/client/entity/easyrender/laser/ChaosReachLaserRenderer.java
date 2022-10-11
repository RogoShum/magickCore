package com.rogoshum.magickcore.client.entity.easyrender.laser;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.superentity.ChaoReachEntity;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ChaosReachLaserRenderer extends EasyRenderer<ChaoReachEntity> {
    @Override
    public void preRender(ChaoReachEntity entity, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        if(!entity.initial)
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
    public void render(ChaoReachEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        if(entityIn.spellContext().element != null && entityIn.spellContext().element.getRenderer() != null) {
            matrixStackIn.scale(2f, 2f, 2f);
            List<Entity> trace = entityIn.findEntity(entity -> entity instanceof LivingEntity && !MagickReleaseHelper.sameLikeOwner(entityIn.getOwner(), entity) && MagickReleaseHelper.canEntityTraceAnother(entityIn, entity));
            for (Entity entity : trace) {
                Vector3d dirc = entityIn.getPositionVec().add(0, entityIn.getHeight() / 2, 0).subtract(getEntityRenderVector(entity, partialTicks).add(0, entity.getHeight() / 2, 0));
                float distance = (float) dirc.length();
                dirc = dirc.normalize();
                Vector2f rota = getRotationFromVector(dirc);
                matrixStackIn.push();
                //matrixStackIn.translate(0, distance, 0);
                //matrixStackIn.rotate(Vector3f.XP.rotationDegrees(30));
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(rota.x));
                matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(rota.y));
                matrixStackIn.push();
                matrixStackIn.scale(0.5f, 0.5f, 0.5f);
                entityIn.spellContext().element.getRenderer().renderLaserParticle(matrixStackIn, bufferIn, entityIn.spellContext().element.getRenderer().getElcTexture(3), 1.0f, distance, 0.5f);
                entityIn.spellContext().element.getRenderer().renderLaserParticle(matrixStackIn, bufferIn, entityIn.spellContext().element.getRenderer().getWaveTexture(entityIn.getEntityId() % 2 == 0 ? 0 : 1), 1.0f, distance, 1.0f);
                //entityIn.getElement().getRenderer().renderLaserParticle(matrixStackIn, bufferIn, entityIn.getElement().getRenderer().getLaserbeamTexture(), 0.5f, distance, 1 / distance);
                matrixStackIn.pop();
                matrixStackIn.pop();
            }
        }
    }
}
