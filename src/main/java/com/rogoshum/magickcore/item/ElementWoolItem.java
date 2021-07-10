package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.block.tileentity.ElementCrystalTileEntity;
import com.rogoshum.magickcore.block.tileentity.ElementWoolTileEntity;
import com.rogoshum.magickcore.lib.LibItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class ElementWoolItem extends BlockItem implements IItemColor {
    public ElementWoolItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
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
    protected boolean onBlockPlaced(BlockPos pos, World worldIn, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        if(stack.hasTag() && stack.getTag().contains("ELEMENT")) {
            ElementWoolTileEntity crystal = (ElementWoolTileEntity) worldIn.getTileEntity(pos);
            crystal.eType = stack.getTag().getString("ELEMENT");
        }
        return super.onBlockPlaced(pos, worldIn, player, stack, state);
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
