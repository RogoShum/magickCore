package com.rogoshum.magickcore.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.render.SingleBuffer;
import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibElements;
import com.rogoshum.magickcore.mixin.fabric.reflection.ILayersRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingRender<T extends LivingEntity, M extends EntityModel<T>> implements ILayersRenderer<T, M> {

    @Shadow
    protected final List<RenderLayer<T, M>> layers = Lists.newArrayList();
    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/world/entity/LivingEntity.isSpectator ()Z"
            )
    )
    public void onRender(T entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, CallbackInfo callbackInfo) {
        boolean flag = this.isBodyVisible(entityIn);
        if(!flag) return;

        EntityStateData state = ExtraDataUtil.entityStateData(entityIn);
        if(state.getBuffList().isEmpty()) return;
        Color color = Color.create(0f, 0f, 0f);
        int time = 0;
        HashMap<String, ManaBuff> buffMap = new HashMap<>(state.getBuffList());
        Iterator<String> it = buffMap.keySet().iterator();
        while (it.hasNext()) {
            ManaBuff buff = buffMap.get(it.next());
            if(buff.isBeneficial()) {
                it.remove();
                color = getColorBlender(time++, color, MagickCore.proxy.getElementRender(buff.getElement()).getColor());
            }
        }
        matrixStackIn.pushPose();
        if (color.r() > 0.0f || color.g() > 0.0f || color.b() > 0.0f) {
            renderBuffLayer(entityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, color, RenderHelper.SPHERE_ROTATE);
        }

        if(buffMap.containsKey(LibBuff.TAKEN)) {
            buffMap.remove(LibBuff.TAKEN);
            TakenEntityData taken = ExtraDataUtil.takenEntityData(entityIn);
            if(taken != null && !taken.getOwnerUUID().equals(MagickCore.emptyUUID) && taken.getTime() > 0)
                renderBuffLayerSolid(entityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, Color.BLACK_COLOR, RenderHelper.TAKEN_LAYER);
            else
                renderBuffLayerSolid(entityIn, partialTicks, matrixStackIn, bufferIn, RenderHelper.renderLight, Color.ORIGIN_COLOR, RenderHelper.TAKEN_LAYER);
        }

        if(buffMap.containsKey(LibBuff.FREEZE)) {
            buffMap.remove(LibBuff.FREEZE);
            renderBuffLayer(entityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, MagickCore.proxy.getElementRender(LibElements.STASIS).getColor(), RenderHelper.CYLINDER_ROTATE);
        }

        for(ManaBuff buff : buffMap.values()) {
            renderBuffLayer(entityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, MagickCore.proxy.getElementRender(buff.getElement()).getColor(), RenderHelper.RES_ITEM_GLINT);
        }
        matrixStackIn.popPose();
    }

    public void onRenderLayer(T entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, CallbackInfo ci) {
        boolean flag = this.isBodyVisible(entityIn);
        if(!flag) return;
        if (entityIn.isSpectator()) return;

        EntityStateData state = ExtraDataUtil.entityStateData(entityIn);
        if(state.getBuffList().isEmpty()) return;
        Color color = Color.create(0f, 0f, 0f);
        int time = 0;
        HashMap<String, ManaBuff> buffMap = new HashMap<>(state.getBuffList());
        Iterator<String> it = buffMap.keySet().iterator();
        while (it.hasNext()) {
            ManaBuff buff = buffMap.get(it.next());
            if(buff.isBeneficial()) {
                it.remove();
                color = getColorBlender(time++, color, MagickCore.proxy.getElementRender(buff.getElement()).getColor());
            }
        }
        matrixStackIn.pushPose();
        if (color.r() > 0.0f || color.g() > 0.0f || color.b() > 0.0f) {
            renderBuffLayerLayer(entityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, color, RenderHelper.SPHERE_ROTATE);
        }

        if(buffMap.containsKey(LibBuff.TAKEN)) {
            buffMap.remove(LibBuff.TAKEN);
            TakenEntityData taken = ExtraDataUtil.takenEntityData(entityIn);
            if(taken != null && !taken.getOwnerUUID().equals(MagickCore.emptyUUID) && taken.getTime() > 0)
                renderBuffLayerSolid(entityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, Color.BLACK_COLOR, RenderHelper.TAKEN_LAYER);
            else
                renderBuffLayerSolid(entityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, Color.GREY_COLOR, RenderHelper.TAKEN_LAYER);
        }

        if(buffMap.containsKey(LibBuff.FREEZE)) {
            buffMap.remove(LibBuff.FREEZE);
            renderBuffLayerLayer(entityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, MagickCore.proxy.getElementRender(LibElements.STASIS).getColor(), RenderHelper.CYLINDER_ROTATE);
        }

        for(ManaBuff buff : buffMap.values()) {
            renderBuffLayerLayer(entityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, MagickCore.proxy.getElementRender(buff.getElement()).getColor(), RenderHelper.RES_ITEM_GLINT);
        }
        matrixStackIn.popPose();
    }

    public void renderBuffLayer(T entityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, Color color, ResourceLocation texture) {
        RenderType rendertype = RenderHelper.getLayerEntityGlint(texture, 1.0f, 10f);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(rendertype);
        int i = LivingEntityRenderer.getOverlayCoords(entityIn, this.getWhiteOverlayProgress(entityIn, partialTicks));
        getModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, i, color.r(), color.g(), color.b(), 1.0F);
    }

    public void renderBuffLayerSolid(T entityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, Color color, ResourceLocation texture) {
        RenderType rendertype = RenderHelper.getLayerEntityGlintSolid(texture, 1.5f, 10f);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(rendertype);
        int i = LivingEntityRenderer.getOverlayCoords(entityIn, this.getWhiteOverlayProgress(entityIn, partialTicks));
        getModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, i, color.r(), color.g(), color.b(), 1.0F);
    }

    public void renderBuffLayerLayer(T entityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, Color color, ResourceLocation texture) {
        float f6 = Mth.lerp(partialTicks, entityIn.xRotO, entityIn.xRot);
        float f = Mth.rotLerp(partialTicks, entityIn.yBodyRotO, entityIn.yBodyRot);
        float f1 = Mth.rotLerp(partialTicks, entityIn.yHeadRotO, entityIn.yHeadRot);
        float f2 = f1 - f;
        boolean shouldSit = entityIn.isPassenger();
        float f7 = this.getBob(entityIn, partialTicks);

        float f8 = 0.0F;
        float f5 = 0.0F;
        if (!shouldSit && entityIn.isAlive()) {
            f8 = Mth.lerp(partialTicks, entityIn.animationSpeedOld, entityIn.animationSpeed);
            f5 = entityIn.animationPosition - entityIn.animationSpeed * (1.0F - partialTicks);
            if (entityIn.isBaby()) {
                f5 *= 3.0F;
            }

            if (f8 > 1.0F) {
                f8 = 1.0F;
            }
        }
        if (shouldSit && entityIn.getVehicle() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entityIn.getVehicle();
            f = Mth.rotLerp(partialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
            f2 = f1 - f;
            float f3 = Mth.wrapDegrees(f2);
            if (f3 < -85.0F) {
                f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
                f3 = 85.0F;
            }

            f = f1 - f3;
            if (f3 * f3 > 2500.0F) {
                f += f3 * 0.2F;
            }

            f2 = f1 - f;
        }

        SingleBuffer RENDER_TYPE_BUFFER = new SingleBuffer(RenderHelper::getLayerEntityGlint, texture);
        for(RenderLayer<T, M> layerrenderer : this.layers) {
            layerrenderer.render(matrixStackIn, RENDER_TYPE_BUFFER, packedLightIn, entityIn, f5, f8, partialTicks, f7, f2, f6);
        }
        RENDER_TYPE_BUFFER.finish();
    }

    @Shadow
    protected abstract float getBob(T livingBase, float partialTicks);

    public Color getColorBlender(int time, Color preColor, Color blend) {
        float[] newColor = new float[3];
        float scale = 0f;
        if(time > 0)
            scale = (float)time / ((float)time + 1.0f);
        newColor[0] = scale * preColor.r();
        newColor[1] = scale * preColor.g();
        newColor[2] = scale * preColor.b();

        newColor[0] = newColor[0] + (1.0f-scale) * blend.r();
        newColor[1] = newColor[1] + (1.0f-scale) * blend.g();
        newColor[2] = newColor[2] + (1.0f-scale) * blend.b();

        return Color.create(newColor[0], newColor[1], newColor[2]);
    }

    @Shadow
    protected abstract float getWhiteOverlayProgress(T livingEntityIn, float partialTicks);

    @Shadow
    public abstract M getModel();

    @Shadow
    protected abstract boolean isBodyVisible(T livingEntityIn);

    @Shadow protected abstract void scale(T livingEntity, PoseStack poseStack, float f);

    @Override
    public List<RenderLayer<T, M>> getLayers() {
        return layers;
    }

    @Override
    public void invokeScale(T livingEntity, PoseStack poseStack, float f) {
        scale(livingEntity, poseStack, f);
    }
}
