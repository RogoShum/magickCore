package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.common.api.mana.IManaContextItem;
import com.rogoshum.magickcore.client.item.StaffRenderer;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.magick.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.util.ExtraDataUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class SpiritCrystalStaffItem extends ManaItem implements IManaContextItem {
    public SpiritCrystalStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        MagickContext magickContext = MagickContext.create(playerIn.world, data.spellContext());
        MagickElement element = data.manaCapacity().getMana() > 0 ? data.spellContext().element : state.getElement();
        MagickContext context = magickContext.caster(playerIn).victim(playerIn).element(element);
        return MagickReleaseHelper.releaseMagick(context);
    }
}
