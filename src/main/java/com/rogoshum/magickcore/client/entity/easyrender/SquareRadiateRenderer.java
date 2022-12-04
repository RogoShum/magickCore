package com.rogoshum.magickcore.client.entity.easyrender;

import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.radiated.SphereEntity;
import com.rogoshum.magickcore.common.entity.radiated.SquareEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.function.Consumer;

public class SquareRadiateRenderer extends EasyRenderer<SquareEntity> {
    float scale;
    private static final RenderType TYPE = RenderHelper.getLineStripGlow(5);

    public SquareRadiateRenderer(SquareEntity entity) {
        super(entity);
    }

    public void render(RenderParams params) {
        baseOffset(params.matrixStack);
        params.matrixStack.scale(scale, scale, scale);
        params.matrixStack.rotate(Vector3f.XP.rotationDegrees(90));
        RenderHelper.renderCube(
                BufferContext.create(params.matrixStack, params.buffer, TYPE)
                , new RenderHelper.RenderContext(1.0f, entity.spellContext().element.color(), RenderHelper.renderLight));
    }

    @Override
    public boolean forceRender() {
        return entity.isAlive();
    }

    @Override
    public void update() {
        super.update();
        scale = entity.spellContext().range;
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(TYPE, RenderMode.ShaderList.SLIME_SHADER), this::render);
        return map;
    }
}
