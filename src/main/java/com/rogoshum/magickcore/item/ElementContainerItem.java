package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.lib.LibItem;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class ElementContainerItem extends BaseItem implements IItemColor {
    public ElementContainerItem(Properties properties) {
        super(properties);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if(tag.contains("ELEMENT"))
                tooltip.add((new TranslationTextComponent(LibItem.ELEMENT)).appendString(" ").append((new TranslationTextComponent(MagickCore.MOD_ID + ".description." + tag.getString("ELEMENT")))));
        }
    }

    @Override
    public int getColor(ItemStack stack, int p_getColor_2_) {
        if(stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if (tag.contains("ELEMENT")) {
                float[] color = MagickCore.proxy.getElementRender(tag.getString("ELEMENT")).getColor();
                float[] hsv = Color.RGBtoHSB((int) (color[0] * 255), (int) (color[1] * 255), (int) (color[2] * 255), null);
                return MathHelper.hsvToRGB(hsv[0], hsv[1], hsv[2]);
            }
        }
        return 0;
    }
}
