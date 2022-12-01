package com.rogoshum.magickcore.common.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.render.SingleBuffer;
import com.rogoshum.magickcore.common.buff.ManaBuff;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.lib.LibElements;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static net.minecraft.client.renderer.entity.LivingRenderer.getPackedOverlay;

@Mixin(LivingRenderer.class)
public abstract class MixinLivingRender<T extends LivingEntity, M extends EntityModel<T>>{

    @Shadow
    protected final List<LayerRenderer<T, M>> layerRenderers = Lists.newArrayList();
    @Inject(
            method = "net/minecraft/client/renderer/entity/LivingRenderer.render (Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/entity/LivingEntity.isSpectator ()Z"
            )
    )
    public void onRender(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, CallbackInfo callbackInfo) {
        boolean flag = this.isVisible(entityIn);
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
        matrixStackIn.push();
        if (color.r() > 0.0f || color.g() > 0.0f || color.b() > 0.0f) {
            renderBuffLayer(entityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, color, RenderHelper.SPHERE_ROTATE);
        }

        if(buffMap.containsKey(LibBuff.TAKEN)) {
            buffMap.remove(LibBuff.TAKEN);
            renderBuffLayer(entityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, MagickCore.proxy.getElementRender(LibElements.TAKEN).getColor(), RenderHelper.CYLINDER_ROTATE);
        }

        if(buffMap.containsKey(LibBuff.FREEZE)) {
            buffMap.remove(LibBuff.FREEZE);
            renderBuffLayer(entityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, MagickCore.proxy.getElementRender(LibElements.STASIS).getColor(), RenderHelper.CYLINDER_ROTATE);
        }

        for(ManaBuff buff : buffMap.values()) {
            renderBuffLayer(entityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, MagickCore.proxy.getElementRender(buff.getElement()).getColor(), RenderHelper.RES_ITEM_GLINT);
        }
        matrixStackIn.pop();
    }

    public void onRenderLayer(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, CallbackInfo ci) {
        boolean flag = this.isVisible(entityIn);
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
        matrixStackIn.push();
        if (color.r() > 0.0f || color.g() > 0.0f || color.b() > 0.0f) {
            renderBuffLayerLayer(entityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, color, RenderHelper.SPHERE_ROTATE);
        }

        if(buffMap.containsKey(LibBuff.TAKEN)) {
            buffMap.remove(LibBuff.TAKEN);
            renderBuffLayerLayer(entityIn, partialTicks, matrixStackIn, bufferIn, 0, MagickCore.proxy.getElementRender(LibElements.TAKEN).getColor(), RenderHelper.CYLINDER_ROTATE);
        }

        if(buffMap.containsKey(LibBuff.FREEZE)) {
            buffMap.remove(LibBuff.FREEZE);
            renderBuffLayerLayer(entityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, MagickCore.proxy.getElementRender(LibElements.STASIS).getColor(), RenderHelper.CYLINDER_ROTATE);
        }

        for(ManaBuff buff : buffMap.values()) {
            renderBuffLayerLayer(entityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, MagickCore.proxy.getElementRender(buff.getElement()).getColor(), RenderHelper.RES_ITEM_GLINT);
        }
        matrixStackIn.pop();
    }

    public void renderBuffLayer(T entityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, Color color, ResourceLocation texture) {
        RenderType rendertype = RenderHelper.getLayerEntityGlint(texture, 0.32f, 10f);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(rendertype);
        int i = getPackedOverlay(entityIn, this.getOverlayProgress(entityIn, partialTicks));
        getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, i, color.r(), color.g(), color.b(), 1.0F);
    }

    public void renderBuffLayerLayer(T entityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, Color color, ResourceLocation texture) {
        float f6 = MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch);
        float f = MathHelper.interpolateAngle(partialTicks, entityIn.prevRenderYawOffset, entityIn.renderYawOffset);
        float f1 = MathHelper.interpolateAngle(partialTicks, entityIn.prevRotationYawHead, entityIn.rotationYawHead);
        float f2 = f1 - f;
        boolean shouldSit = entityIn.isPassenger() && (entityIn.getRidingEntity() != null && entityIn.getRidingEntity().shouldRiderSit());
        float f7 = this.handleRotationFloat(entityIn, partialTicks);

        float f8 = 0.0F;
        float f5 = 0.0F;
        if (!shouldSit && entityIn.isAlive()) {
            f8 = MathHelper.lerp(partialTicks, entityIn.prevLimbSwingAmount, entityIn.limbSwingAmount);
            f5 = entityIn.limbSwing - entityIn.limbSwingAmount * (1.0F - partialTicks);
            if (entityIn.isChild()) {
                f5 *= 3.0F;
            }

            if (f8 > 1.0F) {
                f8 = 1.0F;
            }
        }
        if (shouldSit && entityIn.getRidingEntity() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entityIn.getRidingEntity();
            f = MathHelper.interpolateAngle(partialTicks, livingentity.prevRenderYawOffset, livingentity.renderYawOffset);
            f2 = f1 - f;
            float f3 = MathHelper.wrapDegrees(f2);
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
        for(LayerRenderer<T, M> layerrenderer : this.layerRenderers) {
            layerrenderer.render(matrixStackIn, RENDER_TYPE_BUFFER, packedLightIn, entityIn, f5, f8, partialTicks, f7, f2, f6);
        }
        RENDER_TYPE_BUFFER.finish();
    }

    @Shadow
    protected abstract float handleRotationFloat(T livingBase, float partialTicks);

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
    protected abstract float getOverlayProgress(T livingEntityIn, float partialTicks);

    @Shadow
    public abstract M getEntityModel();

    @Shadow
    protected abstract boolean isVisible(T livingEntityIn);
}
