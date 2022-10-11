package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.api.IManaContextItem;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class MagickContextItem extends ManaItem{
    public MagickContextItem() {
        super(BaseItem.properties().maxStackSize(16).setISTER(() -> ManaEnergyRenderer::new));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        return false;
    }
}
