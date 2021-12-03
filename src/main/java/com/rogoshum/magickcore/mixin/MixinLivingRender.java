package com.rogoshum.magickcore.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rogoshum.magickcore.tool.EntityLightSourceHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LivingRenderer.class)
public abstract class MixinLivingRender<T extends LivingEntity, M extends EntityModel<T>>{
    @Redirect(
            method = "net/minecraft/client/renderer/entity/LivingRenderer.render (Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/renderer/entity/model/EntityModel.render (Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;IIFFFF)V"
            )
    )
    private void onRender_Redirect(EntityModel entityModel, MatrixStack matrixStackIn, IVertexBuilder ivertexbuilder, int packedLightIn, int i, float red, float green, float blue, float flag1) {
        //entityModel.render(matrixStackIn, ivertexbuilder, packedLightIn, i, red, green, blue, flag1);
    }

    @Inject(
            method = "net/minecraft/client/renderer/entity/LivingRenderer.render (Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/renderer/entity/model/EntityModel.render (Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;IIFFFF)V"
            )
    )
    public void onRender(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, CallbackInfo callbackInfo) {
        boolean flag = this.isVisible(entityIn);
        boolean flag1 = !flag && !entityIn.isInvisibleToPlayer(Minecraft.getInstance().player);
        boolean flag2 = Minecraft.getInstance().isEntityGlowing(entityIn);
        RenderType rendertype = this.func_230496_a_(entityIn, flag, flag1, flag2);
        if (rendertype != null) {
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(rendertype);
            int i = LivingRenderer.getPackedOverlay(entityIn, this.getOverlayProgress(entityIn, partialTicks));
            float[] color = EntityLightSourceHandler.getLightColor(Minecraft.getInstance().world, entityIn.getPositionVec(), 1, 1, 1, packedLightIn);
            getEntityModel().render(matrixStackIn, ivertexbuilder, packedLightIn, i, color[0], color[1], color[2], flag1 ? 0.15F : 1.0F);
        }
    }

    @Shadow
    @Nullable
    protected abstract RenderType func_230496_a_(T p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_);

    @Shadow
    protected abstract float getOverlayProgress(T livingEntityIn, float partialTicks);

    @Shadow
    public abstract M getEntityModel();

    @Shadow
    protected abstract boolean isVisible(T livingEntityIn);
}
