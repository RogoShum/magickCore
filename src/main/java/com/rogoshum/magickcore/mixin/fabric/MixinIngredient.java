package com.rogoshum.magickcore.mixin.fabric;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.rogoshum.magickcore.api.INBTIngredient;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

@Mixin(Ingredient.class)
public abstract class MixinIngredient implements INBTIngredient {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private HashSet<String> keySet;
    private HashMap<String, Tag> keyMap;
    private CompoundTag tag;

    private static HashSet<String> getStackNBTKeySet(ItemStack stack) {
        HashSet<String> keys = new HashSet<>();
        if(stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            keys = NBTTagHelper.getNBTKeySet(tag);
        }
        return keys;
    }

    private static HashMap<String, Tag> getStackNBTKeyMap(ItemStack stack) {
        HashMap<String, Tag> keys = new HashMap<>();
        if(stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            keys = NBTTagHelper.getNBTKeyMap(tag);
        }
        return keys;
    }

    @Inject(method = "test(Lnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "RETURN"), cancellable = true)
    public void onTest(ItemStack input, CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValueZ()) {
            HashSet<String> inputKeys = getStackNBTKeySet(input);
            HashMap<String, Tag> inputKeyMap = getStackNBTKeyMap(input);
            if(this.keySet != null && !inputKeys.containsAll(this.keySet)) {
                cir.setReturnValue(false);
                return;
            }
            if(this.keyMap != null)
                for(String key : this.keyMap.keySet()) {
                    if(!inputKeyMap.containsKey(key) || !inputKeyMap.get(key).getAsString().equals(this.keyMap.get(key).getAsString())) {
                        cir.setReturnValue(false);
                        return;
                    }
                }
        }
    }

    @Inject(method = "toNetwork", at = @At(value = "RETURN"))
    public void onFromNetwork(FriendlyByteBuf buffer, CallbackInfo ci) {
        CompoundTag tag = new CompoundTag();
        if(((INBTIngredient) this).getNBTMap() != null)
            tag = ((INBTIngredient) this).getNBTMap();
        buffer.writeNbt(tag);
    }

    @Inject(method = "fromNetwork", at = @At(value = "RETURN"))
    private static void onFromNetwork(FriendlyByteBuf buffer, CallbackInfoReturnable<Ingredient> cir) {
        Ingredient ingredient = cir.getReturnValue();
        CompoundTag tag = buffer.readNbt();
        ((INBTIngredient)(Object)ingredient).setNBTMap(tag);
    }

    @Inject(method = "fromJson", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonElement;isJsonObject()Z", remap = false), cancellable = true, remap = false)
    private static void onFromJsonNamew(JsonElement jsonElement, CallbackInfoReturnable<Ingredient> cir) {
        if (jsonElement.isJsonObject()) {
            JsonObject json = jsonElement.getAsJsonObject();
            if(json.has("name")) {
                String name = GsonHelper.getAsString(json, "name");
                ImmutableList.Builder<ItemStack> items = ImmutableList.builder();
                Registry.ITEM.forEach(item -> {
                    if(item.toString().contains(name)) {
                        items.add(new ItemStack(item));
                    }
                });
                Ingredient ingredient = Ingredient.of(Arrays.stream(items.build().toArray(new ItemStack[0])));
                cir.setReturnValue(ingredient);
                cir.cancel();
            }
        }
    }

    @Inject(method = "fromJson", at = @At(value = "RETURN"))
    private static void onFromJson(JsonElement jsonElement, CallbackInfoReturnable<Ingredient> cir) {
        Ingredient ingredient = cir.getReturnValue();
        if (jsonElement.isJsonObject()) {
            JsonObject json = jsonElement.getAsJsonObject();
            CompoundTag nbt = getNBTMap(json);
            if(nbt != null)
                ((INBTIngredient)(Object)ingredient).setNBTMap(nbt);
        }
    }

    private static CompoundTag getNBTMap(JsonObject json) {
        if(json.has("nbt")) {
            JsonElement element = json.get("nbt");
            CompoundTag nbt;
            try {
                if (element.isJsonObject())
                    nbt = TagParser.parseTag(GSON.toJson(element));
                else
                    nbt = TagParser.parseTag(GsonHelper.convertToString(element, "nbt"));
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
            return nbt;
        }
        return null;
    }

    @Override
    public void setNBTMap(CompoundTag tag) {
        this.tag = tag;
        this.keySet = NBTTagHelper.getNBTKeySet(tag);
        this.keyMap = NBTTagHelper.getNBTKeyMap(tag);
    }

    @Override
    public CompoundTag getNBTMap() {
        return this.tag;
    }
}
