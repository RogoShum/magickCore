package com.rogoshum.magickcore.common.integration.psi;

import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import net.minecraft.world.entity.Entity;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrickManaOut extends PieceTrick {
    SpellParam<com.rogoshum.magickcore.common.magick.context.SpellContext> spell;
    SpellParam<Entity> target;
    SpellParam<Vector3> pos;
    SpellParam<Vector3> dir;
    public PieceTrickManaOut(Spell spell) {
        super(spell);
        this.setStatLabel(EnumSpellStat.COST, (new StatLabel("psi.spellparam.spell", true)).add(200));
        this.setStatLabel(EnumSpellStat.COST, (new StatLabel("psi.spellparam.target", true)).add(100));
    }

    public void initParams() {
        this.addParam(this.target = new ParamEntity("psi.spellparam.target", SpellParam.YELLOW, true, false));
        this.addParam(this.spell = new ParamSpell("psi.spellparam.spell", SpellParam.PURPLE, false, false));
        this.addParam(this.pos = new ParamVector("psi.spellparam.position", SpellParam.BLUE, false, false));
        this.addParam(this.dir = new ParamVector("psi.spellparam.direction", SpellParam.GREEN, false, false));
    }

    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
        super.addToMetadata(meta);
        com.rogoshum.magickcore.common.magick.context.SpellContext context = this.getParamEvaluation(this.spell);
        if (context != null) {
            meta.addStat(EnumSpellStat.COST, 200);
        }

        Entity target = this.getParamEvaluation(this.target);
        if (target != null) {
            meta.addStat(EnumSpellStat.COST, 100);
        }
    }

    public Object execute(SpellContext context) throws SpellRuntimeException {
        Vector3 pos = this.getParamValue(context, this.pos);
        Vector3 dir = this.getParamValue(context, this.dir);
        if (pos != null && dir != null) {
            Entity e = this.getParamValue(context, this.target);
            if(context.caster != null) {
                com.rogoshum.magickcore.common.magick.context.SpellContext spell = this.getParamValue(context, this.spell);
                if(spell.postContext != null) {
                    MagickContext mc = MagickContext.create(context.caster.getLevel(), spell.postContext);
                    mc.caster(context.caster).victim(e).noCost().addChild(PositionContext.create(pos.toVec3D())).addChild(DirectionContext.create(pos.toVec3D()));
                    if(e == null)
                        mc.doBlock();
                    MagickReleaseHelper.releaseMagick(mc);
                } else
                    throw new SpellRuntimeException("psi.spellerror.nullspell");
            } else
                throw new SpellRuntimeException("psi.spellerror.nulltarget");
        } else {
            throw new SpellRuntimeException("psi.spellerror.nullvector");
        }
        return null;
    }

    @Override
    public Class<?> getEvaluationType() {
        return Void.class;
    }
}
