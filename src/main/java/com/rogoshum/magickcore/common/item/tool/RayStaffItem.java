package com.rogoshum.magickcore.common.item.tool;

import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.event.magickevent.AdvancementsEvent;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RayStaffItem extends ManaItem{
    public RayStaffItem() {
        super(properties().stacksTo(1));
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        MagickContext magickContext = MagickContext.create(playerIn.level, data.spellContext());
        MagickElement element = data.spellContext().element;
        MagickContext context = magickContext.caster(playerIn).element(element);
        SpawnContext spawnContext = SpawnContext.create(ModEntities.RAY.get());
        context.tick(Math.max(context.tick, 100));
        context.addChild(spawnContext);
        context.post(data.spellContext().copy().element(element));
        if(context.postContext.containChild(LibContext.TRACE)) {
            TraceContext traceContext = context.postContext.getChild(LibContext.TRACE);
            traceContext.entity = MagickReleaseHelper.getEntityLookedAt(playerIn);
        }
        context.applyType(ApplyType.SPAWN_ENTITY);
        return MagickReleaseHelper.releaseMagick(context);
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, Level p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(p_77663_3_ instanceof ServerPlayer) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayer) p_77663_3_, LibAdvancements.RAY_STAFF);
        }
    }
}
