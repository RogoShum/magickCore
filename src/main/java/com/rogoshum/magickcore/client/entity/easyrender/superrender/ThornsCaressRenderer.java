package com.rogoshum.magickcore.client.entity.easyrender.superrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.element.ElementRenderer;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.superentity.ThornsCaressEntity;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.lib.LibShaders;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.Queue;
import java.util.function.Consumer;

public class ThornsCaressRenderer extends EasyRenderer<ThornsCaressEntity> {
    float preRotate;
    float postRotate;
    float rotate;
    private static final RenderType BLANK = RenderHelper.getTexedSphereGlow(blank, 1f, 0f);
    private static final RenderType SPHERE = RenderHelper.getTexedSphereGlow(sphere_rotate, 1f, 0f);
    float degrees;

    public ThornsCaressRenderer(ThornsCaressEntity entity) {
        super(entity);
    }

    @Override
    public void update() {
        super.update();
        preRotate = entity.tickCount % 11;
        postRotate = (entity.tickCount + 1) % 11;
        rotate = MathHelper.lerp(Minecraft.getInstance().getFrameTime(), preRotate, postRotate);
        degrees = 360f * (rotate / 10);
    }

    @Override
    public void baseOffset(MatrixStack matrixStackIn) {
        super.baseOffset(matrixStackIn);
        matrixStackIn.scale(1.45f, 1.45f, 1.45f);
    }

    public void renderSphereSlime(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.scale(0.5f, 0.5f, 0.5f);
        RenderHelper.renderSphere(BufferContext.create(matrixStackIn, bufferIn, BLANK), new RenderHelper.RenderContext(0.5f, Color.ORIGIN_COLOR, RenderHelper.renderLight), 6);
    }

    public void renderSphereOpacity(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.scale(1.45f, 1.45f, 1.45f);
        RenderHelper.renderSphere(BufferContext.create(matrixStackIn, bufferIn, SPHERE), new RenderHelper.RenderContext(0.9f, entity.spellContext().element.color(), RenderHelper.renderLight), 6);
    }

    public void renderSphereDistortion(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.scale(1.45f, 1.45f, 1.45f);
        RenderHelper.renderSphere(BufferContext.create(matrixStackIn, bufferIn, SPHERE), new RenderHelper.RenderContext(0.1f, entity.spellContext().element.color(), RenderHelper.renderLight), 6);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(BLANK, RenderMode.ShaderList.SLIME_SHADER), this::renderSphereSlime);
        map.put(new RenderMode(SPHERE, RenderMode.ShaderList.OPACITY_SHADER), this::renderSphereOpacity);
        if(!RenderHelper.isRenderingShader()) {
            map.put(new RenderMode(SPHERE, RenderMode.ShaderList.DISTORTION_MID_SHADER), this::renderSphereDistortion);
        }
        return map;
    }
}
