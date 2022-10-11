package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.init.ModBlocks;
import net.minecraft.block.BlockState;
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

        if(transformType == ItemCameraTransforms.TransformType.GUI) {
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(47.5f));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(20));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(20));

            matrixStackIn.scale(0.65f, 0.65f, 0.65f);
            matrixStackIn.translate(0.1f, 0.4F, 0);
        }
        else if(transformType == ItemCameraTransforms.TransformType.GROUND) {
            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            matrixStackIn.translate(0.5F, 0.5F, 0.5F);
        }
        else {
            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            matrixStackIn.translate(0.5F, 0.5F, 0.5F);
        }

        BlockState state = ModBlocks.magick_crafting.get().getDefaultState();
        Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(state, matrixStackIn, bufferIn, combinedLight, combinedOverlay);
        ItemStack item = ItemStack.EMPTY;
        matrixStackIn.translate(0.5F, 0.5F, 0.5F);

        if(stack.hasTag())
        {
            CompoundNBT blockTag = getBlockTag(stack.getTag());

            if(blockTag.contains("MAIN_ITEM"))
                item = ItemStack.read(blockTag.getCompound("MAIN_ITEM"));
        }

        matrixStackIn.translate(0, 0.2f, 0);
        matrixStackIn.scale(0.6f, 0.6f, 0.6f);
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
