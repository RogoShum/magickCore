package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.lib.LibRegistry;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.MagickElement;
import com.rogoshum.magickcore.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.magick.context.child.TraceContext;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.magick.extradata.item.ItemManaData;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.item.ItemStack;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class LaserStaffItem extends ManaItem {
    public LaserStaffItem() {
        super(BaseItem.properties().maxStackSize(1));
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        ExtraDataHelper.itemData(stack).<ItemManaData>execute(LibRegistry.ITEM_DATA, data -> {
            MagickElement element = data.manaCapacity().getMana() > 0 ? data.spellContext().element : state.getElement();
            MagickContext context = MagickContext.create(playerIn.world, data.spellContext()).<MagickContext>applyType(EnumApplyType.SPAWN_ENTITY).caster(playerIn).element(element);
            SpawnContext spawnContext = SpawnContext.create(ModEntities.mana_laser.get());
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
