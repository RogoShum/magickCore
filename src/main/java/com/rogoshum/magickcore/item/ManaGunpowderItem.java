package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.enums.EnumManaType;
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

public class ManaGunpowderItem extends BaseItem implements IManaMaterial {
    public ManaGunpowderItem() {
        super(BaseItem.properties);
    }

    @Override
    public int getManaNeed() {
        return 800;
    }

    @Override
    public boolean upgradeManaItem(IManaItemData data) {
        if(data.getManaType().equals(EnumManaType.ATTACK) || data.getManaType().equals(EnumManaType.NONE)) {
            data.setManaType(EnumManaType.BUFF);
            return true;
        }

        return false;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(LibItem.MANA_GUNPOWDER));
    }
}
