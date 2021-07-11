package com.rogoshum.magickcore.client.entity.easyrender.laser;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.entity.easyrender.EasyRenderer;
import com.rogoshum.magickcore.client.particle.TrailParticle;
import com.rogoshum.magickcore.entity.superentity.RadianceWellEntity;
import com.rogoshum.magickcore.helper.MagickReleaseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.Iterator;

public class RadianceWellLaserRenderer extends EasyRenderer<RadianceWellEntity> {
    @Override
    public void preRender(RadianceWellEntity entity, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        matrixStackIn.push();
        double x = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * (double) partialTicks;
        double y = entity.lastTickPosY + (entity.getPosY() - entity.lastTickPosY) * (double) partialTicks;
        double z = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * (double) partialTicks;

        Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        matrixStackIn.translate(x - camX, y - camY + entity.getHeight() + 0.005, z - camZ);

        render(entity, matrixStackIn, bufferIn, partialTicks);
        postRender(entity, matrixStackIn, bufferIn, partialTicks);
    }

    @Override
    public void render(RadianceWellEntity entityIn, MatrixStack matrixStackIn, IRenderTypeBuffer.Impl bufferIn, float partialTicks) {
        if(entityIn.getElement() != null && entityIn.getElement().getRenderer() != null) {
            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            HashMap<Integer, TrailParticle> trace = entityIn.getTraceEntity();
            Iterator<Integer> ite = trace.keySet().iterator();
            while (ite.hasNext()) {
                int id = ite.next();
                Entity entity = entityIn.world.getEntityByID(id);
                if(MagickReleaseHelper.sameLikeOwner(entityIn.getOwner(), entity)) {
                    Vector3d dirc = entityIn.getPositionVec().add(0, entityIn.getHeight(), 0).subtract(entity.getPositionVec().add(0, entityIn.getHeight() / 2, 0));
                    float distance = (float) dirc.length();
                    dirc = dirc.normalize();
                    Vector2f rota = getRotationFromVector(dirc);
                    matrixStackIn.push();
                    //matrixStackIn.translate(0, distance, 0);
                    //matrixStackIn.rotate(Vector3f.XP.rotationDegrees(30));
                    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(rota.x + 90));
                    matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(rota.y - 90));
                    entityIn.getElement().getRenderer().renderLaserParticle(matrixStackIn, bufferIn, entityIn.getElement().getRenderer().getWaveTexture(1), 0.35f, distance * 2);
                    matrixStackIn.pop();
                }
            }
        }
    }
}
