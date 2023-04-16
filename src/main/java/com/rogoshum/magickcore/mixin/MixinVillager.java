package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.common.init.ModVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)

public abstract class MixinVillager {

    @Inject(method = "updateSpecialPrices", at = @At("HEAD"))
    public void onRead(Player p_35541_, CallbackInfo ci) {
        Villager me = (Villager)(Object)this;
        if(me.getVillagerData().getProfession() == ModVillager.MAGE.get()) {
            MerchantOffers merchantOffers = new MerchantOffers();
            merchantOffers.addAll(ModVillager.getPlayerTrades(p_35541_));
            me.setOffers(merchantOffers);
        }
    }
}
