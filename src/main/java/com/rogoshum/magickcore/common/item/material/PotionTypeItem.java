package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.init.ModGroups;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.PotionContext;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PotionTypeItem extends ManaItem implements IManaMaterial {

    public PotionTypeItem() {
        super(properties());
    }

    @Override
    public boolean disappearAfterRead() {
        return false;
    }

    public void fillPotion(NonNullList<ItemStack> items, ItemStack stack, Potion potion) {
        ItemStack itemStack = stack.copy();
        if(potion.getEffects().isEmpty()) return;
        ExtraDataUtil.itemManaData(itemStack, (data) -> {
            data.spellContext().addChild(PotionContext.create(potion));
            data.spellContext().applyType(ApplyType.POTION);
        });
        items.add(itemStack);
    }

    public static boolean canTransform(ItemStack stack) {
        if(stack.getItem() == Items.POTION.asItem()) {
            return !PotionUtils.getPotion(stack).getEffects().isEmpty();
        }
        return false;
    }

    public static ItemStack transformToType(ItemStack stack) {
        if(stack.getItem() == Items.POTION.asItem()) {
            ItemStack sample = new ItemStack(ModItems.POTION_TYPE.get());
            ExtraDataUtil.itemManaData(sample, (data) -> {
                data.spellContext().applyType(ApplyType.POTION);
                data.spellContext().addChild(PotionContext.create(stack));
            });
            return sample;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        ItemStack sample = new ItemStack(this);
        ExtraDataUtil.itemManaData(sample, (data) -> data.spellContext().applyType(ApplyType.POTION));
        if (group == ModGroups.POTION_TYPE_GROUP) {
            Registry.POTION.forEach(potion -> fillPotion(items, sample, potion));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent(LibItem.CONTEXT_MATERIAL));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public int getManaNeed(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    @Override
    public boolean upgradeManaItem(ItemStack stack, ISpellContext data) {
        SpellContext spellContext = data.spellContext();
        spellContext.applyType(ApplyType.POTION);
        spellContext.merge(ExtraDataUtil.itemManaData(stack).spellContext());
        return true;
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        return false;
    }
}
