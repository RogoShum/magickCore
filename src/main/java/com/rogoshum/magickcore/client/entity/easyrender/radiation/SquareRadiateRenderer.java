package com.rogoshum.magickcore.client.entity.easyrender.radiation;

import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.radiation.SquareEntity;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.vector.Vector3d;

import java.util.HashMap;
import java.util.function.Consumer;

public class SquareRadiateRenderer extends EasyRenderer<SquareEntity> {
    float scale;
    private static final RenderType TYPE = RenderHelper.getLineStripPC(5);

    public SquareRadiateRenderer(SquareEntity entity) {
        super(entity);
    }

    public void render(RenderParams params) {
        Color color = entity.spellContext().element.color();
        Vector3d cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        WorldRenderer.renderLineBox(params.matrixStack, params.buffer, entity.getBoundingBox().inflate(scale * 0.5).move(-cam.x, -cam.y, -cam.z), color.r(), color.g(), color.b(), 1.0F);
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
        map.put(new RenderMode(TYPE), this::render);
        return map;
    }
}
