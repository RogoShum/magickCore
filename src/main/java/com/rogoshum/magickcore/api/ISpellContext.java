package com.rogoshum.magickcore.api;

import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.enums.EnumTargetType;
import com.rogoshum.magickcore.magick.MagickElement;
import com.rogoshum.magickcore.magick.ManaData;
import com.rogoshum.magickcore.magick.context.SpellContext;

import javax.annotation.Nullable;
import java.util.UUID;

public interface ISpellContext {
    SpellContext spellContext();
}
