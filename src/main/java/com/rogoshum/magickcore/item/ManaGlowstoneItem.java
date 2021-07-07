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

public class ManaGlowstoneItem extends BaseItem implements IManaMaterial {
    public ManaGlowstoneItem() {
        super(BaseItem.properties.maxStackSize(2));
    }

    @Override
    public int getManaNeed() {
        return 500;
    }

    @Override
    public boolean upgradeManaItem(IManaItemData data) {
        if(data.getForce() >= EnumManaLimit.FORCE.getValue())
            return false;
        data.setForce(EnumManaLimit.FORCE.limit(data.getForce() + 1));
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(LibItem.MANA_GLOWSTONE));
    }
}
