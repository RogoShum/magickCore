package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.common.tileentity.ElementWoolTileEntity;
import com.rogoshum.magickcore.client.item.ElementWoolRenderer;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.init.ModBlocks;
import com.rogoshum.magickcore.common.init.ModGroups;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ElementWoolItem extends BlockItem{
    public ElementWoolItem() {
        super(ModBlocks.ELEMENT_WOOL.get(), BaseItem.properties().stacksTo(64).setISTER(() -> ElementWoolRenderer::new));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if(tag.contains("ELEMENT"))
                tooltip.add((new TranslationTextComponent(LibItem.ELEMENT)).append(" ").append((new TranslationTextComponent(MagickCore.MOD_ID + ".description." + tag.getString("ELEMENT")))));
        }
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, World worldIn, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        if(stack.hasTag() && stack.getTag().contains("ELEMENT")) {
            ElementWoolTileEntity crystal = (ElementWoolTileEntity) worldIn.getBlockEntity(pos);
            crystal.eType = stack.getTag().getString("ELEMENT");
        }
        return super.updateCustomBlockEntityTag(pos, worldIn, player, stack, state);
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if(group == ModGroups.ELEMENT_ITEM_GROUP) {
            MagickRegistry.getRegistry(LibRegistry.ELEMENT).registry().forEach( (key, value) ->
                    items.add(NBTTagHelper.setElement(new ItemStack(this), key))
            );
        }
    }
}
