package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.api.item.IDimensionTooltip;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.common.lib.LibElementTool;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ElementStringItem extends ElementContainerItem implements IDimensionTooltip {
    public ElementStringItem() {
        super(BaseItem.properties());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if(NBTTagHelper.hasElement(stack)) {
            tooltip.add((new TranslatableComponent(LibItem.FUNCTION)).append(" ").append((withElementColor(new TranslatableComponent(LibElementTool.TOOL_ATTRIBUTE + NBTTagHelper.getElement(stack)), stack))));
        }
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, Level p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(p_77663_3_ instanceof ServerPlayer) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayer) p_77663_3_, LibAdvancements.ELEMENT_STRING);
        }
    }

    @Override
    public List<Component> dimensionToolTip(ItemStack stack) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add((new TranslatableComponent(LibItem.FUNCTION)).append(" ").append(ElementContainerItem.withElementColor(new TranslatableComponent(LibElementTool.TOOL_ATTRIBUTE + NBTTagHelper.getElement(stack)), stack)));
        return tooltip;
    }
}
