package com.rogoshum.magickcore.api;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public interface IItemContainer {
    String getItemName();
    public Item getItem();

    public boolean hasKey();

    public String[] getKeys();

    public boolean matches(ItemStack stack);

    IItemContainer read(JsonObject json);

    IItemContainer read(PacketBuffer buffer);

    void write(PacketBuffer buffer, IItemContainer recipe);
}
