package com.rogoshum.magickcore.client.entity.easyrender.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.projectile.ManaLaserEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;

import java.util.HashMap;
import java.util.function.Consumer;

public class ManaLaserRenderer extends EasyRenderer<ManaLaserEntity> {
    private static final ResourceLocation LASER_TOP = new ResourceLocation(MagickCore.MOD_ID,  "textures/laser/laser_top.png");
    private static final ResourceLocation LASER_MID = new ResourceLocation(MagickCore.MOD_ID,  "textures/laser/laser_mid.png");
    private static final ResourceLocation LASER_BOTTOM = new ResourceLocation(MagickCore.MOD_ID,  "textures/laser/laser_bottom.png");
    private float length;

    public ManaLaserRenderer(ManaLaserEntity entity) {
        super(entity);
    }

    @Override
    public void baseOffset(PoseStack matrixStackIn) {
        super.baseOffset(matrixStackIn);
        Vec3 dir = entity.getDeltaMovement().scale(-1).normalize();
        Vec2 rota = getRotationFromVector(dir);
        float scale = 0.5f * entity.getBbWidth();
        double length = this.length * scale;
        matrixStackIn.translate(dir.x * length - dir.x * entity.getBbWidth(), dir.y * length - dir.y * entity.getBbWidth(), dir.z * length - dir.z * entity.getBbWidth());
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rota.x));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(rota.y));
        matrixStackIn.scale(scale, scale, scale);
    }

    @Override
    public void update() {
        super.update();
        length = (float) Math.max(entity.getDeltaMovement().length() * 30 + 1, 1);
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
        map.put(new RenderMode(RenderHelper.getTexedLaser(LASER_TOP), RenderMode.ShaderList.BITS_SHADER), this::renderTop);
        map.put(new RenderMode(RenderHelper.getTexedLaser(LASER_MID), RenderMode.ShaderList.BITS_SHADER), this::renderMid);
        map.put(new RenderMode(RenderHelper.getTexedLaser(LASER_BOTTOM), RenderMode.ShaderList.BITS_SHADER), this::renderBottom);
        return map;
    }
}
