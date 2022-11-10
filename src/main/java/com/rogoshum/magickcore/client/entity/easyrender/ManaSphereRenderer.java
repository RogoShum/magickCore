package com.rogoshum.magickcore.client.entity.easyrender;

import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.pointed.ManaSphereEntity;
import com.rogoshum.magickcore.common.lib.LibShaders;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.Queue;
import java.util.function.Consumer;

public class ManaSphereRenderer extends EasyRenderer<ManaSphereEntity> {
    float scale;
    Queue<Queue<RenderHelper.VertexAttribute>> SPHERE;
    private static final RenderType TYPE = RenderHelper.getTexedSphereGlow(sphere_rotate, 1.2f, 0f);

    public ManaSphereRenderer(ManaSphereEntity entity) {
        super(entity);
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        params.matrixStack.scale(scale, scale, scale);
        params.matrixStack.rotate(Vector3f.XP.rotationDegrees(90));
        if(SPHERE != null)
            RenderHelper.renderSphere(
                BufferContext.create(params.matrixStack, params.buffer, TYPE).useShader(LibShaders.slime)
                , SPHERE);
    }

    @Override
    public void update() {
        super.update();
        scale = entity.getWidth() * 1.6f;
        if(entity.ticksExisted < 9)
            scale *= 1 - 1f / ((float)entity.ticksExisted + 1f);

        if(entity.spellContext().tick - entity.ticksExisted <= 9)
            scale *= 1 - 1f / (float)(entity.spellContext().tick - entity.ticksExisted);
        if(entity.spellContext().tick <= entity.ticksExisted)
            scale = 0;

        SPHERE = RenderHelper.drawSphere(12, new RenderHelper.RenderContext(0.6f, entity.spellContext().element.color(), RenderHelper.renderLight)
                , new RenderHelper.VertexContext(entity.getHitReactions(), true, "MANA_SPHERE"+entity.getEntityId(), 2.10f));
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        if(SPHERE != null)
            map.put(RenderMode.ORIGIN_RENDER, this::render);
        return map;
    }
}
