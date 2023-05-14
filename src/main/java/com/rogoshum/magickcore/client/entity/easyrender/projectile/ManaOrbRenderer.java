package com.rogoshum.magickcore.client.entity.easyrender.projectile;

import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.projectile.ManaOrbEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;

import java.util.HashMap;
import java.util.function.Consumer;

public class ManaOrbRenderer extends EasyRenderer<ManaOrbEntity> {
    private static final RenderType TYPE = RenderHelper.getTexturedQuadsGlow(ModElements.ORIGIN.getRenderer().getOrbTexture());

    public ManaOrbRenderer(ManaOrbEntity entity) {
        super(entity);
    }


    @Override
    public void update() {
        super.update();
        entity.renderFrame(Minecraft.getInstance().getFrameTime());
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE, RenderMode.ShaderList.BITS_SHADER), (renderParams) -> {
            baseOffset(renderParams.matrixStack);
            renderParams.matrixStack.scale(entity.getBbWidth() * 0.6f, entity.getBbWidth() * 0.6f, entity.getBbWidth() * 0.6f);
            RenderHelper.renderParticle(BufferContext.create(renderParams.matrixStack, renderParams.buffer, TYPE), new RenderHelper.RenderContext(1.0f, entity.spellContext().element.primaryColor(), RenderHelper.renderLight));
            RenderHelper.renderParticle(BufferContext.create(renderParams.matrixStack, renderParams.buffer, TYPE), new RenderHelper.RenderContext(1.0f, entity.spellContext().element.primaryColor(), RenderHelper.renderLight));
            RenderHelper.renderParticle(BufferContext.create(renderParams.matrixStack, renderParams.buffer, TYPE), new RenderHelper.RenderContext(1.0f, entity.spellContext().element.primaryColor(), RenderHelper.renderLight));
        });

        return map;
    }
}
