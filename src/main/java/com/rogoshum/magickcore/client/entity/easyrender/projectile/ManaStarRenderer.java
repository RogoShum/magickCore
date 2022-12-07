package com.rogoshum.magickcore.client.entity.easyrender.projectile;

import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.projectile.ManaStarEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import net.minecraft.client.renderer.RenderType;

import java.util.HashMap;
import java.util.function.Consumer;

public class ManaStarRenderer extends EasyRenderer<ManaStarEntity> {
    private static final RenderType TYPE = RenderHelper.getTexedOrbGlow(ModElements.ORIGIN.getRenderer().getStarTexture());

    public ManaStarRenderer(ManaStarEntity entity) {
        super(entity);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE), (renderParams) -> {
            baseOffset(renderParams.matrixStack);
            renderParams.matrixStack.scale(entity.getWidth(), entity.getWidth(), entity.getWidth());
            RenderHelper.renderParticle(BufferContext.create(renderParams.matrixStack, renderParams.buffer, TYPE), new RenderHelper.RenderContext(1.0f, entity.spellContext().element.color(), RenderHelper.renderLight));
            RenderHelper.renderParticle(BufferContext.create(renderParams.matrixStack, renderParams.buffer, TYPE), new RenderHelper.RenderContext(1.0f, entity.spellContext().element.color(), RenderHelper.renderLight));
            RenderHelper.renderParticle(BufferContext.create(renderParams.matrixStack, renderParams.buffer, TYPE), new RenderHelper.RenderContext(1.0f, entity.spellContext().element.color(), RenderHelper.renderLight));
        });

        return map;
    }
}
