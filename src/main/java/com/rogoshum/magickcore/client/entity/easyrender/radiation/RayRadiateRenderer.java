package com.rogoshum.magickcore.client.entity.easyrender.radiation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.radiation.RayTraceEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;

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
    public void baseOffset(PoseStack matrixStackIn) {
        super.baseOffset(matrixStackIn);
        Vec3 dir = Vec3.ZERO;
        if(entity.spellContext().containChild(LibContext.DIRECTION))
            dir = entity.spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.normalize().scale(-1);
        else if (entity.getCaster() != null)
            dir = entity.getCaster().getLookAngle().normalize();
        Vec2 rota = getRotationFromVector(dir);
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
                new RenderHelper.RenderContext(1.0f, entity.spellContext().element.primaryColor(), RenderHelper.renderLight),
                length
        );
    }

    public void renderMid(RenderParams params) {
        baseOffset(params.matrixStack);
        RenderHelper.renderLaserMid(
                BufferContext.create(params.matrixStack, params.buffer, RenderHelper.getTexedLaser(LASER_MID)),
                new RenderHelper.RenderContext(1.0f, entity.spellContext().element.primaryColor(), RenderHelper.renderLight),
                length
        );
    }

    public void renderBottom(RenderParams params) {
        baseOffset(params.matrixStack);
        RenderHelper.renderLaserBottom(
                BufferContext.create(params.matrixStack, params.buffer, RenderHelper.getTexedLaser(LASER_BOTTOM)),
                new RenderHelper.RenderContext(1.0f, entity.spellContext().element.primaryColor(), RenderHelper.renderLight),
                length
        );
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(RenderHelper.getTexedLaser(LASER_TOP), RenderMode.ShaderList.BITS_SMALL_SHADER), this::renderTop);
        map.put(new RenderMode(RenderHelper.getTexedLaser(LASER_MID), RenderMode.ShaderList.BITS_SMALL_SHADER), this::renderMid);
        map.put(new RenderMode(RenderHelper.getTexedLaser(LASER_BOTTOM), RenderMode.ShaderList.BITS_SMALL_SHADER), this::renderBottom);
        return map;
    }
}
