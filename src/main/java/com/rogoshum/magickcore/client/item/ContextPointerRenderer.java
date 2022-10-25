package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.render.BufferContext;
import com.rogoshum.magickcore.client.render.RenderHelper;
import com.rogoshum.magickcore.entity.pointed.ContextPointerEntity;
import com.rogoshum.magickcore.init.ModEntities;
import com.rogoshum.magickcore.tool.NBTTagHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class ContextPointerRenderer extends ItemStackTileEntityRenderer {
    protected static final ResourceLocation wind = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/wind.png");

    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.push();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        Entity entity = NBTTagHelper.createEntityByItem(stack, Minecraft.getInstance().world);
        ContextPointerEntity pointer = ModEntities.context_pointer.get().create(Minecraft.getInstance().world);
        if(entity instanceof ContextPointerEntity)
            pointer = (ContextPointerEntity) entity;
        float alpha = 0.5f - MagickCore.proxy.getRunTick() % 100 / 100f;
        alpha *= alpha * 4;
        if(alpha < 0.8f)
            alpha = 0.8f;

        float c = MagickCore.proxy.getRunTick() % 30;
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(360f * (c / 29)));

        float height = 0.2f + pointer.getHeight();
        float radius = 0.25f;
        RenderHelper.CylinderContext context = new RenderHelper.CylinderContext(radius, radius, 1, height, 16, 0, alpha, 0.3f, pointer.spellContext().element.color());
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), RenderHelper.getTexedCylinderGlint(
                        wind, pointer.getHeight(), 0f))
                , RenderHelper.drawCylinder(context, null, 0));

        height = pointer.getHeight() - 0.2f;
        alpha = 1.0f;
        matrixStackIn.translate(0, 0.2, 0);
        radius = 0.5f;
        context = new RenderHelper.CylinderContext(radius, radius, 1, height, 16, 0, alpha, 0.3f, pointer.spellContext().element.color());
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), RenderHelper.getTexedCylinderGlint(
                        wind, pointer.getHeight(), 0f))
                , RenderHelper.drawCylinder(context, null, 0));
        matrixStackIn.translate(0, -0.2, 0);
        radius = 0.4f;
        context = new RenderHelper.CylinderContext(radius, radius, 1, height, 16, 0, alpha, 0.3f, pointer.spellContext().element.color());
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, Tessellator.getInstance().getBuffer(), RenderHelper.getTexedCylinderGlint(
                        wind, pointer.getHeight(), 0f))
                , RenderHelper.drawCylinder(context, null, 0));
        matrixStackIn.pop();
    }
}
