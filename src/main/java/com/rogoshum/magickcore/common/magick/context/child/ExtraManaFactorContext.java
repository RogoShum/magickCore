package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import net.minecraft.nbt.CompoundTag;

public class ExtraManaFactorContext extends ChildContext{
    public static final Type<ExtraManaFactorContext> TYPE = new Type<>(LibContext.MANA_FACTOR);
    public ManaFactor manaFactor = ManaFactor.DEFAULT;

    public static ExtraManaFactorContext create(ManaFactor manaFactor) {
        ExtraManaFactorContext extraApplyTypeContext = new ExtraManaFactorContext();
        extraApplyTypeContext.manaFactor = manaFactor;
        return extraApplyTypeContext;
    }

    @Override
    public void serialize(CompoundTag tag) {
        tag.putFloat("mana_factor_force", manaFactor.force);
        tag.putFloat("mana_factor_range", manaFactor.range);
        tag.putFloat("mana_factor_tick", manaFactor.tick);
    }

    @Override
    public void deserialize(CompoundTag tag) {
        manaFactor = ManaFactor.create(tag.getFloat("mana_factor_force"), tag.getFloat("mana_factor_range"), tag.getFloat("mana_factor_tick"));
    }

    @Override
    public boolean valid() {
        return manaFactor != null;
    }

    @Override
    public Type<ExtraManaFactorContext> getType() {
        return TYPE;
    }

    @Override
    public String getString(int tab) {
        return "";
    }
}
