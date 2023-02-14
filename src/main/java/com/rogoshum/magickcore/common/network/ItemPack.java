package com.rogoshum.magickcore.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class ItemPack {
    protected final String id;

    public ItemPack(FriendlyByteBuf buffer) {
        id = buffer.readUtf();
    }

    public ItemPack(String id) {
        this.id = id;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.id);
    }

    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            doWork(ctx);
        });
        ctx.get().setPacketHandled(true);
    }

    public abstract void doWork(Supplier<NetworkEvent.Context> ctx);
}
