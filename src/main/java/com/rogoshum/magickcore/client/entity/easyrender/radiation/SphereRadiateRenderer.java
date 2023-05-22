package com.rogoshum.magickcore.client.entity.easyrender.radiation;

import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.radiation.SphereEntity;
import net.minecraft.client.renderer.RenderType;
import com.mojang.math.Vector3f;

import java.util.HashMap;
import java.util.function.Consumer;

public class SphereRadiateRenderer extends EasyRenderer<SphereEntity> {
    float scale;
    private static final RenderType TYPE = RenderHelper.getLineStripGlow(3);

    public SphereRadiateRenderer(SphereEntity entity) {
        super(entity);
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        params.matrixStack.scale(scale, scale, scale);
        params.matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
        RenderHelper.renderSphereDynamic(
                BufferContext.create(params.matrixStack, params.buffer, TYPE).useShader(RenderMode.ShaderList.SLIME_SHADER)
                , new RenderHelper.RenderContext(1.0f, entity.spellContext().element().primaryColor(), RenderHelper.renderLight)
                , RenderHelper.EMPTY_VERTEX_CONTEXT, 2);
    }

    @Override
    public boolean forceRender() {
        return entity.isAlive();
    }

    @Override
    public void update() {
        super.update();
        scale = entity.getRange()*2;
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE, RenderMode.ShaderList.BITS_SMALL_SHADER), this::render);
        return map;
    }
}
