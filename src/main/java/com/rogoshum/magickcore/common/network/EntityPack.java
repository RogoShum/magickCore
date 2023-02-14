package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.MagickCore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;
import java.util.logging.Logger;

public abstract class EntityPack {
    protected final int id;

    public EntityPack(FriendlyByteBuf buffer) {
        id = buffer.readInt();
    }

    public EntityPack(int id) {
        this.id = id;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
    }

    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> doWork(ctx));
        ctx.get().setPacketHandled(true);
    }

    public abstract void doWork(Supplier<NetworkEvent.Context> ctx);
}
