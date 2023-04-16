package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.lib.LibItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ManaGunpowderItem extends BaseItem implements IManaMaterial {
    public ManaGunpowderItem() {
        super(properties());
    }

    @Override
    public boolean disappearAfterRead() {
        return false;
    }

    @Override
    public boolean typeMaterial() {
        return true;
    }

    @Override
    public boolean singleMaterial() {
        return true;
    }

    @Override
    public int getManaNeed(ItemStack stack) {
        return 800;
    }

    @Override
    public boolean upgradeManaItem(ItemStack stack, ISpellContext data) {
        data.spellContext().applyType(ApplyType.BUFF);
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent(LibItem.CONTEXT_MATERIAL));
        tooltip.add(new TranslatableComponent(LibItem.MANA_GUNPOWDER));
    }
}
