package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class MagickCraftingItemStackTileEntityRenderer extends ItemStackTileEntityRenderer {
    protected final ResourceLocation cylinder_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_rotate.png");
    protected final ResourceLocation orbTex = new ResourceLocation(MagickCore.MOD_ID +":textures/element/base/magick_orb.png");
    protected final ResourceLocation ripple_4 = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/ripple/ripple_4.png");

    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.push();
        //matrixStackIn.scale(0.9f, 0.6f, 0.6f);
        matrixStackIn.translate(0.5F, 0.5F, 0.5F);
        float xOffset = -1 / 32f;
        float zOffset = 0;
        matrixStackIn.translate(-xOffset, 0, -zOffset);
        matrixStackIn.translate(xOffset, 0, zOffset);
        if(transformType == ItemCameraTransforms.TransformType.GUI) {
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(47.5f));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(20));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(20));
            matrixStackIn.scale(0.9f, 0.9f, 0.9f);
        }
        else if(transformType == ItemCameraTransforms.TransformType.GROUND)
            matrixStackIn.scale(0.4f, 0.4f, 0.4f);
        else
            matrixStackIn.scale(0.6f, 0.6f, 0.6f);
        float[] color = RenderHelper.ORIGIN;
        ItemStack item = ItemStack.EMPTY;

        String type = LibElements.ORIGIN;

        if(stack.hasTag())
        {
            CompoundNBT blockTag = this.getBlockTag(stack.getTag());

            type = blockTag.getString("TYPE") == "" ? LibElements.ORIGIN : blockTag.getString("TYPE");
            if(blockTag.contains("MAIN_ITEM"))
                item = ItemStack.read(blockTag.getCompound("MAIN_ITEM"));
        }

        IManaElement element = ModElements.getElement(type);
        if(element != null)
            color = element.getRenderer().getColor();
        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
        RenderHelper.renderSphere(matrixStackIn.getLast().getMatrix(), (IRenderTypeBuffer.Impl) bufferIn, RenderHelper.getTexedSphereGlow(ripple_4), 16, 1f, color, RenderHelper.renderLight);
        RenderHelper.renderSphere(matrixStackIn.getLast().getMatrix(), (IRenderTypeBuffer.Impl) bufferIn, RenderHelper.getTexedSphereGlow(cylinder_rotate), 16, 1f, color, RenderHelper.renderLight);
        matrixStackIn.pop();
        matrixStackIn.scale(0.6f, 0.6f, 0.6f);
        //RenderHelper.renderParticle(matrixStackIn, bufferIn.getBuffer(RenderHelper.getTexedOrbGlow(orbTex)), 0.5f, color);

        IBakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(item, null, (LivingEntity) null);
        Minecraft.getInstance().getItemRenderer().renderItem(item, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, ibakedmodel);

        matrixStackIn.pop();
    }

    public static CompoundNBT getBlockTag(CompoundNBT tag)
    {
        CompoundNBT blockTag = tag.getCompound("BlockEntityTag");
        return blockTag;
    }
}
