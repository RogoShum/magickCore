package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.projectile.WindEntity;
import com.rogoshum.magickcore.common.lib.LibShaders;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.function.Consumer;

public class WindRenderer extends EasyRenderer<WindEntity> {
    float c;
    RenderType TYPE;

    public WindRenderer(WindEntity entity) {
        super(entity);
    }

    @Override
    public void update() {
        super.update();
        c = entity.ticksExisted % 30;
        TYPE = RenderHelper.getTexedCylinderGlint(wind, entity.getHeight(), 0f);
    }

    public void renderOpacity(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        BufferBuilder bufferIn = params.buffer;
        baseOffset(matrixStackIn);
        float alpha = 1.0f;

        RenderHelper.CylinderContext context = new RenderHelper.CylinderContext(entity.getWidth() * 0.5f, entity.getWidth() * 0.5f, 1
                , 0.2f + entity.getHeight(), 16
                , 0.1f * alpha, alpha, 0.3f, entity.spellContext().element.color());
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, TYPE)
                , context);
    }
    public void renderSlime(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        BufferBuilder bufferIn = params.buffer;
        baseOffset(matrixStackIn);
        float alpha = 1.0f;

        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(360f * (c / 29)));

        float height = entity.getHeight() - 0.2f;

        RenderHelper.CylinderContext context = new RenderHelper.CylinderContext(entity.getWidth() * 2f, entity.getWidth() * 0.5f, 2
                , height , 16
                , 0, 0.7f, 0.9f, entity.spellContext().element.color());
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, TYPE)
                , context);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        if(TYPE != null) {
            map.put(new RenderMode(TYPE, RenderMode.ShaderList.OPACITY_SHADER), this::renderOpacity);
            map.put(new RenderMode(TYPE, RenderMode.ShaderList.SLIME_SHADER), this::renderSlime);
        }

        return map;
    }
}
