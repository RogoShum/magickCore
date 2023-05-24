package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.api.item.IDimensionTooltip;
import com.rogoshum.magickcore.common.lib.LibElementTool;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AssemblyEssenceItem extends ElementContainerItem implements IDimensionTooltip {
    public AssemblyEssenceItem() {
        super(BaseItem.properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if(NBTTagHelper.hasElement(stack)) {
            tooltip.add(new TranslatableComponent(LibItem.FUNCTION).append(" ").append(withElementColor(new TranslatableComponent(LibElementTool.TOOL_ESSENCE + NBTTagHelper.getElement(stack)), stack)));
        }
    }

    @Override
    public List<Component> dimensionToolTip(ItemStack stack) {
        List<Component> components = new ArrayList<>();
        components.add(new TranslatableComponent(LibItem.FUNCTION).append(" ").append(ElementContainerItem.withElementColor(new TranslatableComponent(LibElementTool.TOOL_ESSENCE + NBTTagHelper.getElement(stack)), stack)));
        return components;
    }
}
