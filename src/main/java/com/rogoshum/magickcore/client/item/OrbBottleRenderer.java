package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.api.IManaItem;
import com.rogoshum.magickcore.capability.IManaItemData;
import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.helper.NBTTagHelper;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class OrbBottleRenderer extends ItemStackTileEntityRenderer {
    protected final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    protected final ResourceLocation cylinder_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_rotate.png");

    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.push();
        matrixStackIn.translate(0.5F, 0.5F, 0.5F);
        //matrixStackIn.translate(xOffset, 0, zOffset);

        CompoundNBT nbt = stack.getTag();
        if(stack.hasTag() && nbt.contains("ELEMENT") && nbt.getString("ELEMENT") != LibElements.ORIGIN) {
            ItemStack potion = new ItemStack(Items.POTION);
            float[] color = MagickCore.proxy.getElementRender(nbt.getString("ELEMENT")).getColor();
            float[] hsv = Color.RGBtoHSB((int)(color[0] * 255), (int)(color[1] * 255), (int)(color[2] * 255), null);
            CompoundNBT tag = NBTTagHelper.getStackTag(potion);
            tag.putInt("CustomPotionColor", MathHelper.hsvToRGB(hsv[0], hsv[1], hsv[2]));
            potion.setTag(tag);
            matrixStackIn.push();
            IBakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(potion, null, null);
            Minecraft.getInstance().getItemRenderer().renderItem(potion, transformType, false, matrixStackIn, bufferIn, combinedLight, combinedOverlay, ibakedmodel);
            matrixStackIn.pop();
        }
        else {
            ItemStack glass = new ItemStack(Items.GLASS_BOTTLE);
            IBakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(glass, null, null);
            Minecraft.getInstance().getItemRenderer().renderItem(glass, transformType, false, matrixStackIn, bufferIn, combinedLight, combinedOverlay, ibakedmodel_);
        }
        matrixStackIn.pop();
    }
}
