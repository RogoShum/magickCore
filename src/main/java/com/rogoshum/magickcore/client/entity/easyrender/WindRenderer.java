package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.entity.projectile.WindEntity;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibShaders;
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
        matrixStackIn.translate(0, 0.2, 0);
        float height = entity.getHeight() - 0.2f;
        context = new RenderHelper.CylinderContext(entity.getWidth() * 0.6f, entity.getWidth() * 0.6f, 1
                , height, 16
                , 0.0f, alpha, 0.3f, ModElements.ORIGIN.color());
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, TYPE)
                , context);
    }
    public void renderSlime(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        BufferBuilder bufferIn = params.buffer;
        baseOffset(matrixStackIn);
        float alpha = 1.0f;

        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(360f * (c / 29)));
        matrixStackIn.translate(0, -0.2, 0);
        matrixStackIn.push();
        matrixStackIn.translate(0, 0.2, 0);
        RenderHelper.CylinderContext context =
                new RenderHelper.CylinderContext(entity.getWidth() * 0.4f, entity.getWidth() * 0.4f, 1
                        , entity.getHeight() / 2, 16
                        , 0.1f * alpha, alpha, 0.3f, ModElements.ORIGIN.color());
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, TYPE)
                , context);
        matrixStackIn.pop();
        matrixStackIn.translate(0, 0.2, 0);
        context = new RenderHelper.CylinderContext(entity.getWidth() * 0.5f, entity.getWidth() * 0.5f, 1
                , 0.2f + entity.getHeight(), 16
                , 0.1f * alpha, alpha, 0.3f, entity.spellContext().element.color());
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, TYPE)
                , context);

        float height = entity.getHeight() - 0.2f;

        context = new RenderHelper.CylinderContext(entity.getWidth() * 2f, entity.getWidth() * 2f, 1
                , height * 0.5f, 16
                , 0, 0.8f, 0.3f, entity.spellContext().element.color());
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, TYPE)
                , context);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        if(TYPE != null) {
            map.put(new RenderMode(TYPE, LibShaders.opacity), this::renderOpacity);
            map.put(new RenderMode(TYPE, LibShaders.slime), this::renderSlime);
        }

        return map;
    }
}
