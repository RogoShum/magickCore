package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.common.init.ModVillager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)

public abstract class MixinVillager {

    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;getOffers()Lnet/minecraft/world/item/trading/MerchantOffers;"))
    public void onRead(Player p_35472_, InteractionHand p_35473_, CallbackInfoReturnable<InteractionResult> cir) {
        Villager me = (Villager)(Object)this;
        if(me.getVillagerData().getProfession() == ModVillager.MAGE.get()) {
            MerchantOffers merchantOffers = new MerchantOffers();
            merchantOffers.addAll(ModVillager.getPlayerTrades(p_35472_));
            me.setOffers(merchantOffers);
        }
    }
}
