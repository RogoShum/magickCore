package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.api.EnumManaLimit;
import com.rogoshum.magickcore.api.IManaItem;
import com.rogoshum.magickcore.api.IManaMaterial;
import com.rogoshum.magickcore.capability.IManaItemData;
import com.rogoshum.magickcore.lib.LibItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ManaRedstoneItem extends BaseItem implements IManaMaterial {
    public ManaRedstoneItem() {
        super(BaseItem.properties.maxStackSize(2));
    }

    @Override
    public int getManaNeed() {
        return 200;
    }

    @Override
    public boolean upgradeManaItem(IManaItemData data) {
        if(data.getTickTime() >= EnumManaLimit.TICK.getValue())
            return false;
        data.setTickTime(EnumManaLimit.TICK.limit(data.getTickTime() + 100));
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(LibItem.MANA_REDSTONE));
    }
}
