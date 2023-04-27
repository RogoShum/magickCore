package com.rogoshum.magickcore.common.integration.psi;

import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorManaIn extends PieceOperator {
    public PieceOperatorManaIn(Spell spell) {
        super(spell);
    }

    public Object execute(SpellContext context) throws SpellRuntimeException {
        if (context.customData.containsKey("magick_context")) {
            return context.customData.get("magick_context");
        } else {
            throw new SpellRuntimeException("psi.spellerror.nullspell");
        }
    }

    @Override
    public Class<?> getEvaluationType() {
        return com.rogoshum.magickcore.api.magick.context.SpellContext.class;
    }
}
