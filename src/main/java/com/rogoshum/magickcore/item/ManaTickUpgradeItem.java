package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.api.ISpellContext;
import com.rogoshum.magickcore.api.IManaMaterial;
import com.rogoshum.magickcore.lib.LibItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ManaTickUpgradeItem extends BaseItem implements IManaMaterial {
    public ManaTickUpgradeItem() {
        super(BaseItem.properties());
    }

    @Override
    public boolean disappearAfterRead() {
        return false;
    }

    @Override
    public int getManaNeed(ItemStack stack) {
        return 200;
    }

    @Override
    public boolean upgradeManaItem(ItemStack stack, ISpellContext data) {
        //if(data.getTickTime() >= data.getMaterial().getTick())
            //return false;
        data.spellContext().tick(data.spellContext().tick + 10);
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(LibItem.MANA_REDSTONE));
    }
}
