package com.rogoshum.magickcore.common.integration.psi;

import com.rogoshum.magickcore.api.magick.context.SpellContext;
import vazkii.psi.api.spell.param.ParamSpecific;

public class ParamSpell extends ParamSpecific<SpellContext> {
    public ParamSpell(String name, int color, boolean canDisable, boolean constant) {
        super(name, color, canDisable, constant);
    }

    public Class<SpellContext> getRequiredType() {
        return SpellContext.class;
    }
}
