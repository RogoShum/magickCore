package com.rogoshum.magickcore.client.entity.easyrender.radiation;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.radiation.RayTraceEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.function.Consumer;

public class RayRadiateRenderer extends EasyRenderer<RayTraceEntity> {
    private static final ResourceLocation LASER_TOP = new ResourceLocation(MagickCore.MOD_ID,  "textures/laser/ray_top.png");
    private static final ResourceLocation LASER_MID = new ResourceLocation(MagickCore.MOD_ID,  "textures/laser/ray_mid.png");
    private static final ResourceLocation LASER_BOTTOM = new ResourceLocation(MagickCore.MOD_ID,  "textures/laser/ray_bottom.png");
    private float length;

    public RayRadiateRenderer(RayTraceEntity entity) {
        super(entity);
    }

    @Override
    public void baseOffset(MatrixStack matrixStackIn) {
        super.baseOffset(matrixStackIn);
        Vector3d dir = Vector3d.ZERO;
        if(entity.spellContext().containChild(LibContext.DIRECTION))
            dir = entity.spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize().scale(-1);
        else if (entity.getOwner() != null)
            dir = entity.getOwner().getLookAngle().normalize();
        Vector2f rota = getRotationFromVector(dir);
        float scale = 0.25f;
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rota.x));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(rota.y));
        matrixStackIn.scale(scale, scale, scale);
    }

    @Override
    public boolean forceRender() {
        return entity.isAlive();
    }

    @Override
    public void update() {
        super.update();
        length = entity.spellContext().range * 20;
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
