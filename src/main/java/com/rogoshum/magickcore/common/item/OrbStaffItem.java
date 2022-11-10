package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.magick.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.util.ExtraDataUtil;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.init.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class OrbStaffItem extends ManaItem {

    public OrbStaffItem() {
        super(properties().maxStackSize(1));
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        MagickContext magickContext = MagickContext.create(playerIn.world, data.spellContext());
        MagickElement element = data.manaCapacity().getMana() > 0 ? data.spellContext().element : state.getElement();
        MagickContext context = magickContext.caster(playerIn).element(element);
        SpellContext orbContext = data.spellContext().copy().element(element);
        SpawnContext spawnSphere = SpawnContext.create(ModEntities.MANA_SPHERE.get());
        orbContext.addChild(spawnSphere);
        orbContext.applyType(ApplyType.SPAWN_ENTITY);
        orbContext.post(data.spellContext());
        orbContext.postContext.element(element);
        SpawnContext spawnContext = SpawnContext.create(ModEntities.MANA_ORB.get());
        context.addChild(spawnContext);
        context.post(orbContext);
        context.applyType(ApplyType.SPAWN_ENTITY);
        if(context.postContext.containChild(LibContext.TRACE)) {
            TraceContext traceContext = context.postContext.getChild(LibContext.TRACE);
            traceContext.entity = MagickReleaseHelper.getEntityLookedAt(playerIn);
        }

        return MagickReleaseHelper.releaseMagick(context);
    }
}
