package com.rogoshum.magickcore.client.entity.easyrender.laser;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.superentity.RadianceWellEntity;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

public class RadianceWellLaserRenderer extends EasyRenderer<RadianceWellEntity> {
    Queue<Vector3d> DIRECTION;

    public RadianceWellLaserRenderer(RadianceWellEntity entity) {
        super(entity);
    }

    @Override
    public void baseOffset(MatrixStack matrixStackIn) {
        Vector3d cam = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        matrixStackIn.translate(x - camX, y - camY + entity.getHeight() + 0.005, z - camZ);
    }

    @Override
    public void update() {
        super.update();
        if(entity.initial) {
            DIRECTION = Queues.newArrayDeque();
            List<Entity> livings = entity.findEntity((living) -> living instanceof LivingEntity && MagickReleaseHelper.sameLikeOwner(entity.getOwner(), living));
            for (Entity entity : livings) {
                Vector3d me = getEntityRenderVector(Minecraft.getInstance().getRenderPartialTicks()).add(0, this.entity.getHeight(), 0);
                Vector3d it = getEntityRenderVector(entity, Minecraft.getInstance().getRenderPartialTicks()).add(0, entity.getHeight() * 0.5, 0);
                Vector3d dirc = me.subtract(it);
                float distance = (float) dirc.length();
                dirc = dirc.normalize();
                Vector2f rota = getRotationFromVector(dirc);
                DIRECTION.add(new Vector3d(rota.x, rota.y, distance));
            }
        }
    }

    public void render(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.scale(0.5f, 0.5f, 0.5f);
        Queue<Vector3d> direction = DIRECTION;
        for (Vector3d vector3d : direction) {
            matrixStackIn.push();
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float) vector3d.x));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((float) vector3d.y));
            entity.spellContext().element.getRenderer().renderLaserParticle(
                    matrixStackIn, params.buffer, entity.spellContext().element.getRenderer().getWaveTexture(1), 0.35f, (float) (vector3d.z * 2), 2.0f);
            matrixStackIn.pop();
        }
    }

    @Override
    public boolean forceRender() {
        return true;
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        if(entity.initial)
            map.put(RenderMode.ORIGIN_RENDER, this::render);
        return map;
    }
}
