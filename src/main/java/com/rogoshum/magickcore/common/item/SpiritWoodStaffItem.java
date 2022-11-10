package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.common.api.mana.IManaContextItem;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.magick.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.util.ExtraDataUtil;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class SpiritWoodStaffItem extends ManaItem implements IManaContextItem {
    public SpiritWoodStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        return false;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 114514;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        super.onUsingTick(stack, player, count);
        EntityStateData state = ExtraDataUtil.entityStateData(player);
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        MagickContext magickContext = MagickContext.create(player.world, data.spellContext());
        MagickElement element = data.manaCapacity().getMana() > 0 ? data.spellContext().element : state.getElement();
        MagickContext context = magickContext.caster(player).victim(player).element(element);
        MagickReleaseHelper.releaseMagick(context);
    }
}
