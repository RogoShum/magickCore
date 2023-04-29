package com.rogoshum.magickcore.client.entity.easyrender.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.projectile.WindEntity;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import com.mojang.math.Vector3f;

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
        c = entity.tickCount % 30;
        TYPE = RenderHelper.getTexedCylinderGlint(wind, entity.getBbHeight(), 0f, 0.2f, 10.0f);
    }

    public void renderOpacity(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        BufferBuilder bufferIn = params.buffer;
        baseOffset(matrixStackIn);
        float alpha = 1.0f;

        RenderHelper.CylinderContext context = new RenderHelper.CylinderContext(entity.getBbWidth() * 0.5f, entity.getBbWidth() * 0.5f, 1
                , 0.2f + entity.getBbHeight(), 16
                , 0.1f * alpha, alpha, 0.3f, entity.spellContext().element.secondaryColor());
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, TYPE)
                , context);
    }
    public void renderSlime(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        BufferBuilder bufferIn = params.buffer;
        baseOffset(matrixStackIn);
        float alpha = 1.0f;

        //matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(360f * (c / 29)));

        float height = entity.getBbHeight()*3;

        RenderHelper.CylinderContext context = new RenderHelper.CylinderContext(entity.getBbWidth() * 2f, entity.getBbWidth() * 0.25f, 2f
                , height , 16
                , 0, 0.9f, 0.8f, entity.spellContext().element.secondaryColor());
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, TYPE)
                , context);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        if(TYPE != null) {
            //map.put(new RenderMode(TYPE, RenderMode.ShaderList.OPACITY_SHADER), this::renderOpacity);
            map.put(new RenderMode(TYPE, RenderMode.ShaderList.BITS_SMALL_SHADER), this::renderSlime);
        }

        return map;
    }
}
