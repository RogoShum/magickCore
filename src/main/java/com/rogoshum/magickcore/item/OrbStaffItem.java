package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.MagickElement;
import com.rogoshum.magickcore.magick.context.SpellContext;
import com.rogoshum.magickcore.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.magick.context.child.TraceContext;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.magick.extradata.item.ItemManaData;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class OrbStaffItem extends ManaItem {

    public OrbStaffItem() {
        super(BaseItem.properties().maxStackSize(1));
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        ItemManaData data = ExtraDataHelper.itemManaData(stack);
        MagickContext magickContext = MagickContext.create(playerIn.world, data.spellContext());
        MagickElement element = data.manaCapacity().getMana() > 0 ? data.spellContext().element : state.getElement();
        MagickContext context = magickContext.caster(playerIn).element(element);
        SpellContext orbContext = data.spellContext().copy().element(element);
        SpawnContext spawnSphere = SpawnContext.create(ModEntities.mana_sphere.get());
        orbContext.addChild(spawnSphere);
        orbContext.applyType(EnumApplyType.SPAWN_ENTITY);
        orbContext.post(data.spellContext());
        orbContext.postContext.element(element);
        SpawnContext spawnContext = SpawnContext.create(ModEntities.mana_orb.get());
        context.addChild(spawnContext);
        context.post(orbContext);
        context.applyType(EnumApplyType.SPAWN_ENTITY);
        if(context.postContext.containChild(LibContext.TRACE)) {
            TraceContext traceContext = context.postContext.getChild(LibContext.TRACE);
            traceContext.entity = MagickReleaseHelper.getEntityLookedAt(playerIn);
        }

        return MagickReleaseHelper.releaseMagick(context);
    }
}
