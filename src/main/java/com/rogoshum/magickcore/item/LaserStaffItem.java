package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.IManaElement;
import com.rogoshum.magickcore.api.IManaItem;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.helper.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModEntites;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class LaserStaffItem extends ManaItem {
    public LaserStaffItem() {
        super(BaseItem.properties.maxStackSize(1));
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, IEntityState state, ItemStack stack) {
        IManaItem item = (IManaItem) stack.getItem();
        UUID trace = MagickCore.emptyUUID;
        if(this.getTrace(stack))
            trace = MagickReleaseHelper.getTraceEntity(playerIn);
        IManaElement element = item.getMana(stack) > 0 ? this.getElement(stack) : state.getElement();
            MagickReleaseHelper.releaseProjectileEntity(ModEntites.mana_laser, playerIn, element, trace, this.getForce(stack), this.getTickTime(stack)
                    , this.getRange(stack), this.getManaType(stack));

        MagickCore.LOGGER.debug(this.getRange(stack));
        return false;
    }
}
