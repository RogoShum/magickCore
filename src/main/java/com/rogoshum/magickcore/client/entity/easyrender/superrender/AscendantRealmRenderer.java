package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.superentity.AscendantRealmEntity;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.function.Consumer;

public class AscendantRealmRenderer extends EasyRenderer<AscendantRealmEntity> {
    private static final RenderType TYPE = RenderHelper.getTexedEntityGlint(RenderHelper.TAKEN_LAYER);

    public AscendantRealmRenderer(AscendantRealmEntity entity) {
        super(entity);
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        params.matrixStack.scale(9.999f, 1.999f, 9.999f);
        RenderHelper.renderCube(BufferContext.create(params.matrixStack, params.buffer
                , TYPE)
                , new RenderHelper.RenderContext(0.1f, Color.BLACK_COLOR, 0));
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE, RenderMode.ShaderList.DISTORTION_SHADER), this::render);
        map.put(new RenderMode(TYPE), this::render);
        return map;
    }
}
