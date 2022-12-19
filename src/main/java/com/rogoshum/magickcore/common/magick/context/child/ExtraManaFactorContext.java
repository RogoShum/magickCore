package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;

public class ExtraManaFactorContext extends ChildContext{
    public ManaFactor manaFactor = ManaFactor.DEFAULT;

    public static ExtraManaFactorContext create(ManaFactor manaFactor) {
        ExtraManaFactorContext extraApplyTypeContext = new ExtraManaFactorContext();
        extraApplyTypeContext.manaFactor = manaFactor;
        return extraApplyTypeContext;
    }

    @Override
    public void serialize(CompoundNBT tag) {
        tag.putFloat("mana_factor_force", manaFactor.force);
        tag.putFloat("mana_factor_range", manaFactor.range);
        tag.putFloat("mana_factor_tick", manaFactor.tick);
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        manaFactor = ManaFactor.create(tag.getFloat("mana_factor_force"), tag.getFloat("mana_factor_range"), tag.getFloat("mana_factor_tick"));
    }

    @Override
    public boolean valid() {
        return manaFactor != null;
    }

    @Override
    public String getName() {
        return LibContext.MANA_FACTOR;
    }

    @Override
    public String getString(int tab) {
        return "";
    }
}
