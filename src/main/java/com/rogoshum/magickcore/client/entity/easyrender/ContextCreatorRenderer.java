package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.VectorHitReaction;
import com.rogoshum.magickcore.entity.pointed.ContextCreatorEntity;
import com.rogoshum.magickcore.lib.LibShaders;
import com.rogoshum.magickcore.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

import java.util.List;

public class ContextCreatorRenderer extends EasyRenderer<ContextCreatorEntity>{
    private static final ResourceLocation TAKEN = new ResourceLocation("magickcore:textures/entity/takensphere.png");

    @Override
    public void render(ContextCreatorEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        if(entityIn.spellContext().element != null && entityIn.spellContext().element.getRenderer() != null) {
            float scale = entityIn.getWidth() * 1.2f;
            if(entityIn.ticksExisted == 0)
                scale *= 0;
            else if(entityIn.ticksExisted < 30)
                scale *= 1f - 1f / (float)entityIn.ticksExisted;

            List<ContextCreatorEntity.PosItem> stacks = entityIn.getStacks();
            for(int i = 0; i < stacks.size(); i++) {
                ContextCreatorEntity.PosItem item = stacks.get(i);
                matrixStackIn.push();
                double x = item.prePos.x + (item.pos.x - item.prePos.x) * (double) partialTicks;
                double y = item.prePos.y + (item.pos.y - item.prePos.y) * (double) partialTicks;
                double z = item.prePos.z + (item.pos.z - item.prePos.z) * (double) partialTicks;
                matrixStackIn.translate(x, y, z);
                float f3 = ((float)item.age + partialTicks) / 20.0F + item.hoverStart;
                matrixStackIn.rotate(Vector3f.YP.rotation(f3));
                IBakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(item.getItemStack(), null, null);
                IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(bufferIn);
                Minecraft.getInstance().getItemRenderer().renderItem(item.getItemStack(), ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, renderTypeBuffer, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
                renderTypeBuffer.finish();
                matrixStackIn.pop();
            }

            matrixStackIn.push();
            ItemStack stack = new ItemStack(entityIn.getMaterial().getItem());
            float f3 = ((float)entityIn.ticksExisted + partialTicks) / 20.0F;
            matrixStackIn.rotate(Vector3f.YP.rotation(f3));
            IBakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, null, null);
            IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(bufferIn);
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, renderTypeBuffer, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
            renderTypeBuffer.finish();
            matrixStackIn.pop();
            Color color = entityIn.getInnerManaData().spellContext().element.color();
            int packedLightIn = Minecraft.getInstance().getRenderManager().getPackedLight(entityIn, partialTicks);
            matrixStackIn.scale(scale, scale, scale);
            VectorHitReaction[] test = {};
            RenderHelper.renderSphere(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedSphereGlow(TAKEN, 1f, 0f)).useShader(LibShaders.slime), 24, 0.5f, entityIn.getHitReactions(), color, packedLightIn, 2);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
            RenderHelper.renderSphere(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedSphereGlow(TAKEN, 1f, 0f)).useShader(LibShaders.slime), 24, 0.5f, entityIn.getHitReactions(), color, packedLightIn, 2);
        }
    }
}
