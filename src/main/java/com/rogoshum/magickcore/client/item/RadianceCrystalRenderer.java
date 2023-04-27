package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.render.easyrender.BufferContext;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RadianceCrystalRenderer extends BlockEntityWithoutLevelRenderer {
    private static final RenderType CRYSTAL_TYPE = RenderHelper.getTexedOrbItem(RenderHelper.SPHERE_ROTATE);
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
        if(transformType != ItemTransforms.TransformType.FIXED)
            matrixStackIn.scale(0.25f, 0.25f, 0.25f);
        else {
            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
        }
        matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(45));
        matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(45));
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), CRYSTAL_TYPE)
                , new RenderHelper.RenderContext(0.3f, color, combinedLight));
        matrixStackIn.scale(0.8f, 0.8f, 0.8f);
        RenderHelper.renderCubeDynamic(BufferContext.create(matrixStackIn, Tesselator.getInstance().getBuilder(), CRYSTAL_TYPE)
                , new RenderHelper.RenderContext(0.5f, color, combinedLight));
        matrixStackIn.popPose();
    }
}
