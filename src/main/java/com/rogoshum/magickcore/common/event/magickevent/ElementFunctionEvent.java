package com.rogoshum.magickcore.common.event.magickevent;

import com.rogoshum.magickcore.api.IConditionOnlyBlock;
import com.rogoshum.magickcore.api.IConditionOnlyEntity;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.event.ElementEvent;
import com.rogoshum.magickcore.common.init.ModConfig;
import com.rogoshum.magickcore.common.lib.LibConditions;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class ElementFunctionEvent {

    @SubscribeEvent
    public void applyFunction(ElementEvent.ElementFunctionApply event) {
        String elementFunction = event.getMagickContext().element.type() +"_"+event.getMagickContext().applyType.getLabel();
        if(event.getMagickContext().doBlock)
            elementFunction = "block_"+elementFunction;
        if(ModConfig.ELEMENT_BAN.get().contains(elementFunction)) {
            event.setCanceled(true);
            return;
        }
        if(event.getMagickContext().victim instanceof ItemEntity && event.getMagickContext().applyType == ApplyType.ATTACK)
            event.setCanceled(true);

        if(event.getMagickContext().doBlock) {
            Entity last = event.getMagickContext().projectile;
            AtomicBoolean entityOnly = new AtomicBoolean(false);
            if(last instanceof IManaEntity) {
                SpellContext spellContext = ((IManaEntity) last).spellContext();
                if(spellContext.containChild(LibContext.CONDITION)) {
                    ConditionContext condition = spellContext.getChild(LibContext.CONDITION);
                    condition.conditions.forEach(condition1 -> {
                        if(condition1 instanceof IConditionOnlyEntity)
                            entityOnly.set(true);
                    });
                }
            }
            if(entityOnly.get())
                event.setCanceled(true);
        } else if(!event.getMagickContext().applyType.isForm()) {
            Entity last = event.getMagickContext().projectile;
            AtomicBoolean blockOnly = new AtomicBoolean(false);
            if(last instanceof IManaEntity) {
                SpellContext spellContext = ((IManaEntity) last).spellContext();
                if(spellContext.containChild(LibContext.CONDITION)) {
                    ConditionContext condition = spellContext.getChild(LibContext.CONDITION);
                    condition.conditions.forEach(condition1 -> {
                        if(condition1 instanceof IConditionOnlyBlock)
                            blockOnly.set(true);
                    });
                }
            }
            if(blockOnly.get())
                event.setCanceled(true);
        }
    }
}
