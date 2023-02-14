package com.rogoshum.magickcore.api;

import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;

public interface IItemContainer {
    String getItemName();
    public Item getItem();

    public boolean hasKey();

    public String[] getKeys();

    public boolean matches(ItemStack stack);

    IItemContainer read(JsonObject json);

    IItemContainer read(FriendlyByteBuf buffer);

    void write(FriendlyByteBuf buffer, IItemContainer recipe);
}
