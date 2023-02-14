package com.rogoshum.magickcore.common.magick.context.child;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.util.ToolTipHelper;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

public class PotionContext extends ChildContext{
    private static final MutableComponent NO_EFFECT = (new TranslatableComponent("effect.none")).withStyle(ChatFormatting.GRAY);
    public List<MobEffectInstance> effectInstances = new ArrayList<>();

    @Override
    public ApplyType getLinkType() {
        return ApplyType.POTION;
    }

    public static PotionContext create(ItemStack stack) {
        PotionContext context = new PotionContext();
        for(MobEffectInstance effectInstance : PotionUtils.getMobEffects(stack)) {
            CompoundTag tag = effectInstance.save(new CompoundTag());
            tag.putInt("Duration", effectInstance.getDuration() / 10);
            context.effectInstances.add(MobEffectInstance.load(tag));
        }
        return context;
    }

    public static PotionContext create(Potion potion) {
        PotionContext context = new PotionContext();
        for(MobEffectInstance effectInstance : potion.getEffects()) {
            CompoundTag tag = effectInstance.save(new CompoundTag());
            tag.putInt("Duration", effectInstance.getDuration() / 10);
            context.effectInstances.add(MobEffectInstance.load(tag));
        }
        return context;
    }

    @Override
    public void serialize(CompoundTag tag) {
        effectInstances.forEach(effectInstance -> {
            tag.put(effectInstance.getDescriptionId(), effectInstance.save(new CompoundTag()));
        });
    }

    @Override
    public void deserialize(CompoundTag tag) {
        Iterator<String> it = tag.getAllKeys().iterator();
        while (it.hasNext()) {
            effectInstances.add(MobEffectInstance.load(tag.getCompound(it.next())));
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
        List<MobEffectInstance> list = effectInstances;
        List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();
        if (list.isEmpty()) {
            lores.nextTrans("  " + NO_EFFECT.getString());
        } else {
            for(MobEffectInstance effectinstance : list) {
                MutableComponent iformattabletextcomponent = new TranslatableComponent(effectinstance.getDescriptionId());
                MobEffect effect = effectinstance.getEffect();
                Map<Attribute, AttributeModifier> map = effect.getAttributeModifiers();
                if (!map.isEmpty()) {
                    for(Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                        AttributeModifier attributemodifier = entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), effect.getAttributeModifierValue(effectinstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                        list1.add(new Pair<>(entry.getKey(), attributemodifier1));
                    }
                }

                if (effectinstance.getAmplifier() > 0) {
                    iformattabletextcomponent = new TranslatableComponent("potion.withAmplifier", iformattabletextcomponent, new TranslatableComponent("potion.potency." + effectinstance.getAmplifier()));
                }

                if (effectinstance.getDuration() > 20) {
                    iformattabletextcomponent = new TranslatableComponent("potion.withDuration", iformattabletextcomponent, MobEffectUtil.formatDuration(effectinstance, durationFactor));
                }

                lores.nextTrans("  " + iformattabletextcomponent.getString(), effect.getCategory().getTooltipFormatting().toString());
            }
        }

        if (!list1.isEmpty()) {
            lores.nextTrans(TextComponent.EMPTY.getString());
            lores.nextTrans("  " + (new TranslatableComponent("potion.whenDrank")).getString(), ChatFormatting.DARK_PURPLE.toString());

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
                    lores.nextTrans("  " + (new TranslatableComponent("attribute.modifier.plus." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslatableComponent(pair.getFirst().getDescriptionId()))).getString(), ChatFormatting.BLUE.toString());
                } else if (d0 < 0.0D) {
                    d1 = d1 * -1.0D;
                    lores.nextTrans("  " + (new TranslatableComponent("attribute.modifier.take." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslatableComponent(pair.getFirst().getDescriptionId()))).getString(), ChatFormatting.RED.toString());
                }
            }
        }

    }
}
