package com.rogoshum.magickcore.common.mixin;

import com.rogoshum.magickcore.common.lib.LibElementTool;
import com.rogoshum.magickcore.common.lib.LibElements;
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
public class MixinItemStack{
    //private final HashMap<String, ItemExtraData> extraData = new HashMap<>();

    @Shadow
    public int getMaxDamage() { return 0; }

    @Shadow
    public int getDamage() {return 0;}

    @Shadow
    public void setDamage(int damage) {}

    @Shadow
    public CompoundNBT getTag() { return new CompoundNBT(); }

    /*
    @Override
    public HashMap<String, ItemExtraData> extraData() {
        return extraData;
    }

    /*
    @Inject(method = "getTag", at = @At("HEAD"))
    public void onGetTag(CallbackInfoReturnable<CompoundNBT> cir) {
        if(extraData().isEmpty()) return;
        saveItemData();
    }

    @Inject(method = "hasTag", at = @At("HEAD"))
    public void onHasTag(CallbackInfoReturnable<Boolean> cir) {
        if(extraData().isEmpty()) return;
        saveItemData();
    }

    @Inject(method = "isItemStackEqual", at = @At("HEAD"))
    public void onEqual(ItemStack other, CallbackInfoReturnable<Boolean> cir) {
        if(extraData().isEmpty()) return;
        saveItemData();
    }

    @Inject(method = "areItemStackTagsEqual", at = @At("HEAD"))
    private static void onTagsEqual(ItemStack stackA, ItemStack stackB, CallbackInfoReturnable<Boolean> cir) {
        IItemData dataA = ExtraDataUtil.itemData(stackA);
        if(!dataA.extraData().isEmpty())
            saveItemData(stackA);

        IItemData dataB = ExtraDataUtil.itemData(stackB);
        if(!dataB.extraData().isEmpty())
            saveItemData(stackA);
    }

    public CompoundNBT originTag() {
        ItemStack thisStack = (ItemStack)(Object)this;
        return ObfuscationReflectionHelper.getPrivateValue(ItemStack.class, thisStack, "field_77990_d");
    }

    @Shadow
    public void setTag(@Nullable CompoundNBT nbt) { }

    /*
    @Inject(method = "copy", at = @At("RETURN"))
    public void onCopy(CallbackInfoReturnable<ItemStack> stack) {
        if(stack.getReturnValue().isEmpty()) return;
        copyItemData(stack.getReturnValue());
    }

    @Inject(method = "write", at = @At("HEAD"))
    public void onWrite(CompoundNBT nbt, CallbackInfoReturnable<CompoundNBT> tag) {
        saveItemData();
    }

    @Inject(method = "setTag", at = @At("RETURN"))
    public void onSetTag(@Nullable CompoundNBT nbt, CallbackInfo info) {
        if(nbt != null && nbt.contains(ItemExtraData.ITEM_DATA)) {
            ItemStack thisStack = (ItemStack)(Object)this;
            CompoundNBT tag = nbt.getCompound(ItemExtraData.ITEM_DATA);
            tag.keySet().forEach((key) -> {
                ExtraDataUtil.itemData(thisStack).<ItemManaData>execute(key, data -> data.read(tag.getCompound(key)));
            });
        }
    }

     */

    /*
    @Inject(method = "<init>(Lnet/minecraft/util/IItemProvider;ILnet/minecraft/nbt/CompoundNBT;)V", at = @At("RETURN"), cancellable = true)
    protected void onConstructor(CallbackInfo info) {
        initItemData();
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundNBT;)V", at = @At("RETURN"), cancellable = true)
    protected void onConstructorCompoundNBT(CallbackInfo info) {
        initItemData();
    }

    /*
    private void copyItemData(ItemStack stack) {
        CompoundNBT itemData = new CompoundNBT();
        extraData.forEach((key, func) -> {
            CompoundNBT dataTag = new CompoundNBT();
            func.write(dataTag);
            itemData.put(key, dataTag);
        });
        ((IItemData)(Object)stack).extraData().forEach((key, func) -> {
            if(itemData.contains(key))
                func.read(itemData.getCompound(key));
        });
    }
    /*

    private void saveItemData() {
        if(extraData().isEmpty()) return;
        ItemStack thisStack = (ItemStack)(Object)this;
        CompoundNBT tag = thisStack.getOrCreateTag();
        CompoundNBT itemData = new CompoundNBT();
        extraData.forEach((key, func) -> {
            if(func.isItemSuitable(thisStack)) {
                CompoundNBT dataTag = new CompoundNBT();
                func.write(dataTag);
                itemData.put(key, dataTag);
            }
        });
        if(!itemData.isEmpty())
            tag.put(ItemExtraData.ITEM_DATA, itemData);
    }

    private static void saveItemData(ItemStack stack) {
        IItemData data = ExtraDataUtil.itemData(stack);
        if(data.extraData().isEmpty()) return;
        CompoundNBT tag = stack.getOrCreateTag();
        CompoundNBT itemData = new CompoundNBT();
        data.extraData().forEach((key, func) -> {
            if(func.isItemSuitable(stack)) {
                CompoundNBT dataTag = new CompoundNBT();
                func.write(dataTag);
                itemData.put(key, dataTag);
            }
        });
        if(!itemData.isEmpty())
            tag.put(ItemExtraData.ITEM_DATA, itemData);
    }


     */

     /*
    private void initItemData() {
        ItemStack thisStack = (ItemStack)(Object)this;
        HashMap<String, Function<ItemStack, ItemExtraData>> dataMap = new HashMap<>();
        ExtraDataEvent.ItemStack event = new ExtraDataEvent.ItemStack(dataMap);
        MinecraftForge.EVENT_BUS.post(event);
        dataMap.forEach((key, value) -> {
            try {
                ItemExtraData itemExtraData = value.apply(thisStack);
                if(itemExtraData.isItemSuitable(thisStack))
                    extraData.put(key, value.apply(thisStack));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //readData();
    }

    /*
    private void readData() {
        if(extraData().isEmpty()) return;
        ItemStack thisStack = (ItemStack)(Object)this;
        if(originTag() == null || !originTag().contains(ItemExtraData.ITEM_DATA)) {
            return;
        }
        CompoundNBT itemData = originTag().getCompound(ItemExtraData.ITEM_DATA);
        extraData.forEach((key, func) -> {
            if(func.isItemSuitable(thisStack) && itemData.contains(key)) {
                CompoundNBT data = itemData.getCompound(key);
                func.read(data);
                func.fixData(thisStack);
            }
        });

        if(itemData.isEmpty())
            originTag().remove(ItemExtraData.ITEM_DATA);
    }

     */

    @Inject(method = "attemptDamageItem", at = @At("RETURN"), cancellable = true)
    public void onAttemptDamageItem(int amount, Random rand, @Nullable ServerPlayerEntity damager, CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValueZ() && getTag() != null && getTag().contains(LibElementTool.TOOL_ELEMENT)) {
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
