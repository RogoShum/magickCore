package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.IMaterialLimit;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.lib.LibItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

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
            if(data.spellContext().force() < ((IMaterialLimit) data).getMaterial().getForce()) {
                data.spellContext().force(data.spellContext().force() + 0.5f);
                return true;
            } else
                return false;
        } else {
            data.spellContext().force(data.spellContext().force() + 0.5f);
            return true;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent(LibItem.FUNCTION_MATERIAL));
        tooltip.add(new TranslatableComponent(LibItem.MANA_GLOWSTONE));
    }
}
