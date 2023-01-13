package com.rogoshum.magickcore.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class ItemPack {
    protected final String id;

    public ItemPack(PacketBuffer buffer) {
        id = buffer.readUtf();
    }

    public ItemPack(String id) {
        this.id = id;
    }

    public void toBytes(PacketBuffer buf) {
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
