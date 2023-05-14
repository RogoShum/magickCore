package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.pointed.GravityLiftEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.context.child.DirectionContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;

import java.util.HashMap;
import java.util.function.Consumer;

public class GravityLiftRenderer extends EasyRenderer<GravityLiftEntity> {
    float height;
    float c;
    RenderHelper.CylinderContext INNER_CYLINDER = new RenderHelper.CylinderContext(0.2f, 0.1f, 2f
            , 0, 1.0f
            , 0.0f, 0.5f, 0.3f);
    RenderHelper.CylinderContext BASE_CYLINDER = new RenderHelper.CylinderContext(2f, 2f, 2f
            , 0, 0.2f
            , 0.0f, 0.8f, 0.1f);
    RenderHelper.CylinderContext OUTER_CYLINDER = new RenderHelper.CylinderContext(1.2f, 1.2f, 2f
            , 0, 1.0f
            , 0.0f, 0.7f, 0.4f);

    public GravityLiftRenderer(GravityLiftEntity entity) {
        super(entity);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        RenderType inner = RenderHelper.getTexturedUniGlint(wind, height, 0f);
        map.put(new RenderMode(inner, RenderMode.ShaderList.BITS_SMALL_SHADER), this::renderInner);

        RenderType base = RenderHelper.getTexturedUniGlint(wind, 0.5f, 0f);
        map.put(new RenderMode(base, RenderMode.ShaderList.BITS_SMALL_SHADER), this::renderBase);

        RenderType Outer = RenderHelper.getTexturedUniGlint(wind_round, height*0.25f, 0f);
        map.put(new RenderMode(Outer, RenderMode.ShaderList.BITS_SMALL_SHADER), this::renderOuter);
        return map;
    }

    @Override
    public void baseOffset(PoseStack matrixStackIn) {
        super.baseOffset(matrixStackIn);
        if(entity.spellContext().containChild(LibContext.DIRECTION)) {
            Vec3 dir = entity.spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.scale(-1);
            Vec2 rota = getRotationFromVector(dir);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(rota.x));
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(rota.y));
        }
        matrixStackIn.translate(0, 0, 0);
    }

    @Override
    public void update() {
        super.update();
        height = (float) (entity.liftHeight() * Math.min((entity.tickCount * 0.05), 1));
        c = entity.tickCount % 20;
    }

    private void renderLaser(RenderParams renderParams) {
    }

    private void renderInner(RenderParams renderParams) {
        PoseStack matrixStackIn = renderParams.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.translate(0,  height*0.5, 0);
        matrixStackIn.scale(1, height*0.9f, 1);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180));
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(360f * (c / 19)));
        RenderHelper.renderCylinderCache(BufferContext.create(matrixStackIn, renderParams.buffer, RenderHelper.getTexturedUniGlint(
                        wind, height, 0f))
                , INNER_CYLINDER, new RenderHelper.RenderContext(1.0f, entity.spellContext().element.secondaryColor()));
    }

    private void renderOuter(RenderParams renderParams) {
        PoseStack matrixStackIn = renderParams.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.translate(0,  height*0.5, 0);
        matrixStackIn.scale(1, height*0.9f, 1);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180));
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(360f * (c / 19)));
        //matrixStackIn.translate(0,  -height, 0);
        c = entity.tickCount % 30;
        //matrixStackIn.translate(0,  height - 0.5 + (-c / 29 * 2), 0);
        RenderType renderType = RenderHelper.getTexturedUniGlint(wind, height, 0f);
        RenderHelper.renderCylinderCache(BufferContext.create(matrixStackIn, renderParams.buffer, renderType)
                , OUTER_CYLINDER, new RenderHelper.RenderContext(1.0f, entity.spellContext().element.primaryColor()));
    }

    private void renderBase(RenderParams renderParams) {
        baseOffset(renderParams.matrixStack);
        PoseStack matrixStackIn = renderParams.matrixStack;
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180));
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(360f * (c / 19)));
        RenderType renderType = RenderHelper.getTexturedUniGlint(wind, 0.5f, 0f);
        RenderHelper.renderCylinderCache(BufferContext.create(matrixStackIn, renderParams.buffer, renderType)
                , BASE_CYLINDER, new RenderHelper.RenderContext(1.0f, entity.spellContext().element.primaryColor()));

        matrixStackIn.pushPose();
        matrixStackIn.translate(0, -0.5, 0);
        matrixStackIn.scale(1.2f, 2f, 1.2f);
        RenderHelper.renderCylinderCache(BufferContext.create(matrixStackIn, renderParams.buffer, renderType)
                , BASE_CYLINDER, new RenderHelper.RenderContext(1.0f, entity.spellContext().element.primaryColor()));
        matrixStackIn.popPose();
    }

    @Override
    public boolean forceRender() {
        return true;
    }
}
