package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.common.magick.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RadianceCrystalRenderer extends BlockEntityWithoutLevelRenderer {
    private static final RenderType CRYSTAL_TYPE = RenderHelper.getTexturedItemGlint(RenderHelper.SPHERE_ROTATE, 1.0f, 0);
    public RadianceCrystalRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5, 0.1901, 0.5);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));
        Color color = Color.ORIGIN_COLOR;
        if(stack.hasTag() && stack.getTag().contains("ELEMENT")) {
            String ele = stack.getTag().getString("ELEMENT");
            MagickElement element = MagickRegistry.getElement(ele);
            color = element.secondaryColor();
        }
        if(transformType == ItemTransforms.TransformType.GUI || transformType == ItemTransforms.TransformType.GROUND)
            matrixStackIn.scale(0.35f, 0.35f, 0.35f);
        else {
            matrixStackIn.translate(0, 0.35, 0);
            matrixStackIn.scale(0.8f, 0.8f, 0.8f);
        }
        matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(45));
        matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(45));
        RenderHelper.renderCubeCache(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), CRYSTAL_TYPE)
                , new RenderHelper.RenderContext(0.7f, color, combinedLight));
        matrixStackIn.popPose();
    }
}
