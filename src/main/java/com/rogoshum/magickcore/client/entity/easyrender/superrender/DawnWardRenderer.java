package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.common.entity.superentity.DawnWardEntity;
import com.rogoshum.magickcore.common.lib.LibShaders;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.function.Consumer;

public class DawnWardRenderer extends EasyRenderer<DawnWardEntity> {
    private static final RenderType RENDER_TYPE_0 = RenderHelper.getTexedSphereGlow(RenderHelper.SPHERE_ROTATE, 3f, 0f);
    private static final RenderType RENDER_TYPE_1 = RenderHelper.getTexedSphereGlow(blank, 1f, 0f);
    float scale;

    public DawnWardRenderer(DawnWardEntity entity) {
        super(entity);
    }

    public void renderOuter(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        matrixStackIn.scale(scale, scale, scale);
        params.matrixStack.rotate(Vector3f.XP.rotationDegrees(90));
        RenderHelper.renderSphere(BufferContext.create(matrixStackIn, params.buffer, RENDER_TYPE_0).useShader(LibShaders.slime)
                , new RenderHelper.RenderContext(0.6f, entity.spellContext().element.color(), RenderHelper.renderLight)
                , new RenderHelper.VertexContext(entity.getHitReactions(),true, "DAWN_WARD"+entity.getEntityId(), 0.3f)
                , 16);
        matrixStackIn.scale(0.99f, 0.99f, 0.99f);
        RenderHelper.renderSphere(BufferContext.create(matrixStackIn, params.buffer, RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.3f, entity.spellContext().element.color(), RenderHelper.renderLight)
                , new RenderHelper.VertexContext(entity.getHitReactions(),true, "DAWN_WARD"+entity.getEntityId(), 0.5f)
                , 16);
    }

    public void renderInner(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        params.matrixStack.rotate(Vector3f.XP.rotationDegrees(90));
        matrixStackIn.scale(scale, scale, scale);
        matrixStackIn.scale(0.99f, 0.99f, 0.99f);
        RenderHelper.renderSphere(BufferContext.create(matrixStackIn, params.buffer, RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.3f, entity.spellContext().element.color(), RenderHelper.renderLight)
                , new RenderHelper.VertexContext(entity.getHitReactions(),0.5f)
                , 16);
    }

    @Override
    public void update() {
        super.update();
        if(entity.initial)
            scale = Math.min(1f, (float) (entity.ticksExisted - 15) / 5f) * entity.getWidth();
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        if(entity.initial)
            map.put(RenderMode.ORIGIN_RENDER, this::renderOuter);
        //map.put(RenderMode.ORIGIN_RENDER, this::renderInner);
        return map;
    }
}
