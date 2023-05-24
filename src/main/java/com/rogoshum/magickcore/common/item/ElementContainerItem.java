package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.common.init.ModGroups;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.common.magick.Color;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ElementContainerItem extends BaseItem{
    public ElementContainerItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if(NBTTagHelper.hasElement(stack)) {
                tooltip.add((new TranslatableComponent(LibItem.ELEMENT)).append(" ").append(withElementColor(new TranslatableComponent(MagickCore.MOD_ID + ".description." + NBTTagHelper.getElement(stack)), stack)));
        }
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if(group == ModGroups.ELEMENT_ITEM_GROUP) {
            MagickRegistry.getRegistry(LibRegistry.ELEMENT).registry().forEach( (key, value) ->
                    items.add(NBTTagHelper.setElement(new ItemStack(this), key))
            );
        }
    }

    public static Component withElementColor(Component component, MagickElement element) {
        return withColor(component, element.primaryColor());
    }

    public static Component withElementColor(Component component, String element) {
        return withElementColor(component, MagickRegistry.getElement(element));
    }

    public static Component withElementColor(Component component, ItemStack stack) {
        return withElementColor(component, NBTTagHelper.getElement(stack));
    }

    public static Component withColor(Component component, Color color) {
        if(component instanceof MutableComponent m) {
            return m.setStyle(m.getStyle().withColor(color.decimalColor()));
        }
        return new TextComponent(component.getString()).withStyle(component.getStyle().withColor(color.textColor()));
    }
}
