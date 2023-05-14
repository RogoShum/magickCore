package com.rogoshum.magickcore.common.item.tool;

import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.context.child.TraceContext;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LaserStaffItem extends ManaItem {
    public LaserStaffItem() {
        super(properties().stacksTo(1));
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack, InteractionHand handIn) {
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        MagickContext context = MagickContext.create(playerIn.level, data.spellContext()).caster(playerIn);
        if(context.containChild(LibContext.TRACE)) {
            TraceContext traceContext = context.getChild(LibContext.TRACE);
            traceContext.entity = MagickReleaseHelper.getEntityLookedAt(playerIn);
        }
        return MagickReleaseHelper.releaseMagick(context.hand(handIn));
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, Level p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(p_77663_3_ instanceof ServerPlayer) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayer) p_77663_3_, LibAdvancements.LASER_STAFF);
        }
    }
}
