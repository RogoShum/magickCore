package com.rogoshum.magickcore.client.entity.easyrender.projectile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.projectile.ManaLaserEntity;
import com.rogoshum.magickcore.common.entity.projectile.RayEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.function.Consumer;

public class RayRenderer extends EasyRenderer<RayEntity> {
    private static final ResourceLocation LASER_TOP = new ResourceLocation(MagickCore.MOD_ID,  "textures/laser/ray_top.png");
    private static final ResourceLocation LASER_MID = new ResourceLocation(MagickCore.MOD_ID,  "textures/laser/ray_mid.png");
    private static final ResourceLocation LASER_BOTTOM = new ResourceLocation(MagickCore.MOD_ID,  "textures/laser/ray_bottom.png");
    private float length;


    public RayRenderer(RayEntity entity) {
        super(entity);
    }

    @Override
    public void baseOffset(MatrixStack matrixStackIn) {
        super.baseOffset(matrixStackIn);
        Vector3d dir = entity.getMotion().scale(-1).normalize();
        Vector2f rota = getRotationFromVector(dir);
        float scale = 0.15f * entity.getWidth();
        double length = this.length * scale;
        matrixStackIn.translate(dir.x * length - dir.x * entity.getWidth(), dir.y * length - dir.y * entity.getWidth(), dir.z * length - dir.z * entity.getWidth());
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(rota.x));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(rota.y));
        matrixStackIn.scale(scale, scale, scale);
    }

    @Override
    public void update() {
        super.update();
        length = (float) Math.max(entity.getMotion().length() * 200 + 1, 1);
    }

    public void renderTop(RenderParams params) {
        baseOffset(params.matrixStack);
        RenderHelper.renderLaserTop(
                BufferContext.create(params.matrixStack, params.buffer, RenderHelper.getTexedLaser(LASER_TOP)),
                new RenderHelper.RenderContext(1.0f, entity.spellContext().element.color(), RenderHelper.renderLight),
                length
        );
    }

    public void renderMid(RenderParams params) {
        baseOffset(params.matrixStack);
        RenderHelper.renderLaserMid(
                BufferContext.create(params.matrixStack, params.buffer, RenderHelper.getTexedLaser(LASER_MID)),
                new RenderHelper.RenderContext(1.0f, entity.spellContext().element.color(), RenderHelper.renderLight),
                length
        );
    }

    public void renderBottom(RenderParams params) {
        baseOffset(params.matrixStack);
        RenderHelper.renderLaserBottom(
                BufferContext.create(params.matrixStack, params.buffer, RenderHelper.getTexedLaser(LASER_BOTTOM)),
                new RenderHelper.RenderContext(1.0f, entity.spellContext().element.color(), RenderHelper.renderLight),
                length
        );
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(RenderHelper.getTexedLaser(LASER_TOP)), this::renderTop);
        map.put(new RenderMode(RenderHelper.getTexedLaser(LASER_MID)), this::renderMid);
        map.put(new RenderMode(RenderHelper.getTexedLaser(LASER_BOTTOM)), this::renderBottom);
        return map;
    }
}