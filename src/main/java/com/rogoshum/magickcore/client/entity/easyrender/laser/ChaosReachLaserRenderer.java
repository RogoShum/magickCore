package com.rogoshum.magickcore.client.entity.easyrender.laser;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.superentity.ChaoReachEntity;
import com.rogoshum.magickcore.tool.MagickReleaseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.Iterator;

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
        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            matrixStackIn.scale(2f, 2f, 2f);
            HashMap<Integer, TrailParticle> trace = entityIn.getTraceEntity();
            Iterator<Integer> ite = trace.keySet().iterator();
            while (ite.hasNext()) {
                int id = ite.next();
                Entity entity = entityIn.world.getEntityByID(id);
                if(entity != null && !MagickReleaseHelper.sameLikeOwner(entityIn.getOwner(), entity) && MagickReleaseHelper.canEntityTraceAnother(entityIn, entity)) {
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
                    entityIn.getElement().getRenderer().renderLaserParticle(matrixStackIn, bufferIn, entityIn.getElement().getRenderer().getElcTexture(3), 1.0f, distance, 0.5f);
                    entityIn.getElement().getRenderer().renderLaserParticle(matrixStackIn, bufferIn, entityIn.getElement().getRenderer().getWaveTexture(entityIn.getEntityId() % 2 == 0 ? 0 : 1), 1.0f, distance, 1.0f);
                    //entityIn.getElement().getRenderer().renderLaserParticle(matrixStackIn, bufferIn, entityIn.getElement().getRenderer().getLaserbeamTexture(), 0.5f, distance, 1 / distance);
                    matrixStackIn.pop();
                    matrixStackIn.pop();
                }
            }
        }
    }
}
