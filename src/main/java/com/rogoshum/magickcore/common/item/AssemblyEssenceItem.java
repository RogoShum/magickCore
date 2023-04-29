package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.api.itemstack.IDimensionItem;
import com.rogoshum.magickcore.common.lib.LibElementTool;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class AssemblyEssenceItem extends ElementContainerItem{
    public AssemblyEssenceItem() {
        super(BaseItem.properties());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if(NBTTagHelper.hasElement(stack)) {
            tooltip.add((new TranslatableComponent(LibElementTool.TOOL_DESCRIPTION)));
            tooltip.add((new TranslatableComponent(LibElementTool.TOOL_ESSENCE + NBTTagHelper.getElement(stack))));
        }
    }
}
