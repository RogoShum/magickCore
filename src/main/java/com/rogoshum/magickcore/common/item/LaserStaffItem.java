package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.magick.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.util.ExtraDataUtil;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.init.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class LaserStaffItem extends ManaItem {
    public LaserStaffItem() {
        super(properties().maxStackSize(1));
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        ExtraDataUtil.itemData(stack).<ItemManaData>execute(LibRegistry.ITEM_DATA, data -> {
            MagickElement element = data.manaCapacity().getMana() > 0 ? data.spellContext().element : state.getElement();
            MagickContext context = MagickContext.create(playerIn.world, data.spellContext()).<MagickContext>applyType(ApplyType.SPAWN_ENTITY).caster(playerIn).element(element);
            SpawnContext spawnContext = SpawnContext.create(ModEntities.MANA_LASER.get());
            context.addChild(spawnContext);
            context.post(data.spellContext().copy().element(element));
            if(context.postContext.containChild(LibContext.TRACE)) {
                TraceContext traceContext = context.postContext.getChild(LibContext.TRACE);
                traceContext.entity = MagickReleaseHelper.getEntityLookedAt(playerIn);
            }
            MagickReleaseHelper.releaseMagick(context);
        });

        return true;
    }
}
