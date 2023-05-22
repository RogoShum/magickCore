package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.common.entity.superentity.DawnWardEntity;
import net.minecraft.client.renderer.RenderType;
import com.mojang.math.Vector3f;

import java.util.HashMap;
import java.util.function.Consumer;

public class DawnWardRenderer extends EasyRenderer<DawnWardEntity> {
    private static final RenderType RENDER_TYPE_0 = RenderHelper.getTexturedEntityGlint(RenderHelper.SPHERE_ROTATE, 1f, 45f);
    private static final RenderType RENDER_TYPE_1 = RenderHelper.getTexturedEntityGlint(blank, 1f, 45f);
    float scale;

    public DawnWardRenderer(DawnWardEntity entity) {
        super(entity);
    }

    public void renderOuter(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(params.matrixStack);
        matrixStackIn.scale(scale, scale, scale);
        params.matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
        RenderHelper.renderSphereDynamic(BufferContext.create(matrixStackIn, params.buffer, RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.3f, entity.spellContext().element().primaryColor(), RenderHelper.renderLight)
                , new RenderHelper.VertexContext(entity.getHitReactions(), 0.3f)
                , 3);
        matrixStackIn.scale(1.01f, 1.01f, 1.01f);
        RenderHelper.renderSphereDynamic(BufferContext.create(matrixStackIn, params.buffer, RENDER_TYPE_0).useShader(RenderMode.ShaderList.BITS_SHADER)
                , new RenderHelper.RenderContext(0.4f, entity.spellContext().element().secondaryColor(), RenderHelper.renderLight)
                , new RenderHelper.VertexContext(entity.getHitReactions(), 0.3f)
                , 3);
    }

    public void renderInner(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        params.matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
        matrixStackIn.scale(scale, scale, scale);
        matrixStackIn.scale(0.99f, 0.99f, 0.99f);
        RenderHelper.renderSphereDynamic(BufferContext.create(matrixStackIn, params.buffer, RENDER_TYPE_1)
                , new RenderHelper.RenderContext(0.3f, entity.spellContext().element().primaryColor(), RenderHelper.renderLight)
                , new RenderHelper.VertexContext(entity.getHitReactions(),0.3f)
                , 3);
    }

    @Override
    public void update() {
        super.update();
        if(entity.initial)
            scale = Math.min(1f, (float) (entity.tickCount - 15) / 5f) * entity.getBbWidth();
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
