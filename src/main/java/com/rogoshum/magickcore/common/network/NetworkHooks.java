package com.rogoshum.magickcore.common.network;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;

public class NetworkHooks {
    public static Packet<ClientGamePacketListener> getEntitySpawningPacket(Entity entity) {
        return new ClientboundAddEntityPacket(entity);
    }
}
