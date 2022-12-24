package com.rogoshum.magickcore.common.magick.context.child;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.util.ToolTipHelper;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.*;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PotionContext extends ChildContext{
    private static final IFormattableTextComponent field_242400_a = (new TranslationTextComponent("effect.none")).mergeStyle(TextFormatting.GRAY);
    public List<EffectInstance> effectInstances = new ArrayList<>();

    @Override
    public ApplyType getLinkType() {
        return ApplyType.POTION;
    }

    public static PotionContext create(ItemStack stack) {
        PotionContext context = new PotionContext();
        for(EffectInstance effectInstance : PotionUtils.getEffectsFromStack(stack)) {
            CompoundNBT tag = effectInstance.write(new CompoundNBT());
            tag.putInt("Duration", effectInstance.getDuration() / 10);
            context.effectInstances.add(EffectInstance.read(tag));
        }
        return context;
    }

    public static PotionContext create(Potion potion) {
        PotionContext context = new PotionContext();
        for(EffectInstance effectInstance : potion.getEffects()) {
            CompoundNBT tag = effectInstance.write(new CompoundNBT());
            tag.putInt("Duration", effectInstance.getDuration() / 10);
            context.effectInstances.add(EffectInstance.read(tag));
        }
        return context;
    }

    @Override
    public void serialize(CompoundNBT tag) {
        effectInstances.forEach(effectInstance -> {
            tag.put(effectInstance.getEffectName(), effectInstance.write(new CompoundNBT()));
        });
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        Iterator<String> it = tag.keySet().iterator();
        while (it.hasNext()) {
            effectInstances.add(EffectInstance.read(tag.getCompound(it.next())));
        }
    }

    @Override
    public boolean valid() {
        return effectInstances != null;
    }

    @Override
    public String getName() {
        return LibContext.POTION;
    }

    @Override
    public String getString(int tab) {
        ToolTipHelper toolTip = new ToolTipHelper();
        toolTip.tab = tab;
        toolTip.nextLine("{");
        addPotionTooltip(toolTip, 1.0f);
        toolTip.nextLine("}");
        return toolTip.getString();
    }

    public void addPotionTooltip(ToolTipHelper lores, float durationFactor) {
        List<EffectInstance> list = effectInstances;
        List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();
        if (list.isEmpty()) {
            lores.nextTrans("  " + field_242400_a.getString());
        } else {
            for(EffectInstance effectinstance : list) {
                IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(effectinstance.getEffectName());
                Effect effect = effectinstance.getPotion();
                Map<Attribute, AttributeModifier> map = effect.getAttributeModifierMap();
                if (!map.isEmpty()) {
                    for(Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                        AttributeModifier attributemodifier = entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), effect.getAttributeModifierAmount(effectinstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                        list1.add(new Pair<>(entry.getKey(), attributemodifier1));
                    }
                }

                if (effectinstance.getAmplifier() > 0) {
                    iformattabletextcomponent = new TranslationTextComponent("potion.withAmplifier", iformattabletextcomponent, new TranslationTextComponent("potion.potency." + effectinstance.getAmplifier()));
                }

                if (effectinstance.getDuration() > 20) {
                    iformattabletextcomponent = new TranslationTextComponent("potion.withDuration", iformattabletextcomponent, EffectUtils.getPotionDurationString(effectinstance, durationFactor));
                }

                lores.nextTrans("  " + iformattabletextcomponent.getString(), effect.getEffectType().getColor().toString());
            }
        }

        if (!list1.isEmpty()) {
            lores.nextTrans(StringTextComponent.EMPTY.getString());
            lores.nextTrans("  " + (new TranslationTextComponent("potion.whenDrank")).getString(), TextFormatting.DARK_PURPLE.toString());

            for(Pair<Attribute, AttributeModifier> pair : list1) {
                AttributeModifier attributemodifier2 = pair.getSecond();
                double d0 = attributemodifier2.getAmount();
                double d1;
                if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    d1 = attributemodifier2.getAmount();
                } else {
                    d1 = attributemodifier2.getAmount() * 100.0D;
                }

                if (d0 > 0.0D) {
                    lores.nextTrans("  " + (new TranslationTextComponent("attribute.modifier.plus." + attributemodifier2.getOperation().getId(), ItemStack.DECIMALFORMAT.format(d1), new TranslationTextComponent(pair.getFirst().getAttributeName()))).getString(), TextFormatting.BLUE.toString());
                } else if (d0 < 0.0D) {
                    d1 = d1 * -1.0D;
                    lores.nextTrans("  " + (new TranslationTextComponent("attribute.modifier.take." + attributemodifier2.getOperation().getId(), ItemStack.DECIMALFORMAT.format(d1), new TranslationTextComponent(pair.getFirst().getAttributeName()))).getString(), TextFormatting.RED.toString());
                }
            }
        }

    }
}
