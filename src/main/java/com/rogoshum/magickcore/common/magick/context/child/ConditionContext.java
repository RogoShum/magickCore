package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.TargetType;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.condition.Condition;
import com.rogoshum.magickcore.common.util.ToolTipHelper;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;

import java.util.Arrays;
import java.util.HashSet;

public class ConditionContext extends ChildContext{
    public HashSet<Condition> conditions = new HashSet<>();

    public static ConditionContext create(Condition... condition) {
        ConditionContext context = new ConditionContext();
        context.conditions = new HashSet<>(Arrays.asList(condition));
        return context;
    }

    public static ConditionContext create() {
        return new ConditionContext();
    }

    @Override
    public void serialize(CompoundNBT tag) {
        if(conditions.isEmpty()) return;
        CompoundNBT compoundNBT = new CompoundNBT();
        conditions.forEach((condition) -> {
            CompoundNBT conditionTag = new CompoundNBT();
            condition.write(conditionTag);
            compoundNBT.put(condition.getName(), conditionTag);
        });
        tag.put("Conditions", compoundNBT);
    }

    public void addCondition(Condition... conditions) {
        this.conditions.addAll(Arrays.asList(conditions));
    }

    public boolean test(Entity self, Entity target) {
        for (Condition condition : conditions) {
            Entity entity = self;
            if(condition.getType().equals(TargetType.TARGET))
                entity = target;
            if(!(!condition.isNegate() && condition.test(entity)) && !(condition.isNegate() && !condition.test(entity)))
                return false;
        }
        return true;
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        if(!tag.contains("Conditions")) return;
        CompoundNBT compoundNBT = tag.getCompound("Conditions");
        compoundNBT.keySet().forEach((key) -> {
            Condition condition = MagickRegistry.getCondition(key);
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
    public String getName() {
        return LibContext.CONDITION;
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
            toolTip.nextTrans(MagickCore.MOD_ID + ".condition." + condition.getName(), ToolTipHelper.GREY);
            toolTip.pop();
        });
        toolTip.nextLine("}");
        return toolTip.getString();
    }
}
