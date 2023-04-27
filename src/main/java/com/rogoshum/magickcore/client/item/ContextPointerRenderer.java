package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.common.entity.pointed.ContextPointerEntity;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;

public class ContextPointerRenderer extends BlockEntityWithoutLevelRenderer {
    protected static final ResourceLocation wind = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/wind.png");
    protected static final RenderType TYPE = RenderHelper.getTexedCylinderItem(wind, 1.2f, 0);

    public ContextPointerRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

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

        float height = 1.2f;
        float radius = 0.25f;

        RenderHelper.CylinderContext context = new RenderHelper.CylinderContext(radius, radius, 1, height, 8, 0.0f, alpha, 0.5f, pointer.spellContext().element.primaryColor());
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), TYPE)
                , RenderHelper.drawCylinder(context, null, 1));

        context = new RenderHelper.CylinderContext(0.7f, 0.6f, 1.5f, height, 8, 0.12f, alpha, 0.3f, pointer.spellContext().element.primaryColor());
        RenderHelper.renderCylinder(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), TYPE)
                , RenderHelper.drawCylinder(context, null, 1));
        matrixStackIn.popPose();
    }
}
