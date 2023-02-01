package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.mixin.fabric.accessor.MixinDamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;

public class ModDamages {
    private static final DamageSource arc = ((MixinDamageSource)((MixinDamageSource) MixinDamageSource.callCreate(MagickCore.MOD_ID + "-arc")).invokerBypassArmor().setMagic()).invokerBypassMagic();
    private static final DamageSource solar = ((MixinDamageSource)((MixinDamageSource) MixinDamageSource.callCreate(MagickCore.MOD_ID + "-solar")).invokerBypassArmor().setMagic()).invokerSetIsFire();
    private static final DamageSource voidDamage = ((MixinDamageSource)((MixinDamageSource) MixinDamageSource.callCreate(MagickCore.MOD_ID + "-void")).invokerBypassArmor().setMagic()).invokerBypassInvul();

    private static final DamageSource stasis = ((MixinDamageSource) MixinDamageSource.callCreate(MagickCore.MOD_ID + "-stasis")).invokerBypassArmor().setMagic();
    private static final DamageSource wither = ((MixinDamageSource) MixinDamageSource.callCreate(MagickCore.MOD_ID + "-wither")).invokerBypassArmor().setMagic();
    private static final DamageSource taken = ((MixinDamageSource) MixinDamageSource.callCreate(MagickCore.MOD_ID + "-taken")).invokerBypassArmor().setMagic();
    private static final DamageSource air = ((MixinDamageSource) MixinDamageSource.callCreate(MagickCore.MOD_ID + "-air")).invokerBypassArmor().setMagic();

    public static DamageSource getArcDamage() { return arc; }
    public static DamageSource applyEntityArcDamage(Entity entity) { return ((MixinDamageSource)((MixinDamageSource)(new EntityDamageSource(arc.getMsgId(), entity))).invokerBypassArmor().setMagic()).invokerBypassMagic(); }
    public static DamageSource applyProjectileArcDamage(Entity entity, Entity projectile) {
        if(projectile instanceof Projectile)
            return ((MixinDamageSource)((MixinDamageSource)new IndirectEntityDamageSource(arc.getMsgId(), projectile, entity).setProjectile()).invokerBypassArmor().setMagic()).invokerBypassMagic();
        else
            return ((MixinDamageSource)((MixinDamageSource)new IndirectEntityDamageSource(arc.getMsgId(), projectile, entity)).invokerBypassArmor().setMagic()).invokerBypassMagic();
    }

    public static DamageSource getSolarDamage() { return solar; }
    public static DamageSource applyEntitySolarDamage(Entity entity) { return ((MixinDamageSource)((MixinDamageSource)new EntityDamageSource(solar.getMsgId(), entity)).invokerBypassArmor().setMagic()).invokerSetIsFire(); }
    public static DamageSource applyProjectileSolarDamage(Entity entity, Entity projectile) {
        if(projectile instanceof Projectile)
            return ((MixinDamageSource)((MixinDamageSource)new IndirectEntityDamageSource(solar.getMsgId(), projectile, entity).setProjectile()).invokerBypassArmor().setMagic()).invokerSetIsFire();
        else
            return ((MixinDamageSource)((MixinDamageSource)new IndirectEntityDamageSource(solar.getMsgId(), projectile, entity)).invokerBypassArmor().setMagic()).invokerSetIsFire();
    }

    public static DamageSource getVoidDamage() { return voidDamage; }
    public static DamageSource applyEntityVoidDamage(Entity entity) { return ((MixinDamageSource)((MixinDamageSource)new EntityDamageSource(voidDamage.getMsgId(), entity)).invokerBypassArmor().setMagic()).invokerBypassInvul(); }
    public static DamageSource applyProjectileVoidDamage(Entity entity, Entity projectile) {
        if(projectile instanceof Projectile)
            return ((MixinDamageSource)((MixinDamageSource)new IndirectEntityDamageSource(voidDamage.getMsgId(), projectile, entity).setProjectile()).invokerBypassArmor().setMagic()).invokerBypassInvul();
        else
            return ((MixinDamageSource)((MixinDamageSource)new IndirectEntityDamageSource(voidDamage.getMsgId(), projectile, entity)).invokerBypassArmor().setMagic()).invokerBypassInvul();
    }

    public static DamageSource getStasisDamage() { return stasis; }
    public static DamageSource applyEntityStasisDamage(Entity entity) { return ((MixinDamageSource)new EntityDamageSource(stasis.getMsgId(), entity)).invokerBypassArmor().setMagic(); }
    public static DamageSource applyProjectileStasisDamage(Entity entity, Entity projectile) {
        if(projectile instanceof Projectile)
            return ((MixinDamageSource)new IndirectEntityDamageSource(stasis.getMsgId(), projectile, entity).setProjectile()).invokerBypassArmor().setMagic();
        else
            return ((MixinDamageSource)new IndirectEntityDamageSource(stasis.getMsgId(), projectile, entity)).invokerBypassArmor().setMagic();
    }

    public static DamageSource getWitherDamage() { return wither; }
    public static DamageSource applyEntityWitherDamage(Entity entity) { return ((MixinDamageSource)new EntityDamageSource(wither.getMsgId(), entity)).invokerBypassArmor().setMagic(); }
    public static DamageSource applyProjectileWitherDamage(Entity entity, Entity projectile) {
        if(projectile instanceof Projectile)
            return ((MixinDamageSource)new IndirectEntityDamageSource(wither.getMsgId(), projectile, entity).setProjectile()).invokerBypassArmor().setMagic();
        else
            return ((MixinDamageSource)new IndirectEntityDamageSource(wither.getMsgId(), projectile, entity)).invokerBypassArmor().setMagic();
    }

    public static DamageSource getTakenDamage() { return taken; }
    public static DamageSource applyEntityTakenDamage(Entity entity) { return ((MixinDamageSource)new EntityDamageSource(taken.getMsgId(), entity)).invokerBypassArmor().setMagic(); }
    public static DamageSource applyProjectileTakenDamage(Entity entity, Entity projectile) {
        if(projectile instanceof Projectile)
            return ((MixinDamageSource)new IndirectEntityDamageSource(taken.getMsgId(), projectile, entity).setProjectile()).invokerBypassArmor().setMagic();
        else
            return ((MixinDamageSource)new IndirectEntityDamageSource(taken.getMsgId(), projectile, entity)).invokerBypassArmor().setMagic();
    }

    public static DamageSource getAirDamage() { return air; }
    public static DamageSource applyEntityAirDamage(Entity entity) { return ((MixinDamageSource)new EntityDamageSource(air.getMsgId(), entity)).invokerBypassArmor().setMagic(); }
    public static DamageSource applyProjectileAirDamage(Entity entity, Entity projectile) {
        if(projectile instanceof Projectile)
            return ((MixinDamageSource)new IndirectEntityDamageSource(air.getMsgId(), projectile, entity).setProjectile()).invokerBypassArmor().setMagic();
        else
            return ((MixinDamageSource)new IndirectEntityDamageSource(air.getMsgId(), projectile, entity)).invokerBypassArmor().setMagic();
    }
}
