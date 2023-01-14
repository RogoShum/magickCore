package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.magick.condition.Condition;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.ConditionContext;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

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
        ItemManaData thisData = ExtraDataUtil.itemManaData(stack);
        if(!thisData.spellContext().containChild(LibContext.CONDITION)) return false;
        ConditionContext conditionContext = thisData.spellContext().getChild(LibContext.CONDITION);
        if(spellContext.containChild(LibContext.CONDITION)) {
            ConditionContext context = spellContext.getChild(LibContext.CONDITION);
            for(Condition<?> condition : conditionContext.conditions) {
                CompoundNBT tag = new CompoundNBT();
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
                CompoundNBT tag = new CompoundNBT();
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
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            ItemStack condition = new ItemStack(this);
            ExtraDataUtil.itemManaData(condition, (data) -> data.spellContext().addChild(ConditionContext.create(MagickRegistry.getCondition(this.condition))));
            items.add(condition);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(LibItem.CONTEXT_MATERIAL));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        return false;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(playerIn.isShiftKeyDown()) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            ItemManaData thisData = ExtraDataUtil.itemManaData(stack);
            if(!thisData.spellContext().containChild(LibContext.CONDITION))
                thisData.spellContext().addChild(ConditionContext.create());
            ConditionContext conditionContext = thisData.spellContext().getChild(LibContext.CONDITION);
            conditionContext.conditions.forEach(Condition::setNegate);
            thisData.spellContext().addChild(conditionContext);
            return ActionResult.success(stack);
        }
        MagickCore.LOGGER.info(playerIn.getMainHandItem().getTag());
        return super.use(worldIn, playerIn, handIn);
    }
}
