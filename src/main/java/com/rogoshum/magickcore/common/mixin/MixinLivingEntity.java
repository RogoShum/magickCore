package com.rogoshum.magickcore.common.mixin;

import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.lib.LibBuff;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
    public MixinLivingEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract float getMaxHealth();

    @Inject(method = "setHealth", at = @At("HEAD"))
    public void onSetHealth(float health, CallbackInfo info) {
        EntityStateData state = ExtraDataUtil.entityStateData(this);
        if(state != null && state.getBuffList().containsKey(LibBuff.CRIPPLE)) {
            float force = state.getBuffList().get(LibBuff.CRIPPLE).getForce();
            float maxHealth = Math.min(this.getMaxHealth() - (this.getMaxHealth() * force * 0.05f), getHealth());
            if(health > maxHealth)
                health = maxHealth;
        }
    }
}
