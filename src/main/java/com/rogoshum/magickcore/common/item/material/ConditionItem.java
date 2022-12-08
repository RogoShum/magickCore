package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.IMaterialLimit;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.lib.LibConditions;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.magick.materials.Material;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.util.ItemStackUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;

public class ConditionItem extends ManaItem implements IManaMaterial {
    public ConditionItem() {
        super(properties());
    }

    @Override
    public boolean disappearAfterRead() {
        return false;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    @Override
    public int getManaNeed(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean upgradeManaItem(ItemStack stack, ISpellContext data) {
        SpellContext spellContext = data.spellContext();
        spellContext.merge(ExtraDataUtil.itemManaData(stack).spellContext());
        return true;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            ItemStack condition = new ItemStack(this);
            ItemStack living = condition.copy();
            ExtraDataUtil.itemManaData(living, (data) -> data.spellContext().addChild(ConditionContext.create(MagickRegistry.getCondition(LibConditions.LIVING_ENTITY))));
            ItemStack block = condition.copy();
            ExtraDataUtil.itemManaData(block, (data) -> data.spellContext().addChild(ConditionContext.create(MagickRegistry.getCondition(LibConditions.BLOCK_ONLY))));
            ItemStack non_living = condition.copy();
            ExtraDataUtil.itemManaData(non_living, (data) -> data.spellContext().addChild(ConditionContext.create(MagickRegistry.getCondition(LibConditions.NON_LIVING_ENTITY))));
            items.add(living);
            items.add(non_living);
            items.add(block);
        }
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        return false;
    }
}
