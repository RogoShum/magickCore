package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.api.magick.condition.Condition;
import com.rogoshum.magickcore.api.magick.context.SpellContext;
import com.rogoshum.magickcore.api.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ConditionItem extends ManaItem implements IManaMaterial {
    private final String condition;
    public ConditionItem(String condition) {
        super(properties());
        this.condition = condition;
    }

    @Override
    public boolean disappearAfterRead() {
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public int getManaNeed(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean upgradeManaItem(ItemStack stack, ISpellContext data) {
        SpellContext spellContext = data.spellContext();
        ItemManaData thisData = ExtraDataUtil.itemManaData(stack);
        if(!thisData.spellContext().containChild(LibContext.CONDITION)) return false;
        ConditionContext conditionContext = thisData.spellContext().getChild(LibContext.CONDITION);
        if(spellContext.containChild(LibContext.CONDITION)) {
            ConditionContext context = spellContext.getChild(LibContext.CONDITION);
            for(Condition<?> condition : conditionContext.conditions) {
                CompoundTag tag = new CompoundTag();
                condition.write(tag);
                try {
                    Condition<?> condition1 = MagickRegistry.getCondition(condition.getName());
                    condition1.read(tag);
                    context.addCondition(condition1);
                }
                catch (Exception ignored) {

                }
            }
            spellContext.addChild(context);
        } else {
            ConditionContext context = ConditionContext.create();
            for(Condition<?> condition : conditionContext.conditions) {
                CompoundTag tag = new CompoundTag();
                condition.write(tag);
                try {
                    Condition<?> condition1 = MagickRegistry.getCondition(condition.getName());
                    condition1.read(tag);
                    context.addCondition(condition1);
                }
                catch (Exception ignored) {

                }
            }
            spellContext.addChild(context);
        }
        return true;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            ItemStack condition = new ItemStack(this);
            ExtraDataUtil.itemManaData(condition, (data) -> data.spellContext().addChild(ConditionContext.create(MagickRegistry.getCondition(this.condition))));
            items.add(condition);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent(LibItem.CONTEXT_MATERIAL));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack, InteractionHand hand) {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if(playerIn.isShiftKeyDown()) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            ItemManaData thisData = ExtraDataUtil.itemManaData(stack);
            if(!thisData.spellContext().containChild(LibContext.CONDITION))
                thisData.spellContext().addChild(ConditionContext.create());
            ConditionContext conditionContext = thisData.spellContext().getChild(LibContext.CONDITION);
            conditionContext.conditions.forEach(Condition::setNegate);
            thisData.spellContext().addChild(conditionContext);
            return InteractionResultHolder.success(stack);
        }
        MagickCore.LOGGER.info(playerIn.getMainHandItem().getTag());
        return super.use(worldIn, playerIn, handIn);
    }
}
