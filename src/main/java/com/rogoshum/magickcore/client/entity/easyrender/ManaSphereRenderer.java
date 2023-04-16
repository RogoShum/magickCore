package com.rogoshum.magickcore.client.entity.easyrender;

import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.pointed.ManaSphereEntity;
import net.minecraft.client.renderer.RenderType;
import com.mojang.math.Vector3f;

import java.util.HashMap;
import java.util.Queue;
import java.util.function.Consumer;

public class ManaSphereRenderer extends EasyRenderer<ManaSphereEntity> {
    float scale;
    Queue<Queue<RenderHelper.VertexAttribute>> SPHERE;
    Queue<Queue<RenderHelper.VertexAttribute>> SPHERE_DISTORTION;
    private static final RenderType TYPE = RenderHelper.getTexedSphereGlow(sphere_rotate, 1.2f, 0f);

    public ManaSphereRenderer(ManaSphereEntity entity) {
        super(entity);
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        params.matrixStack.scale(scale, scale, scale);
        params.matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));

        /*
                if(!RenderHelper.isRenderingShader() && SPHERE_DISTORTION != null)
            RenderHelper.renderSphere(
                BufferContext.create(params.matrixStack, params.buffer, TYPE).useShader(RenderMode.ShaderList.DISTORTION_MID_SHADER)
                , SPHERE);
        params.matrixStack.scale(1.02f, 1.02f, 1.02f);
         */
        if(SPHERE != null)
            RenderHelper.renderSphere(
                    BufferContext.create(params.matrixStack, params.buffer, TYPE).useShader(RenderMode.ShaderList.BITS_SHADER)
                    , SPHERE);
    }

    @Override
    public void update() {
        super.update();
        scale = entity.getBbWidth() * 1.2f;
        if(entity.tickCount < 9)
            scale *= 1 - 1f / ((float)entity.tickCount + 1f);

        if(entity.spellContext().tick - entity.tickCount <= 9)
            scale *= 1 - 1f / (float)(entity.spellContext().tick - entity.tickCount);
        if(entity.spellContext().tick <= entity.tickCount)
            scale = 0;

        SPHERE = RenderHelper.drawSphere(12, new RenderHelper.RenderContext(0.6f, entity.spellContext().element.primaryColor(), RenderHelper.renderLight)
                , new RenderHelper.VertexContext(entity.getHitReactions(), true, "MANA_SPHERE"+entity.getId(), 2.10f));
/*
        SPHERE_DISTORTION = RenderHelper.drawSphere(12, new RenderHelper.RenderContext(1.0f, entity.spellContext().element.color(), RenderHelper.renderLight)
                , new RenderHelper.VertexContext(entity.getHitReactions(), true, "MANA_SPHERE"+entity.getId(), 2.10f));
 */
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        if(SPHERE != null)
            map.put(RenderMode.ORIGIN_RENDER, this::render);
        return map;
    }
}
