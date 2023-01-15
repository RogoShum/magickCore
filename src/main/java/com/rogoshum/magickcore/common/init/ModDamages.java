package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;

public class ModDamages {
    private static final DamageSource arc = new DamageSource(MagickCore.MOD_ID + "-arc").bypassArmor().setMagic().bypassMagic();
    private static final DamageSource solar = new DamageSource(MagickCore.MOD_ID + "-solar").bypassArmor().setMagic().setIsFire();
    private static final DamageSource voidDamage = new DamageSource(MagickCore.MOD_ID + "-void").bypassArmor().setMagic().bypassInvul();

    private static final DamageSource stasis = new DamageSource(MagickCore.MOD_ID + "-stasis").bypassArmor().setMagic();
    private static final DamageSource wither = new DamageSource(MagickCore.MOD_ID + "-wither").bypassArmor().setMagic();
    private static final DamageSource taken = new DamageSource(MagickCore.MOD_ID + "-taken").bypassArmor().setMagic();
    private static final DamageSource air = new DamageSource(MagickCore.MOD_ID + "-air").bypassArmor().setMagic();

    public static DamageSource getArcDamage() { return arc; }
    public static DamageSource applyEntityArcDamage(Entity entity) { return new EntityDamageSource(arc.getMsgId(), entity).bypassArmor().setMagic().bypassMagic(); }
    public static DamageSource applyProjectileArcDamage(Entity entity, Entity projectile) {
        if(projectile instanceof Projectile)
            return new IndirectEntityDamageSource(arc.getMsgId(), projectile, entity).setProjectile().bypassArmor().setMagic().bypassMagic();
        else
            return new IndirectEntityDamageSource(arc.getMsgId(), projectile, entity).bypassArmor().setMagic().bypassMagic();
    }

    public static DamageSource getSolarDamage() { return solar; }
    public static DamageSource applyEntitySolarDamage(Entity entity) { return new EntityDamageSource(solar.getMsgId(), entity).bypassArmor().setMagic().setIsFire(); }
    public static DamageSource applyProjectileSolarDamage(Entity entity, Entity projectile) {
        if(projectile instanceof Projectile)
            return new IndirectEntityDamageSource(solar.getMsgId(), projectile, entity).setProjectile().bypassArmor().setMagic().setIsFire();
        else
            return new IndirectEntityDamageSource(solar.getMsgId(), projectile, entity).bypassArmor().setMagic().setIsFire();
    }

    public static DamageSource getVoidDamage() { return voidDamage; }
    public static DamageSource applyEntityVoidDamage(Entity entity) { return new EntityDamageSource(voidDamage.getMsgId(), entity).bypassArmor().setMagic().bypassInvul(); }
    public static DamageSource applyProjectileVoidDamage(Entity entity, Entity projectile) {
        if(projectile instanceof Projectile)
            return new IndirectEntityDamageSource(voidDamage.getMsgId(), projectile, entity).setProjectile().bypassArmor().setMagic().bypassInvul();
        else
            return new IndirectEntityDamageSource(voidDamage.getMsgId(), projectile, entity).bypassArmor().setMagic().bypassInvul();
    }

    public static DamageSource getStasisDamage() { return stasis; }
    public static DamageSource applyEntityStasisDamage(Entity entity) { return new EntityDamageSource(stasis.getMsgId(), entity).bypassArmor().setMagic(); }
    public static DamageSource applyProjectileStasisDamage(Entity entity, Entity projectile) {
        if(projectile instanceof Projectile)
            return new IndirectEntityDamageSource(stasis.getMsgId(), projectile, entity).setProjectile().bypassArmor().setMagic();
        else
            return new IndirectEntityDamageSource(stasis.getMsgId(), projectile, entity).bypassArmor().setMagic();
    }

    public static DamageSource getWitherDamage() { return wither; }
    public static DamageSource applyEntityWitherDamage(Entity entity) { return new EntityDamageSource(wither.getMsgId(), entity).bypassArmor().setMagic(); }
    public static DamageSource applyProjectileWitherDamage(Entity entity, Entity projectile) {
        if(projectile instanceof Projectile)
            return new IndirectEntityDamageSource(wither.getMsgId(), projectile, entity).setProjectile().bypassArmor().setMagic();
        else
            return new IndirectEntityDamageSource(wither.getMsgId(), projectile, entity).bypassArmor().setMagic();
    }

    public static DamageSource getTakenDamage() { return taken; }
    public static DamageSource applyEntityTakenDamage(Entity entity) { return new EntityDamageSource(taken.getMsgId(), entity).bypassArmor().setMagic(); }
    public static DamageSource applyProjectileTakenDamage(Entity entity, Entity projectile) {
        if(projectile instanceof Projectile)
            return new IndirectEntityDamageSource(taken.getMsgId(), projectile, entity).setProjectile().bypassArmor().setMagic();
        else
            return new IndirectEntityDamageSource(taken.getMsgId(), projectile, entity).bypassArmor().setMagic();
    }

    public static DamageSource getAirDamage() { return air; }
    public static DamageSource applyEntityAirDamage(Entity entity) { return new EntityDamageSource(air.getMsgId(), entity).bypassArmor().setMagic(); }
    public static DamageSource applyProjectileAirDamage(Entity entity, Entity projectile) {
        if(projectile instanceof Projectile)
            return new IndirectEntityDamageSource(air.getMsgId(), projectile, entity).setProjectile().bypassArmor().setMagic();
        else
            return new IndirectEntityDamageSource(air.getMsgId(), projectile, entity).bypassArmor().setMagic();
    }
}
