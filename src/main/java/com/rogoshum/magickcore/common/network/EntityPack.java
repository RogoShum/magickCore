package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.MagickCore;
import net.minecraft.network.FriendlyByteBuf;

public abstract class EntityPack<T extends NetworkContext<?>> extends NetworkPack<T>{
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
}
