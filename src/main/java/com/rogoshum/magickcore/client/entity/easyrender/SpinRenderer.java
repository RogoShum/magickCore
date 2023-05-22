package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.ElementRenderer;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.pointed.SpinEntity;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;

import java.util.HashMap;
import java.util.function.Consumer;

public class SpinRenderer extends EasyRenderer<SpinEntity> {
    RenderType SPHERE = RenderHelper.getLineStripGlow(2f);
    float preRotate;
    float postRotate;
    float rotate;
    ElementRenderer renderer;

    public SpinRenderer(SpinEntity entity) {
        super(entity);
    }

    @Override
    public void update() {
        super.update();
        preRotate = entity.tickCount % 5;
        postRotate = (entity.tickCount + 1) % 5;
        rotate = Mth.lerp(Minecraft.getInstance().getFrameTime(), preRotate, postRotate);
        renderer = entity.spellContext().element().getRenderer();
    }

    public void render(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        BufferBuilder bufferIn = params.buffer;
        baseOffset(matrixStackIn);
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(360f * (rotate / 4)));

        RenderHelper.CylinderContext context = new RenderHelper.CylinderContext(0.5f, 0.25f, 2
                , 1, 0.5f
                , 0.1f, 1.0f, 0.3f);

        RenderHelper.renderCylinderCache(BufferContext.create(matrixStackIn, bufferIn, SPHERE)
                , context, new RenderHelper.RenderContext(1, entity.spellContext().element().primaryColor()));
        matrixStackIn.popPose();
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(SPHERE), this::render);
        return map;
    }
}
