package com.rogoshum.magickcore.api.magick.context.child;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.TargetType;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.condition.Condition;
import com.rogoshum.magickcore.common.util.ToolTipHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Arrays;
import java.util.HashSet;

public class ConditionContext extends ChildContext{
    public static final Type<ConditionContext> TYPE = new Type<>(LibContext.CONDITION);
    public HashSet<Condition<?>> conditions = new HashSet<>();

    public static ConditionContext create(Condition<?> condition) {
        ConditionContext context = new ConditionContext();
        context.conditions = new HashSet<>(Arrays.asList(condition));
        return context;
    }

    public static ConditionContext create() {
        return new ConditionContext();
    }

    @Override
    public void serialize(CompoundTag tag) {
        if(conditions.isEmpty()) return;
        CompoundTag compoundNBT = new CompoundTag();
        conditions.forEach((condition) -> {
            CompoundTag conditionTag = new CompoundTag();
            condition.write(conditionTag);
            compoundNBT.put(condition.getName(), conditionTag);
        });
        tag.put("Conditions", compoundNBT);
    }

    public void addCondition(Condition<?>... conditions) {
        this.conditions.addAll(Arrays.asList(conditions));
    }

    public <T> boolean test(T self, T target) {
        for (Condition<T> condition : Util.make(new HashSet<Condition<T>>(), set -> {
            conditions.forEach(condition -> set.add((Condition<T>) condition));
        })) {
            T entity = self;
            if(condition.getType().equals(TargetType.TARGET))
                entity = target;
            if(!condition.suitable(entity)) continue;
            if(!(!condition.isNegate() && condition.test(entity)) && !(condition.isNegate() && !condition.test(entity)))
                return false;
        }
        return true;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        if(!tag.contains("Conditions")) return;
        CompoundTag compoundNBT = tag.getCompound("Conditions");
        compoundNBT.getAllKeys().forEach((key) -> {
            Condition<?> condition = MagickRegistry.getCondition(key);
            if(condition != null) {
                condition.read(compoundNBT.getCompound(key));
                conditions.add(condition);
            }
        });
    }

    @Override
    public boolean valid() {
        return !conditions.isEmpty();
    }

    @Override
    public Type<ConditionContext> getType() {
        return TYPE;
    }

    @Override
    public String getString(int tab) {
        if(conditions.isEmpty())
            return "";
        ToolTipHelper toolTip = new ToolTipHelper();
        toolTip.tab = tab;
        toolTip.nextLine("{");
        conditions.forEach((condition) -> {
            toolTip.push();
            toolTip.nextLine();
            toolTip.prefix();
            if(condition.isNegate())
                toolTip.builder.append(ToolTipHelper.DEEP_GREY)
                        .append(new TranslatableComponent(MagickCore.MOD_ID + ".condition.negate").getString())
                        .append(" ")
                        .append(ToolTipHelper.GREY)
                        .append(new TranslatableComponent(MagickCore.MOD_ID + ".condition." + condition.getName()).getString());
            else
                toolTip.builder.append(ToolTipHelper.GREY)
                        .append(new TranslatableComponent(MagickCore.MOD_ID + ".condition." + condition.getName()).getString());
            String post = condition.toString();
            if(!post.isEmpty()) {
                String[] postSplit = post.split("\n");
                toolTip.builder.append(":");
                for (String postString : postSplit) {
                    if(postString.isEmpty()) continue;
                    toolTip.nextLine();
                    toolTip.prefix();
                    toolTip.builder.append(postString);
                }
            }
            toolTip.pop();
        });
        toolTip.nextLine("}");
        return toolTip.getString();
    }
}
