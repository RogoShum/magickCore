package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.superentity.ThornsCaressEntity;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.function.Consumer;

public class ThornsCaressRenderer extends EasyRenderer<ThornsCaressEntity> {
    float preRotate;
    float postRotate;
    float rotate;
    private static final RenderType BLANK = RenderHelper.getTexturedEntityGlintShake(blank, 2f, 0f, 0.2f, 1f);
    private static final RenderType SPHERE = RenderHelper.getTexturedEntityGlintShake(sphere_rotate, 2, 0f, 0.2f, 1f);
    float degrees;

    public ThornsCaressRenderer(ThornsCaressEntity entity) {
        super(entity);
    }

    @Override
    public void update() {
        super.update();
        preRotate = entity.tickCount % 11;
        postRotate = (entity.tickCount + 1) % 11;
        rotate = Mth.lerp(Minecraft.getInstance().getFrameTime(), preRotate, postRotate);
        degrees = 360f * (rotate / 10);
    }

    @Override
    public void baseOffset(PoseStack matrixStackIn) {
        super.baseOffset(matrixStackIn);
        matrixStackIn.scale(1.45f, 1.45f, 1.45f);
    }

    public void renderSphereOpacity(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.scale(1.45f, 1.45f, 1.45f);
        RenderHelper.renderSphereCache(BufferContext.create(matrixStackIn, bufferIn, SPHERE), new RenderHelper.RenderContext(0.9f, entity.spellContext().element().primaryColor(), RenderHelper.renderLight), 1);
    }

    public void renderSphereDistortion(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.scale(1.2f, 1.2f, 1.2f);
        RenderHelper.renderSphereCache(BufferContext.create(matrixStackIn, bufferIn, SPHERE), new RenderHelper.RenderContext(0.95f, entity.spellContext().element().secondaryColor(), RenderHelper.renderLight), 1);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        //map.put(new RenderMode(BLANK, RenderMode.ShaderList.BITS_SHADER), this::renderSphereSlime);
        map.put(new RenderMode(SPHERE, RenderMode.ShaderList.OPACITY_SHADER), this::renderSphereOpacity);
        map.put(new RenderMode(SPHERE, RenderMode.ShaderList.DISTORTION_MID_SHADER), this::renderSphereDistortion);
        return map;
    }
}
