package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.init.ModElements;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.lib.LibBuff;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Sheep.class)
public abstract class MixinSheep extends Entity {
    public MixinSheep(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }


    @Inject(method = "onSheared", at = @At("RETURN"), remap = false)
    public void onSetHealth(Player player, ItemStack item, Level world, BlockPos pos, int fortune, CallbackInfoReturnable<List<ItemStack>> cir) {
        EntityStateData state = ExtraDataUtil.entityStateData(this);
        if(state != null && state.getElement() != ModElements.ORIGIN) {
            List<ItemStack> element = new ArrayList<>();
            cir.getReturnValue().removeIf((e) -> {
                if (e.getItem().getRegistryName().toString().contains("wool")) {
                    int count = e.getCount();
                    ItemStack stack = new ItemStack(ModItems.ELEMENT_WOOL.get());
                    CompoundTag tag = new CompoundTag();
                    tag.putString("ELEMENT", state.getElement().type());
                    stack.setCount(count);
                    stack.setTag(tag);
                    element.add(stack);
                    return true;
                }

                if (e.getItem().getRegistryName().toString().contains("string")) {
                    int count = e.getCount();
                    ItemStack stack = new ItemStack(ModItems.ELEMENT_STRING.get());
                    CompoundTag tag = new CompoundTag();
                    tag.putString("ELEMENT", state.getElement().type());
                    stack.setCount(count);
                    stack.setTag(tag);
                    element.add(stack);
                    return true;
                }
                return false;
            });
            cir.getReturnValue().addAll(element);
        }
    }
}
