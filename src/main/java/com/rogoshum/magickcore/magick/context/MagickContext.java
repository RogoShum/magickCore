package com.rogoshum.magickcore.magick.context;

import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.magick.MagickElement;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.util.HashMap;

public class MagickContext extends SpellContext{
    public final World world;
    public Entity caster, projectile, victim;
    public boolean consumeMana = true;

    public MagickContext(World world) {
        this.world = world;
    }

    public static MagickContext create(World world, SpellContext spellContext) {
        MagickContext context = new MagickContext(world);
        if(spellContext != null)
            context.copy(spellContext);
        return context;
    }

    public MagickContext caster(Entity caster) {
        this.caster = caster;
        return this;
    }

    public MagickContext saveMana() {
        this.consumeMana = false;
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

    public static MagickContext create(World world) {
        return new MagickContext(world);
    }
}
