package com.rogoshum.magickcore.common.integration.psi;

import com.rogoshum.magickcore.api.magick.context.MagickContext;
import net.minecraft.world.entity.Entity;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceOperator;

public class PieceOperatorManaTarget extends PieceOperator {
    SpellParam<com.rogoshum.magickcore.api.magick.context.SpellContext> spell;
    public PieceOperatorManaTarget(Spell spell) {
        super(spell);
    }

    public void initParams() {
        this.addParam(this.spell = new ParamSpell("psi.spellparam.spell", SpellParam.PURPLE, false, false));
    }

    public Object execute(SpellContext context) throws SpellRuntimeException {
        com.rogoshum.magickcore.api.magick.context.SpellContext spellContext = this.getParamValue(context, this.spell);
        Entity e = null;
        if(spellContext instanceof MagickContext)
            e = ((MagickContext) spellContext).victim;
        if (e == null) {
            throw new SpellRuntimeException("psi.spellerror.nulltarget");
        } else {
            return e;
        }
    }

    @Override
    public Class<?> getEvaluationType() {
        return Entity.class;
    }
}
