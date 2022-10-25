package com.rogoshum.magickcore.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.buff.ManaBuff;
import com.rogoshum.magickcore.client.render.RenderHelper;
import com.rogoshum.magickcore.lib.LibBuff;
import com.rogoshum.magickcore.lib.LibElements;
import com.rogoshum.magickcore.magick.Color;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Iterator;

import static net.minecraft.client.renderer.entity.LivingRenderer.getPackedOverlay;

@Mixin(LivingRenderer.class)
public abstract class MixinLivingRender<T extends LivingEntity, M extends EntityModel<T>>{

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

        EntityStateData state = ExtraDataHelper.entityStateData(entityIn);
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

    public void renderBuffLayer(T entityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, Color color, ResourceLocation texture) {
        RenderType rendertype = RenderHelper.getTexedEntityGlint(texture, 0.32f, 10f);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(rendertype);
        int i = getPackedOverlay(entityIn, this.getOverlayProgress(entityIn, partialTicks));
        getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, i, color.r(), color.g(), color.b(), 1.0F);
        matrixStackIn.translate(0, -0.005f * entityIn.getHeight(), 0);
        matrixStackIn.scale(1.01f, 1.01f, 1.01f);
    }

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
