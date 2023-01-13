package com.rogoshum.magickcore.client.entity.easyrender;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.pointed.GravityLiftEntity;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibShaders;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.Queue;
import java.util.function.Consumer;

public class GravityLiftRenderer extends EasyRenderer<GravityLiftEntity> {
    float height;
    float c;
    RenderHelper.CylinderContext INNER_CYLINDER;
    RenderHelper.CylinderContext BASE_CYLINDER;
    RenderHelper.CylinderContext AIR_CYLINDER;
    Queue<RenderHelper.CylinderContext> OUTER_CYLINDER;
    int outerCount;

    public GravityLiftRenderer(GravityLiftEntity entity) {
        super(entity);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        RenderType laser = RenderHelper.getTexedLaserGlint(entity.spellContext().element.getRenderer().getElcTexture(3), height * 0.1f);
        map.put(new RenderMode(laser, RenderMode.ShaderList.SLIME_SHADER), this::renderLaser);

        RenderType inner = RenderHelper.getTexedCylinderGlint(wind, height, 0f);
        map.put(new RenderMode(inner, RenderMode.ShaderList.SLIME_SHADER), this::renderInner);

        RenderType base = RenderHelper.getTexedCylinderGlint(wind, 0.5f, 0f);
        map.put(new RenderMode(base, RenderMode.ShaderList.OPACITY_SHADER), this::renderBase);

        return map;
    }

    @Override
    public void baseOffset(MatrixStack matrixStackIn) {
        super.baseOffset(matrixStackIn);
        if(entity.spellContext().containChild(LibContext.DIRECTION)) {
            Vector3d dir = entity.spellContext().<DirectionContext>getChild(LibContext.DIRECTION).direction.scale(-1);
            Vector2f rota = getRotationFromVector(dir);
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

        INNER_CYLINDER = new RenderHelper.CylinderContext(0.3f, 0.1f, 2f
                , height, 16
                , 0.0f, 1.0f, 0.3f, entity.spellContext().element.color());

        BASE_CYLINDER = new RenderHelper.CylinderContext(2f, 2f, 2f
                , 0.5f, 16
                , 0.0f, 0.8f, 0.1f, entity.spellContext().element.color());

        c = entity.tickCount % 30;
        Queue<RenderHelper.CylinderContext> cylinders = Queues.newArrayDeque();
        for (int i = 0; i < height; i+=2) {
            float radius = 1.2f;
            if(i % 4 == 0)
                radius += 0.3f * Math.sin(c / 29 * Math.PI);
            RenderHelper.CylinderContext context = new RenderHelper.CylinderContext(radius, radius, 2f
                    , 0.7f, 16
                    , 0.0f, 1.0f, 0.6f, entity.spellContext().element.color());

            cylinders.add(context);
        }
        OUTER_CYLINDER = cylinders;
    }

    private void renderLaser(RenderParams renderParams) {
        MatrixStack matrixStackIn = renderParams.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.translate(0,  -1, 0);
        matrixStackIn.scale(0.25f, 0.25f, 0.25f);
        RenderHelper.renderLaserParticle(
                BufferContext.create(matrixStackIn, renderParams.buffer, RenderHelper.getTexedLaserGlint(entity.spellContext().element.getRenderer().getElcTexture(3), height * 0.1f))
                , new RenderHelper.RenderContext(0.5f, entity.spellContext().element.color())
                , RenderHelper.EmptyVertexContext
                , height * 4.0f);
    }

    private void renderInner(RenderParams renderParams) {
        MatrixStack matrixStackIn = renderParams.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.translate(0,  height * 0.5-1, 0);

        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180));
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(360f * (c / 19)));
        if(INNER_CYLINDER != null)
            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, renderParams.buffer, RenderHelper.getTexedCylinderGlint(
                        wind, height, 0f))
                , INNER_CYLINDER);
    }

    private void renderOuter(RenderParams renderParams) {
        MatrixStack matrixStackIn = renderParams.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.translate(0,  height * 0.5-1, 0);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180));
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(360f * (c / 19)));
        if(AIR_CYLINDER != null)
            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, renderParams.buffer, RenderHelper.getTexedCylinderGlint(
                            wind, height, 0f))
                    , AIR_CYLINDER);
    }

    private void renderBase(RenderParams renderParams) {
        baseOffset(renderParams.matrixStack);
        MatrixStack matrixStackIn = renderParams.matrixStack;
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180));
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(360f * (c / 19)));
        RenderType renderType = RenderHelper.getTexedCylinderGlint(wind, 0.5f, 0f);
        if(BASE_CYLINDER != null)
            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, renderParams.buffer, renderType)
                , BASE_CYLINDER);

        matrixStackIn.pushPose();
        matrixStackIn.translate(0, -0.5, 0);
        matrixStackIn.scale(1.2f, 2f, 1.2f);
        if(BASE_CYLINDER != null)
            RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, renderParams.buffer, renderType)
                , BASE_CYLINDER);
        matrixStackIn.popPose();
        matrixStackIn.translate(0,  -height, 0);
        c = entity.tickCount % 30;
        matrixStackIn.translate(0,  height - 0.5 + (-c / 29 * 2), 0);
        if(OUTER_CYLINDER != null) {
            for(RenderHelper.CylinderContext cylinder : OUTER_CYLINDER) {
                RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, renderParams.buffer, renderType)
                        , cylinder);
                matrixStackIn.translate(0, -2, 0);
            }
        }
    }

    @Override
    public boolean forceRender() {
        return true;
    }
}
