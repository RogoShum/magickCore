package com.rogoshum.magickcore.api.item;

import com.google.common.collect.ImmutableList;
import com.rogoshum.magickcore.api.entity.IQuadrantEntity;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.common.item.material.ElementItem;
import com.rogoshum.magickcore.common.item.material.ManaEnergyItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Objects;

public interface ISpiritDimension extends IDimensionItem {
    @Override
    default int slotSize(ItemStack stack) {
        return 6;
    }

    @Override
    default boolean shouldAddToSlots(Entity interactor, ItemStack stack, ImmutableList<ItemStack> slots) {
        if(stack.getItem() instanceof ManaEnergyItem) {
            ItemManaData data = ExtraDataUtil.itemManaData(stack);
            int flag = 0;
            if(data.spellContext().tick() > 0)
                flag++;
            if(data.spellContext().range() > 0)
                flag++;
            if(data.spellContext().force() > 0)
                flag++;
            if(flag != 1) return false;

            for(ItemStack energy : slots) {
                if(energy.getItem() instanceof ManaEnergyItem) {
                    ItemManaData energyData = ExtraDataUtil.itemManaData(energy);
                    if(energyData.spellContext().force() > 0 && data.spellContext().force() > 0)
                        return false;
                    if(energyData.spellContext().range() > 0 && data.spellContext().range() > 0)
                        return false;
                    if(energyData.spellContext().tick() > 0 && data.spellContext().tick() > 0)
                        return false;
                }
            }
            return true;
        }

        if(stack.getItem() instanceof ElementItem) {
            List<Entity> entities = interactor.level.getEntities(interactor, interactor.getBoundingBox().inflate(32));
            for(Entity entity : entities) {
                if(entity instanceof IQuadrantEntity && entity instanceof ISpellContext) {
                    if(((ISpellContext) entity).spellContext().element().type().equals(((ElementItem) stack.getItem()).getElementType())) {
                        AABB aabb = new AABB(entity.getEyePosition(), entity.getEyePosition()).inflate(((IQuadrantEntity) entity).range());
                        if(aabb.contains(interactor.position())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    default void onSetToSlot(Entity interactor, ItemStack original, ItemStack copy) {
        original.shrink(1);
        copy.setCount(1);
        if(interactor instanceof LivingEntity && copy.getItem() instanceof ManaEnergyItem) {
            float mana = ExtraDataUtil.entityStateData(interactor).getMaxManaValue();
            mana *= 0.002;
            ItemManaData data = ExtraDataUtil.itemManaData(copy);
            data.spellContext().tick((int) (data.spellContext().tick()*0.2));
            data.spellContext().range(data.spellContext().range()*0.2f);
            data.spellContext().force(data.spellContext().force()*0.2f);
            if (data.spellContext().tick() > mana * 20)
                data.spellContext().tick((int) (mana * 20));
            if (data.spellContext().range() > mana)
                data.spellContext().range(mana);
            if (data.spellContext().force() > mana)
                data.spellContext().force(mana);
        }
    }
}
