package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.init.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class LaserStaffItem extends ManaItem {
    public LaserStaffItem() {
        super(properties().maxStackSize(1));
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        ExtraDataUtil.itemManaData(stack, data -> {
            MagickElement element = data.spellContext().element;
            MagickContext context = MagickContext.create(playerIn.world, data.spellContext()).<MagickContext>applyType(ApplyType.SPAWN_ENTITY).caster(playerIn).element(element);
            SpawnContext spawnContext = SpawnContext.create(ModEntities.MANA_LASER.get());
            context.addChild(spawnContext);
            context.tick(Math.max(context.tick, 100));
            context.post(data.spellContext().copy().element(element));
            if(context.postContext.containChild(LibContext.TRACE)) {
                TraceContext traceContext = context.postContext.getChild(LibContext.TRACE);
                traceContext.entity = MagickReleaseHelper.getEntityLookedAt(playerIn);
            }
            MagickReleaseHelper.releaseMagick(context);
        });

        return true;
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(p_77663_3_ instanceof ServerPlayerEntity) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayerEntity) p_77663_3_, LibAdvancements.LASER_STAFF);
        }
    }
}
