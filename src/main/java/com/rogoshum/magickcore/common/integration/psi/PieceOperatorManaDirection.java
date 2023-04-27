package com.rogoshum.magickcore.common.integration.psi;

import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.context.child.DirectionContext;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorManaDirection extends PieceOperator {
    SpellParam<com.rogoshum.magickcore.api.magick.context.SpellContext> spell;
    public PieceOperatorManaDirection(Spell spell) {
        super(spell);
    }

    public void initParams() {
        this.addParam(this.spell = new ParamSpell("psi.spellparam.spell", SpellParam.PURPLE, false, false));
    }

    public Object execute(SpellContext context) throws SpellRuntimeException {
        com.rogoshum.magickcore.api.magick.context.SpellContext spellContext = this.getParamValue(context, this.spell);
        if (!spellContext.containChild(LibContext.DIRECTION)) {
            throw new SpellRuntimeException("psi.spellerror.nullvector");
        } else {
            return Vector3.fromVec3d(spellContext.<DirectionContext>getChild(LibContext.DIRECTION).direction);
        }
    }

    @Override
    public Class<?> getEvaluationType() {
        return Vector3.class;
    }
}
