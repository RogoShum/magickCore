package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.lib.LibElementTool;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Random;

@Mixin(ItemStack.class)
public abstract class MixinItemStack{

    @Shadow
    public abstract int getMaxDamage();

    @Shadow
    public abstract int getDamage();

    @Shadow
    public abstract void setDamage(int damage);

    @Shadow
    public abstract CompoundNBT getTag();

    @Inject(method = "attemptDamageItem", at = @At("RETURN"), cancellable = true)
    public void onAttemptDamageItem(int amount, Random rand, @Nullable ServerPlayerEntity damager, CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValueZ() && getTag() != null && getTag().contains(LibElementTool.TOOL_ELEMENT))
        {
            CompoundNBT tag = getTag();
            if(tag.getCompound(LibElementTool.TOOL_ELEMENT).contains(LibElements.WITHER)) {
                CompoundNBT elements = tag.getCompound(LibElementTool.TOOL_ELEMENT);
                int count = elements.getInt(LibElements.WITHER);
                if(count > 1)
                    elements.putInt(LibElements.WITHER, count - amount);
                else
                    elements.remove(LibElements.WITHER);
                setDamage(getMaxDamage() - 1);
                cir.setReturnValue(false);
            }
        }
    }
}
