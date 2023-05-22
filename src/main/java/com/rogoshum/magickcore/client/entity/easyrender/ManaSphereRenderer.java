package com.rogoshum.magickcore.client.entity.easyrender;

import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.pointed.ManaSphereEntity;
import net.minecraft.client.renderer.RenderType;
import com.mojang.math.Vector3f;

import java.util.HashMap;
import java.util.function.Consumer;

public class ManaSphereRenderer extends EasyRenderer<ManaSphereEntity> {
    float scale;
    private static final RenderType TYPE = RenderHelper.getTexturedEntityGlintShake(sphere_rotate, 1f, 0f, 0.05f, 1f);

    public ManaSphereRenderer(ManaSphereEntity entity) {
        super(entity);
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        params.matrixStack.scale(scale, scale, scale);
        params.matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
        RenderHelper.renderSphereCache(
                BufferContext.create(params.matrixStack, params.buffer, TYPE)
                , new RenderHelper.RenderContext(0.4f, entity.spellContext().element().primaryColor(), RenderHelper.renderLight), 1);
    }

    @Override
    public void update() {
        super.update();
        scale = entity.getBbWidth() * 1.4f;
        if(entity.tickCount < 9)
            scale *= 1 - 1f / ((float)entity.tickCount + 1f);

        if(entity.spellContext().tick() - entity.tickCount <= 9)
            scale *= 1 - 1f / (float)(entity.spellContext().tick() - entity.tickCount);
        if(entity.spellContext().tick() <= entity.tickCount)
            scale = 0;
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE, RenderMode.ShaderList.BITS_SHADER), this::render);
        return map;
    }
}
