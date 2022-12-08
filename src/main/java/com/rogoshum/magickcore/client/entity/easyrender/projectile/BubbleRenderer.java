package com.rogoshum.magickcore.client.entity.easyrender.projectile;

import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.projectile.BubbleEntity;
import net.minecraft.client.renderer.RenderType;

import java.util.HashMap;
import java.util.function.Consumer;

public class BubbleRenderer extends EasyRenderer<BubbleEntity> {
    private static final RenderType TYPE = RenderHelper.getTexedOrbGlow(BubbleEntity.ICON);

    public BubbleRenderer(BubbleEntity entity) {
        super(entity);
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        params.matrixStack.scale(entity.getWidth() * 0.6f, entity.getWidth() * 0.6f, entity.getWidth() * 0.6f);
        RenderHelper.renderParticle(BufferContext.create(params.matrixStack, params.buffer, TYPE), new RenderHelper.RenderContext(1.0f, entity.spellContext().element.color(), RenderHelper.renderLight));
    }


    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE, RenderMode.ShaderList.DISTORTION_SMALL_SHADER), this::render);
        map.put(new RenderMode(TYPE), this::render);

        return map;
    }
}
