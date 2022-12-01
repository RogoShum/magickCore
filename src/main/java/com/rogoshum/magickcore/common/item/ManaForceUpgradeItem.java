package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.IMaterialLimit;
import com.rogoshum.magickcore.common.lib.LibItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ManaForceUpgradeItem extends BaseItem implements IManaMaterial {
    public ManaForceUpgradeItem() {
        super(properties());
    }

    @Override
    public boolean disappearAfterRead() {
        return false;
    }

    @Override
    public int getManaNeed(ItemStack stack) {
        return 500;
    }

    @Override
    public boolean upgradeManaItem(ItemStack stack, ISpellContext data) {
        if(data instanceof IMaterialLimit) {
            if(data.spellContext().force < ((IMaterialLimit) data).getMaterial().getForce()) {
                data.spellContext().force(data.spellContext().force + 0.5f);
                return true;
            } else
                return false;
        } else {
            data.spellContext().force(data.spellContext().force + 0.5f);
            return true;
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(LibItem.MANA_GLOWSTONE));
    }
}
