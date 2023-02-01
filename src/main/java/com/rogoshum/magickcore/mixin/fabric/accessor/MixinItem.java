package com.rogoshum.magickcore.mixin.fabric.accessor;

import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.item.OrbBottleItem;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class MixinItem {

    @Inject(method = "getCraftingRemainingItem", at = @At("RETURN"), cancellable = true)
    public void onGetCraftingRemainingItem(CallbackInfoReturnable<Item> cir) {
        if((Object)this == ModItems.ORB_BOTTLE.get())
            cir.setReturnValue(ModItems.ORB_BOTTLE.get());
    }
}
