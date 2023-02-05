package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.event.living.LivingDamageEvent;
import com.rogoshum.magickcore.api.event.living.LivingDeathEvent;
import com.rogoshum.magickcore.api.event.living.LivingHealEvent;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.util.LootUtil;
import net.minecraft.Util;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
    public MixinLivingEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract float getMaxHealth();

    @Shadow public int deathTime;

    @Shadow public abstract boolean isAlive();

    @ModifyArg(method = "setHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"), index = 0)
    public float onSetHealth(float health) {
        EntityStateData state = ExtraDataUtil.entityStateData(this);
        if(state != null && state.getBuffList().containsKey(LibBuff.CRIPPLE)) {
            float force = state.getBuffList().get(LibBuff.CRIPPLE).getForce();
            float maxHealth = Math.min(this.getMaxHealth() - (this.getMaxHealth() * force * 0.05f), getHealth());
            if(health > maxHealth)
                health = maxHealth;
        }
        return health;
    }

    @ModifyArg(method = "heal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    public float onHeal(float f) {
        LivingHealEvent healEvent = new LivingHealEvent((LivingEntity) (Object)this, f);
        MagickCore.EVENT_BUS.post(healEvent);
        return this.getHealth() + healEvent.getAmount();
    }

    @ModifyVariable(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"), argsOnly = true, index = 2)
    public float onHurt(float value, DamageSource damageSource) {
        LivingDamageEvent damageEvent = new LivingDamageEvent((LivingEntity) (Object)this, damageSource, value);
        MagickCore.EVENT_BUS.post(damageEvent);
        return damageEvent.getAmount();
    }

    @Inject(method = "die", at = @At(value = "HEAD"))
    public void onDie(DamageSource damageSource, CallbackInfo ci) {
        LivingDeathEvent deathEvent = new LivingDeathEvent((LivingEntity) (Object)this, damageSource);
        MagickCore.EVENT_BUS.post(deathEvent);
    }

    @ModifyArg(method = "dropFromLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V"), index = 1)
    public Consumer<ItemStack> onLoot(Consumer<ItemStack> consumer) {
        return itemStack -> {
            List<ItemStack> lootList = new ArrayList<>();
            lootList.add(itemStack);
            LootUtil.modifyLivingLoot((LivingEntity) (Object)this, lootList);
            consumer.accept(lootList.get(0));
        };
    }
}
