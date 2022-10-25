package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;

public class ModDamage {
    private static final DamageSource arc = new DamageSource(MagickCore.MOD_ID + "-arc").setDamageBypassesArmor().setMagicDamage().setDamageIsAbsolute();
    private static final DamageSource solar = new DamageSource(MagickCore.MOD_ID + "-solar").setDamageBypassesArmor().setMagicDamage().setFireDamage();
    private static final DamageSource voidDamage = new DamageSource(MagickCore.MOD_ID + "-void").setDamageBypassesArmor().setMagicDamage().setDamageAllowedInCreativeMode();

    private static final DamageSource stasis = new DamageSource(MagickCore.MOD_ID + "-stasis").setDamageBypassesArmor().setMagicDamage();
    private static final DamageSource wither = new DamageSource(MagickCore.MOD_ID + "-wither").setDamageBypassesArmor().setMagicDamage();
    private static final DamageSource taken = new DamageSource(MagickCore.MOD_ID + "-taken").setDamageBypassesArmor().setMagicDamage();
    private static final DamageSource air = new DamageSource(MagickCore.MOD_ID + "-air").setDamageBypassesArmor().setMagicDamage();

    public static DamageSource getArcDamage() { return arc; }
    public static DamageSource applyEntityArcDamage(Entity entity) { return new EntityDamageSource(arc.getDamageType(), entity).setDamageBypassesArmor().setMagicDamage().setDamageIsAbsolute(); }
    public static DamageSource applyProjectileArcDamage(Entity entity, Entity projectile) { return new IndirectEntityDamageSource(arc.getDamageType(), projectile, entity).setDamageBypassesArmor().setMagicDamage().setDamageIsAbsolute(); }

    public static DamageSource getSolarDamage() { return solar; }
    public static DamageSource applyEntitySolarDamage(Entity entity) { return new EntityDamageSource(solar.getDamageType(), entity).setDamageBypassesArmor().setMagicDamage().setFireDamage(); }
    public static DamageSource applyProjectileSolarDamage(Entity entity, Entity projectile) { return new IndirectEntityDamageSource(solar.getDamageType(), projectile, entity).setDamageBypassesArmor().setMagicDamage().setFireDamage(); }

    public static DamageSource getVoidDamage() { return voidDamage; }
    public static DamageSource applyEntityVoidDamage(Entity entity) { return new EntityDamageSource(voidDamage.getDamageType(), entity).setDamageBypassesArmor().setMagicDamage().setDamageAllowedInCreativeMode(); }
    public static DamageSource applyProjectileVoidDamage(Entity entity, Entity projectile) { return new IndirectEntityDamageSource(voidDamage.getDamageType(), projectile, entity).setDamageBypassesArmor().setMagicDamage().setDamageAllowedInCreativeMode(); }

    public static DamageSource getStasisDamage() { return stasis; }
    public static DamageSource applyEntityStasisDamage(Entity entity) { return new EntityDamageSource(stasis.getDamageType(), entity).setDamageBypassesArmor().setMagicDamage(); }
    public static DamageSource applyProjectileStasisDamage(Entity entity, Entity projectile) { return new IndirectEntityDamageSource(stasis.getDamageType(), projectile, entity).setDamageBypassesArmor().setMagicDamage(); }

    public static DamageSource getWitherDamage() { return wither; }
    public static DamageSource applyEntityWitherDamage(Entity entity) { return new EntityDamageSource(wither.getDamageType(), entity).setDamageBypassesArmor().setMagicDamage(); }
    public static DamageSource applyProjectileWitherDamage(Entity entity, Entity projectile) { return new IndirectEntityDamageSource(wither.getDamageType(), projectile, entity).setDamageBypassesArmor().setMagicDamage(); }

    public static DamageSource getTakenDamage() { return taken; }
    public static DamageSource applyEntityTakenDamage(Entity entity) { return new EntityDamageSource(taken.getDamageType(), entity).setDamageBypassesArmor().setMagicDamage(); }
    public static DamageSource applyProjectileTakenDamage(Entity entity, Entity projectile) { return new IndirectEntityDamageSource(taken.getDamageType(), projectile, entity).setDamageBypassesArmor().setMagicDamage(); }

    public static DamageSource getAirDamage() { return air; }
    public static DamageSource applyEntityAirDamage(Entity entity) { return new EntityDamageSource(air.getDamageType(), entity).setDamageBypassesArmor().setMagicDamage(); }
    public static DamageSource applyProjectileAirDamage(Entity entity, Entity projectile) { return new IndirectEntityDamageSource(air.getDamageType(), projectile, entity).setDamageBypassesArmor().setMagicDamage(); }
}
