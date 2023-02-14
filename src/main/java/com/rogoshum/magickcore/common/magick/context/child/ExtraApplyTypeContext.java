package com.rogoshum.magickcore.common.magick.context.child;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.lib.LibContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;

public class ExtraApplyTypeContext extends ChildContext{
    public ApplyType applyType = ApplyType.NONE;

    public static ExtraApplyTypeContext create(ApplyType applyType) {
        ExtraApplyTypeContext extraApplyTypeContext = new ExtraApplyTypeContext();
        extraApplyTypeContext.applyType = applyType;
        return extraApplyTypeContext;
    }

    @Override
    public void serialize(CompoundTag tag) {
        tag.putString("apply_type", applyType.getLabel());
    }

    @Override
    public void deserialize(CompoundTag tag) {
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
        return new TranslatableComponent(MagickCore.MOD_ID + ".context." + applyType).getString();
    }
}
