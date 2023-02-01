package com.rogoshum.magickcore.mixin.fabric;

import com.rogoshum.magickcore.api.mixin.IItemUpdate;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity {
    @Shadow public abstract ItemStack getItem();

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"), cancellable = true)
    public void onTick(CallbackInfo ci) {
        if(getItem().getItem() instanceof IItemUpdate) {
            if(((IItemUpdate)getItem().getItem()).onEntityItemUpdate(getItem(), (ItemEntity)(Object)this)) {
                ci.cancel();
            }
        }
    }
}
