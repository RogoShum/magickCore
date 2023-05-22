package com.rogoshum.magickcore.client.entity.easyrender.laser;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.superentity.RadianceWellEntity;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

public class RadianceWellLaserRenderer extends EasyRenderer<RadianceWellEntity> {
    Queue<Vec3> DIRECTION;
    final RenderType TYPE = RenderHelper.getTexturedLaserGlint(entity.spellContext().element().getRenderer().getWaveTexture(1), 1.0f);

    public RadianceWellLaserRenderer(RadianceWellEntity entity) {
        super(entity);
    }

    @Override
    public void baseOffset(PoseStack matrixStackIn) {
        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double camX = cam.x, camY = cam.y, camZ = cam.z;
        matrixStackIn.translate(x - camX, y - camY + entity.getBbHeight() + 0.005, z - camZ);
    }

    @Override
    public void update() {
        super.update();
        if(entity.initial) {
            DIRECTION = Queues.newArrayDeque();
            List<Entity> livings = entity.findEntity((living) -> living instanceof LivingEntity && MagickReleaseHelper.sameLikeOwner(entity.getCaster(), living));
            for (Entity entity : livings) {
                Vec3 me = getEntityRenderVector(Minecraft.getInstance().getFrameTime()).add(0, this.entity.getBbHeight(), 0);
                Vec3 it = getEntityRenderVector(entity, Minecraft.getInstance().getFrameTime()).add(0, entity.getBbHeight() * 0.5, 0);
                Vec3 dirc = me.subtract(it);
                float distance = (float) dirc.length();
                dirc = dirc.normalize();
                Vec2 rota = getRotationFromVector(dirc);
                DIRECTION.add(new Vec3(rota.x, rota.y, distance));
            }
        }

    }

    public void render(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.scale(0.5f, 0.5f, 0.5f);
        Queue<Vec3> direction = DIRECTION;
        for (Vec3 vector3d : direction) {
            matrixStackIn.pushPose();
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees((float) vector3d.x));
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((float) vector3d.y));
            matrixStackIn.scale(1, (float) (vector3d.z * 2), 1);
            RenderHelper.renderLaserParticle(
                    BufferContext.create(matrixStackIn, params.buffer, TYPE)
                    , new RenderHelper.RenderContext(1f, this.entity.spellContext().element().primaryColor()));
            matrixStackIn.popPose();
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
            map.put(new RenderMode(TYPE, RenderMode.ShaderList.BITS_SMALL_SHADER), this::render);
        return map;
    }
}
