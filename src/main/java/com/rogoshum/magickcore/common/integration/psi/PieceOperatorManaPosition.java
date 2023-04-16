package com.rogoshum.magickcore.common.integration.psi;

import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorManaPosition extends PieceOperator {
    SpellParam<com.rogoshum.magickcore.common.magick.context.SpellContext> spell;
    public PieceOperatorManaPosition(Spell spell) {
        super(spell);
    }

    public void initParams() {
        this.addParam(this.spell = new ParamSpell("psi.spellparam.spell", SpellParam.PURPLE, false, false));
    }

    public Object execute(SpellContext context) throws SpellRuntimeException {
        com.rogoshum.magickcore.common.magick.context.SpellContext spellContext = this.getParamValue(context, this.spell);
        if (!spellContext.containChild(LibContext.POSITION)) {
            throw new SpellRuntimeException("psi.spellerror.nullvector");
        } else {
            return Vector3.fromVec3d(spellContext.<PositionContext>getChild(LibContext.POSITION).pos);
        }
    }

    @Override
    public Class<?> getEvaluationType() {
        return Vector3.class;
    }
}
