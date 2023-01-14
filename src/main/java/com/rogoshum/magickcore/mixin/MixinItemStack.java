package com.rogoshum.magickcore.mixin;

import com.rogoshum.magickcore.common.lib.LibElementTool;
import com.rogoshum.magickcore.common.lib.LibElements;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ItemStack.class)
public class MixinItemStack{
    //private final HashMap<String, ItemExtraData> extraData = new HashMap<>();

    @Shadow
    public int getMaxDamage() { return 0; }

    @Shadow
    public int getDamageValue() {return 0;}

    @Shadow
    public void setDamageValue(int damage) {}

    @Shadow
    public CompoundTag getTag() { return new CompoundTag(); }

    /*
    @Override
    public HashMap<String, ItemExtraData> extraData() {
        return extraData;
    }

    /*
    @Inject(method = "getTag", at = @At("HEAD"))
    public void onGetTag(CallbackInfoReturnable<CompoundTag> cir) {
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

    public CompoundTag originTag() {
        ItemStack thisStack = (ItemStack)(Object)this;
        return ObfuscationReflectionHelper.getPrivateValue(ItemStack.class, thisStack, "field_77990_d");
    }

    @Shadow
    public void setTag(@Nullable CompoundTag nbt) { }

    /*
    @Inject(method = "copy", at = @At("RETURN"))
    public void onCopy(CallbackInfoReturnable<ItemStack> stack) {
        if(stack.getReturnValue().isEmpty()) return;
        copyItemData(stack.getReturnValue());
    }

    @Inject(method = "write", at = @At("HEAD"))
    public void onWrite(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> tag) {
        saveItemData();
    }

    @Inject(method = "setTag", at = @At("RETURN"))
    public void onSetTag(@Nullable CompoundTag nbt, CallbackInfo info) {
        if(nbt != null && nbt.contains(ItemExtraData.ITEM_DATA)) {
            ItemStack thisStack = (ItemStack)(Object)this;
            CompoundTag tag = nbt.getCompound(ItemExtraData.ITEM_DATA);
            tag.keySet().forEach((key) -> {
                ExtraDataUtil.itemData(thisStack).<ItemManaData>execute(key, data -> data.read(tag.getCompound(key)));
            });
        }
    }

     */

    /*
    @Inject(method = "<init>(Lnet/minecraft/util/IItemProvider;ILnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"), cancellable = true)
    protected void onConstructor(CallbackInfo info) {
        initItemData();
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"), cancellable = true)
    protected void onConstructorCompoundTag(CallbackInfo info) {
        initItemData();
    }

    /*
    private void copyItemData(ItemStack stack) {
        CompoundTag itemData = new CompoundTag();
        extraData.forEach((key, func) -> {
            CompoundTag dataTag = new CompoundTag();
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
        CompoundTag tag = thisStack.getOrCreateTag();
        CompoundTag itemData = new CompoundTag();
        extraData.forEach((key, func) -> {
            if(func.isItemSuitable(thisStack)) {
                CompoundTag dataTag = new CompoundTag();
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
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag itemData = new CompoundTag();
        data.extraData().forEach((key, func) -> {
            if(func.isItemSuitable(stack)) {
                CompoundTag dataTag = new CompoundTag();
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
        CompoundTag itemData = originTag().getCompound(ItemExtraData.ITEM_DATA);
        extraData.forEach((key, func) -> {
            if(func.isItemSuitable(thisStack) && itemData.contains(key)) {
                CompoundTag data = itemData.getCompound(key);
                func.read(data);
                func.fixData(thisStack);
            }
        });

        if(itemData.isEmpty())
            originTag().remove(ItemExtraData.ITEM_DATA);
    }

     */

    @Inject(method = "hurt", at = @At("RETURN"), cancellable = true)
    public void onAttemptDamageItem(int amount, Random rand, ServerPlayer damager, CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValueZ() && getTag() != null && getTag().contains(LibElementTool.TOOL_ELEMENT)) {
            CompoundTag tag = getTag();
            if(tag.getCompound(LibElementTool.TOOL_ELEMENT).contains(LibElements.WITHER)) {
                CompoundTag elements = tag.getCompound(LibElementTool.TOOL_ELEMENT);
                int count = elements.getInt(LibElements.WITHER);
                if(count > 1)
                    elements.putInt(LibElements.WITHER, count - amount);
                else
                    elements.remove(LibElements.WITHER);
                setDamageValue(getMaxDamage() - 1);
                cir.setReturnValue(false);
            }
        }
    }
}
