package com.rogoshum.magickcore.client.entity.easyrender.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.easyrender.base.EasyRenderer;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.RenderMode;
import com.rogoshum.magickcore.client.render.RenderParams;
import com.rogoshum.magickcore.common.entity.projectile.RedStoneEntity;
import com.rogoshum.magickcore.common.magick.Color;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;

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
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.mulPose(Vector3f.XN.rotationDegrees((float) (entity.getDeltaMovement().x * 360) / entity.getBbWidth()));
        matrixStackIn.mulPose(Vector3f.ZN.rotationDegrees((float) (entity.getDeltaMovement().z * 360) / entity.getBbWidth()));
        float scale = entity.getBbWidth() * 0.99f;
        matrixStackIn.scale(scale, scale, scale);
        matrixStackIn.pushPose();
        scale = 1.01f;
        matrixStackIn.scale(scale, scale, scale);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, bufferIn, RENDER_TYPE_0), RENDER_CONTEXT_1);
        matrixStackIn.popPose();
        matrixStackIn.mulPose(Vector3f.YP.rotation(45));
        matrixStackIn.mulPose(Vector3f.ZP.rotation(45));
        matrixStackIn.scale(scale, scale, scale);
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, bufferIn, RENDER_TYPE_0), RENDER_CONTEXT_1);
    }

    public void renderType1(RenderParams params) {
        PoseStack matrixStackIn = params.matrixStack;
        baseOffset(matrixStackIn);
        BufferBuilder bufferIn = params.buffer;
        matrixStackIn.mulPose(Vector3f.XN.rotationDegrees((float) (entity.getDeltaMovement().x * 360) / entity.getBbWidth()));
        matrixStackIn.mulPose(Vector3f.ZN.rotationDegrees((float) (entity.getDeltaMovement().z * 360) / entity.getBbWidth()));
        float scale = entity.getBbWidth();
        matrixStackIn.scale(scale, scale, scale);
        matrixStackIn.pushPose();
        RenderHelper.renderCube(BufferContext.create(matrixStackIn, bufferIn, RENDER_TYPE_1), RENDER_CONTEXT_0);
        scale = 1.01f;
        matrixStackIn.scale(scale, scale, scale);
        matrixStackIn.popPose();
        matrixStackIn.mulPose(Vector3f.YP.rotation(45));
        matrixStackIn.mulPose(Vector3f.ZP.rotation(45));
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
