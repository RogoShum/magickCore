package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.capability.ITakenState;
import com.rogoshum.magickcore.helper.NBTTagHelper;
import com.rogoshum.magickcore.helper.TakenTargetHelper;
import com.rogoshum.magickcore.lib.LibElementTool;
import com.rogoshum.magickcore.lib.LibElements;
import net.minecraft.block.Block;
import net.minecraft.block.FireBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

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
