package com.rogoshum.magickcore.client.entity.easyrender;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.client.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.entity.pointed.ContextPointerEntity;
import com.rogoshum.magickcore.lib.LibShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

import java.util.List;

public class ContextPointerRenderer extends EasyRenderer<ContextPointerEntity>{
    private static final ResourceLocation TAKEN = new ResourceLocation("magickcore:textures/entity/takensphere.png");

    @Override
    public void render(ContextPointerEntity entityIn, MatrixStack matrixStackIn, BufferBuilder bufferIn, float partialTicks) {
        List<ContextPointerEntity.PosItem> stacks = entityIn.getStacks();
        for(int i = 0; i < stacks.size(); i++) {
            ContextPointerEntity.PosItem item = stacks.get(i);
            matrixStackIn.push();
            double x = item.prePos.x + (item.pos.x - item.prePos.x) * (double) partialTicks;
            double y = item.prePos.y + (item.pos.y - item.prePos.y) * (double) partialTicks;
            double z = item.prePos.z + (item.pos.z - item.prePos.z) * (double) partialTicks;
            matrixStackIn.translate(x, y - entityIn.getHeight() / 2, z);
            float f3 = ((float)item.age + partialTicks) / 20.0F + item.hoverStart;
            matrixStackIn.rotate(Vector3f.YP.rotation(f3));
            IBakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(item.getItemStack(), null, null);
            IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.getImpl(bufferIn);
            Minecraft.getInstance().getItemRenderer().renderItem(item.getItemStack(), ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, renderTypeBuffer, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
            renderTypeBuffer.finish();
            matrixStackIn.pop();
        }

        float alpha = 0.5f - (float)entityIn.ticksExisted % 100 / 100f;
        alpha *= alpha * 4;
        if(alpha < 0.8f)
            alpha = 0.8f;

        float c = entityIn.ticksExisted % 30;
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(360f * (c / 29)));
        RenderHelper.CylinderContext context =
                new RenderHelper.CylinderContext(0.25f, 0.25f, 1
                        , 0.2f + entityIn.getHeight(), 16
                        , 0.5f * alpha, alpha, 0.3f);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                        wind, entityIn.getHeight(), 0f)).useShader(LibShaders.slime)
                , entityIn.spellContext().element.color()
                , context, entityIn.getHitReactions(), 0f);

        float height = entityIn.getHeight() - 0.2f;
        alpha = 1.0f;
        matrixStackIn.translate(0, 0.2, 0);
        context = new RenderHelper.CylinderContext(0.5f, 0.3f, 1.5f
                        , height, 16
                        , 0.3f * alpha, alpha, 0.3f);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                        wind, entityIn.getHeight(), 0f)).useShader(LibShaders.opacity)
                , entityIn.spellContext().element.color()
                , context, entityIn.getHitReactions(), 0f);
        matrixStackIn.translate(0, -0.2, 0);
        context = new RenderHelper.CylinderContext(0.4f, 0.25f, 1.5f
                , height, 16
                , 0.2f * alpha, alpha, 0.3f);
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, bufferIn, RenderHelper.getTexedCylinderGlint(
                        wind, entityIn.getHeight(), 0f)).useShader(LibShaders.opacity)
                , entityIn.spellContext().element.color()
                , context, entityIn.getHitReactions(), 0f);
    }
}
