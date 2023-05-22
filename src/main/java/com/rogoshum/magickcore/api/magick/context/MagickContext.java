package com.rogoshum.magickcore.api.magick.context;

import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.util.ToolTipHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MagickContext extends SpellContext {
    public final Level world;
    public Entity caster, projectile, victim, separator;
    public InteractionHand hand = InteractionHand.MAIN_HAND;
    public BlockEntity blockCaster;
    public boolean noCost = false;
    public float reduceCost = 0;
    public boolean doBlock = false;

    public MagickContext(Level world) {
        this.world = world;
    }

    public static MagickContext create(Level world, SpellContext spellContext) {
        MagickContext context = new MagickContext(world);
        if(spellContext != null)
            context.copy(spellContext);
        return context;
    }

    public MagickContext caster(Entity caster) {
        this.caster = caster;
        return this;
    }

    public MagickContext hand(InteractionHand hand) {
        this.hand = hand;
        return this;
    }

    public MagickContext caster(BlockEntity caster) {
        this.blockCaster = caster;
        return this;
    }

    public MagickContext separator(Entity separator) {
        this.separator = separator;
        return this;
    }

    public MagickContext noCost() {
        this.noCost = true;
        return this;
    }

    public MagickContext doBlock() {
        this.doBlock = true;
        SpellContext context = this;
        while (context != null) {
            if(context.applyType().isForm())
                context.applyType(ApplyType.NONE);
            context = context.postContext();
        }
        return this;
    }

    public MagickContext projectile(Entity projectile) {
        this.projectile = projectile;
        return this;
    }

    public MagickContext victim(Entity victim) {
        this.victim = victim;
        return this;
    }

    public MagickContext addReduceCost(float cost) {
        reduceCost+=cost;
        return this;
    }

    public static MagickContext create(Level world) {
        return new MagickContext(world);
    }

    @Override
    public String toString() {
        ToolTipHelper toolTip = new ToolTipHelper();
        if(world != null)
            toolTip.nextTrans("World", world, ToolTipHelper.PINK, ToolTipHelper.GREY);
        if(caster != null)
            toolTip.nextTrans("Caster", caster, ToolTipHelper.PINK, ToolTipHelper.GREY);
        if(projectile != null)
            toolTip.nextTrans("Projectile", projectile, ToolTipHelper.PINK, ToolTipHelper.GREY);
        if(victim != null)
            toolTip.nextTrans("Victim", victim, ToolTipHelper.PINK, ToolTipHelper.GREY);
        if(noCost)
            toolTip.nextTrans("noCost", ToolTipHelper.PINK);

        toolTip.builder.append(getString(false, this.element()));
        return super.toString();
    }
}
