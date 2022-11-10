package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.client.model.OrbBottleModel;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class OrbBottleRenderer extends ItemStackTileEntityRenderer {
    protected final ResourceLocation BOTTLE = new ResourceLocation(MagickCore.MOD_ID + ":textures/items/orb_bottle/bottle.png");
    protected final ResourceLocation LIQUID = new ResourceLocation(MagickCore.MOD_ID + ":textures/items/orb_bottle/liquid.png");
    private final OrbBottleModel model = new OrbBottleModel();

    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.push();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        matrixStackIn.rotate(Vector3f.ZN.rotationDegrees(180));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(270));
        matrixStackIn.translate(0, -0.95, 0);
        if(stack.hasTag() && NBTTagHelper.hasElement(stack)) {
            Color color = MagickRegistry.getElement(NBTTagHelper.getElement(stack)).color();
            this.model.render(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedEntity(LIQUID)), RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, color.r(), color.g(), color.b(), 1.0F);
        }
        this.model.render(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedEntity(BOTTLE)), combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.3F);
        matrixStackIn.pop();
    }
}
