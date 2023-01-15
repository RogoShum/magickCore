package com.rogoshum.magickcore.common.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

public class ClientNetworkContext<T extends NetworkPack<?>> extends NetworkContext<T> {
    private ClientNetworkContext(T pack, Minecraft client, ClientPacketListener handler, PacketSender responseSender){
        this.packet = pack;
        this.client = client;
        this.handler = handler;
        this.responseSender = responseSender;
    }
    public static <T extends NetworkPack<?>> ClientNetworkContext<T> create(T pack, Minecraft client, ClientPacketListener handler, PacketSender responseSender) {
        return new ClientNetworkContext<>(pack, client, handler, responseSender);
    }

    private final Minecraft client;
    private final ClientPacketListener handler;
    private final PacketSender responseSender;
    private final T packet;

    public T packet() {
        return packet;
    }

    public Minecraft client() {
        return client;
    }
    public ClientPacketListener handler() {
        return handler;
    }
    public PacketSender responseSender() {
        return responseSender;
    }
}
