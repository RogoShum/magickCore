package com.rogoshum.magickcore.common.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ServerNetworkContext<T extends NetworkPack<?>> extends NetworkContext<T>{
    private ServerNetworkContext(T pack, MinecraftServer client, ServerPlayer player, ServerGamePacketListenerImpl handler, PacketSender responseSender){
        this.packet = pack;
        this.server = client;
        this.player = player;
        this.handler = handler;
        this.responseSender = responseSender;
    }
    public static <T extends NetworkPack<?>> ServerNetworkContext<T> create(T pack, MinecraftServer server,
                                                                            ServerPlayer player, ServerGamePacketListenerImpl handler, PacketSender responseSender) {
        return new ServerNetworkContext<>(pack, server, player, handler, responseSender);
    }

    private final MinecraftServer server;
    private final ServerPlayer player;
    private final ServerGamePacketListenerImpl handler;
    private final PacketSender responseSender;

    public MinecraftServer server() {
        return server;
    }

    private final T packet;

    public T packet() {
        return packet;
    }

    public ServerGamePacketListenerImpl handler() {
        return handler;
    }

    public PacketSender responseSender() {
        return responseSender;
    }

    public ServerPlayer player() {
        return player;
    }

}
