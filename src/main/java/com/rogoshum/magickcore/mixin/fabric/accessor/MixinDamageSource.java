package com.rogoshum.magickcore.mixin.fabric.accessor;

import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DamageSource.class)
public interface MixinDamageSource {

    @Invoker("bypassArmor")
    DamageSource invokerBypassArmor();

    @Invoker("bypassMagic")
    DamageSource invokerBypassMagic();

    @Invoker("setIsFire")
    DamageSource invokerSetIsFire();

    @Invoker("bypassInvul")
    DamageSource invokerBypassInvul();

    @Invoker("<init>")
    static DamageSource callCreate(String name) {
        throw new AssertionError("Untransformed Accessor!");
    }
}
