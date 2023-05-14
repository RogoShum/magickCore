package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.superentity.AscendantRealmEntity;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.renderer.RenderType;

import java.util.HashMap;
import java.util.function.Consumer;

public class AscendantRealmRenderer extends EasyRenderer<AscendantRealmEntity> {
    private static final RenderType TYPE = RenderHelper.getTexturedQuadsGlow(RenderHelper.BLANK_TEX);

    public AscendantRealmRenderer(AscendantRealmEntity entity) {
        super(entity);
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        params.matrixStack.scale(9.999f, 2.2f, 9.999f);
        RenderHelper.renderCubeCache(BufferContext.create(params.matrixStack, params.buffer
                , TYPE)
                , new RenderHelper.RenderContext(1.0f, Color.BLACK_COLOR, 0));
    }

    public void renderDistortion(RenderParams params) {
        baseOffset(params.matrixStack);
        params.matrixStack.scale(9.999f, 2.2f, 9.999f);
        RenderHelper.renderCubeCache(BufferContext.create(params.matrixStack, params.buffer
                        , TYPE)
                , new RenderHelper.RenderContext(0.3f, Color.BLACK_COLOR, 0));
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE, RenderMode.ShaderList.DISTORTION_SHADER), this::renderDistortion);
        map.put(new RenderMode(TYPE, RenderMode.ShaderList.EDGE_SHADER), this::render);
        return map;
    }
}
