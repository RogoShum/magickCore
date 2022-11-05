package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.entity.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderHelper;
import com.rogoshum.magickcore.client.render.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.entity.projectile.RedStoneEntity;
import com.rogoshum.magickcore.magick.Color;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.function.Consumer;

public class RedStoneRenderer extends EasyRenderer<RedStoneEntity> {
    public static final ResourceLocation TRAIL = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/trail.png");
    private static final RenderType RENDER_TYPE_1 = RenderHelper.getTexedEntity(RenderHelper.blankTex);
    private static final RenderType RENDER_TYPE_0 = RenderHelper.getTexedEntity(TRAIL);

    private static final RenderHelper.RenderContext RENDER_CONTEXT_0 = new RenderHelper.RenderContext(0.3f, Color.RED_COLOR, 0);
    private static final RenderHelper.RenderContext RENDER_CONTEXT_1 = new RenderHelper.RenderContext(0.9f, Color.RED_COLOR, RenderHelper.halfLight);

    public RedStoneRenderer(RedStoneEntity entity) {
        super(entity);
    }

    public void renderType0(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.rotate(Vector3f.XN.rotationDegrees((float) (entity.getMotion().x * 360) / entity.getWidth()));
        matrixStackIn.rotate(Vector3f.ZN.rotationDegrees((float) (entity.getMotion().z * 360) / entity.getWidth()));
        float scale = entity.getWidth();
        matrixStackIn.scale(scale, scale, scale);
        matrixStackIn.push();
        scale = 1.01f;
        matrixStackIn.scale(scale, scale, scale);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, bufferIn, RENDER_TYPE_0), RENDER_CONTEXT_1);
        matrixStackIn.pop();
        matrixStackIn.rotate(Vector3f.YP.rotation(45));
        matrixStackIn.rotate(Vector3f.ZP.rotation(45));
        matrixStackIn.scale(scale, scale, scale);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, bufferIn, RENDER_TYPE_0), RENDER_CONTEXT_1);
    }

    public void renderType1(RenderParams params) {
        MatrixStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.rotate(Vector3f.XN.rotationDegrees((float) (entity.getMotion().x * 360) / entity.getWidth()));
        matrixStackIn.rotate(Vector3f.ZN.rotationDegrees((float) (entity.getMotion().z * 360) / entity.getWidth()));
        float scale = entity.getWidth();
        matrixStackIn.scale(scale, scale, scale);
        matrixStackIn.push();
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, bufferIn, RENDER_TYPE_1), RENDER_CONTEXT_0);
        scale = 1.01f;
        matrixStackIn.scale(scale, scale, scale);
        matrixStackIn.pop();
        matrixStackIn.rotate(Vector3f.YP.rotation(45));
        matrixStackIn.rotate(Vector3f.ZP.rotation(45));
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, bufferIn, RENDER_TYPE_1), RENDER_CONTEXT_0);
        matrixStackIn.scale(scale, scale, scale);
    }

    @Override
    public HashMap<RenderMode, Consumer<RenderParams>> getRenderFunction() {
        HashMap<RenderMode, Consumer<RenderParams>> map = new HashMap<>();
        map.put(new RenderMode(RENDER_TYPE_0), this::renderType0);
        map.put(new RenderMode(RENDER_TYPE_1), this::renderType1);
        return map;
    }
}