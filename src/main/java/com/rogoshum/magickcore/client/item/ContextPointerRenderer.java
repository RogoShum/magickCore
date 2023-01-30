package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.entity.pointed.ContextPointerEntity;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

public class ContextPointerRenderer extends EasyItemRenderer {
    protected static final ResourceLocation wind = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/wind.png");

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        Entity entity = NBTTagHelper.createEntityByItem(stack, Minecraft.getInstance().level);
        ContextPointerEntity pointer = ModEntities.CONTEXT_POINTER.get().create(Minecraft.getInstance().level);
        if(entity instanceof ContextPointerEntity)
            pointer = (ContextPointerEntity) entity;
        float alpha = 0.5f - MagickCore.proxy.getRunTick() % 100 / 100f;
        alpha *= alpha * 4;
        if(alpha < 0.8f)
            alpha = 0.8f;

        float c = MagickCore.proxy.getRunTick() % 30;
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(360f * (c / 29)));

        float height = 0.2f + pointer.getBbHeight();
        float radius = 0.25f;
        RenderHelper.CylinderContext context = new RenderHelper.CylinderContext(radius, radius, 1, height, 16, 0, alpha, 0.3f, pointer.spellContext().element.color());
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RenderHelper.getTexedCylinderGlint(
                        wind, pointer.getBbHeight(), 0f))
                , RenderHelper.drawCylinder(context, null, 0));

        height = pointer.getBbHeight() - 0.2f;
        alpha = 1.0f;
        matrixStackIn.translate(0, 0.2, 0);
        radius = 0.5f;
        context = new RenderHelper.CylinderContext(radius, radius, 1, height, 16, 0, alpha, 0.3f, pointer.spellContext().element.color());
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RenderHelper.getTexedCylinderGlint(
                        wind, pointer.getBbHeight(), 0f))
                , RenderHelper.drawCylinder(context, null, 0));
        matrixStackIn.translate(0, -0.2, 0);
        radius = 0.4f;
        context = new RenderHelper.CylinderContext(radius, radius, 1, height, 16, 0, alpha, 0.3f, pointer.spellContext().element.color());
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), RenderHelper.getTexedCylinderGlint(
                        wind, pointer.getBbHeight(), 0f))
                , RenderHelper.drawCylinder(context, null, 0));
        matrixStackIn.popPose();
    }
}
