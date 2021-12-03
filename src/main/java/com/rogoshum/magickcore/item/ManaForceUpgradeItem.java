package com.rogoshum.magickcore.item;

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

public class ManaForceUpgradeItem extends BaseItem implements IManaMaterial {
    public ManaForceUpgradeItem() {
        super(BaseItem.properties());
    }

    @Override
    public int getManaNeed() {
        return 500;
    }

    @Override
    public boolean upgradeManaItem(IManaItemData data) {
        if(data.getForce() >= data.getMaterial().getForce())
            return false;
        data.setForce(Math.min(data.getMaterial().getForce(), data.getForce() + 0.5f));
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(LibItem.MANA_GLOWSTONE));
    }
}
