package com.rogoshum.magickcore.common.integration.curios;

import com.rogoshum.magickcore.common.item.SpiritCrystalRingItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CuriosHelper {
    public static boolean hasSpiritRing(LivingEntity entity) {
        return getSpiritRing(entity) != null;
    }

    @Nullable
    public static ItemStack getSpiritRing(LivingEntity entity) {
        /*
        List<ItemStack> spellContext = new ArrayList<ItemStack>();

        CuriosApi.getCuriosHelper().getCuriosHandler(entity).ifPresent(handler -> {

            ICurioStacksHandler stacksHandler = handler.getCurios().get("spirit_crystal_ring");

            if (stacksHandler != null) {

                IDynamicStackHandler soloStackHandler = stacksHandler.getStacks();

                if (soloStackHandler != null) {
                    for (int i = 0; i < stacksHandler.getSlots(); i++) {
                        if (soloStackHandler.getStackInSlot(i) != null && soloStackHandler.getStackInSlot(i).getItem() instanceof SpiritCrystalRingItem) {
                            spellContext.add(soloStackHandler.getStackInSlot(i));
                            break;
                        }
                    }
                }

            }

        });

        return spellContext.isEmpty() ? null : spellContext.get(0);

         */
        return ItemStack.EMPTY;
    }
}
