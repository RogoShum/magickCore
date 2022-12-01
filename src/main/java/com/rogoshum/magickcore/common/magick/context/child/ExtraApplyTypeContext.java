package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;

public class ExtraApplyTypeContext extends ChildContext{
    public ApplyType applyType = ApplyType.NONE;

    public static ExtraApplyTypeContext create(ApplyType applyType) {
        ExtraApplyTypeContext extraApplyTypeContext = new ExtraApplyTypeContext();
        extraApplyTypeContext.applyType = applyType;
        return extraApplyTypeContext;
    }

    @Override
    public void serialize(CompoundNBT tag) {
        tag.putString("apply_type", applyType.getLabel());
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        applyType = ApplyType.getEnum(tag.getString("apply_type"));
    }

    @Override
    public boolean valid() {
        return applyType != null;
    }

    @Override
    public String getName() {
        return LibContext.APPLY_TYPE;
    }

    @Override
    public String getString(int tab) {
        return new TranslationTextComponent(MagickCore.MOD_ID + ".context." + applyType).getString();
    }
}
