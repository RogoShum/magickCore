package com.rogoshum.magickcore.client.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.render.RenderHelper;
import com.rogoshum.magickcore.tool.NBTTagHelper;
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
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.awt.*;

public class OrbBottleRenderer extends ItemStackTileEntityRenderer {
    protected final ResourceLocation blank = new ResourceLocation(MagickCore.MOD_ID + ":textures/blank.png");
    protected final ResourceLocation cylinder_rotate = new ResourceLocation(MagickCore.MOD_ID + ":textures/element/base/cylinder_rotate.png");

    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLight, int combinedOverlay) {
        matrixStackIn.push();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        CompoundNBT nbt = stack.getTag();
        if(stack.hasTag() && nbt.contains("ELEMENT") && nbt.getString("ELEMENT") != LibElements.ORIGIN) {
            ItemStack potion = new ItemStack(Items.POTION);
            com.rogoshum.magickcore.magick.Color color = MagickCore.proxy.getElementRender(nbt.getString("ELEMENT")).getColor();
            float[] hsv = Color.RGBtoHSB((int)(color.r() * 255), (int)(color.g() * 255), (int)(color.b() * 255), null);
            CompoundNBT tag = NBTTagHelper.getStackTag(potion);
            tag.putInt("CustomPotionColor", MathHelper.hsvToRGB(hsv[0], hsv[1], hsv[2]));
            potion.setTag(tag);
            matrixStackIn.push();
            IBakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(potion, null, null);
            Minecraft.getInstance().getItemRenderer().renderItem(potion, transformType, false, matrixStackIn, bufferIn, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, ibakedmodel);
            matrixStackIn.pop();
        }
        else {
            ItemStack glass = new ItemStack(Items.GLASS_BOTTLE);
            CompoundNBT tag = NBTTagHelper.getStackTag(glass);
            ListNBT listnbt = new ListNBT();
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putString("id", String.valueOf((Object) ""));
            compoundnbt.putShort("lvl", (short)((byte)0));
            listnbt.add(compoundnbt);
            tag.put("Enchantments", listnbt);
            glass.setTag(tag);
            IBakedModel ibakedmodel_ = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(glass, null, null);
            Minecraft.getInstance().getItemRenderer().renderItem(glass, transformType, false, matrixStackIn, bufferIn, RenderHelper.renderLight, OverlayTexture.NO_OVERLAY, ibakedmodel_);
        }
        matrixStackIn.pop();
    }
}
