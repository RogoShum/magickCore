package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.MagickCore;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleChannel {
    private final HashMap<ResourceLocation, Message<? extends NetworkPack<?>>> packMap = new HashMap<>();
    public <T extends NetworkPack<?>> Message<T> messageBuilder(Class<T> clazz) {
        return new Message<>(this, clazz);
    }

    public <MSG extends NetworkPack<?>> void send(SendType sendType, MSG networkPack) {
        if(sendType.isClient) {
            Map.Entry<ResourceLocation, Message<? extends NetworkPack<?>>> messageEntry = packMap.entrySet().stream().filter((entry -> entry.getValue().type == networkPack.getClass())).findFirst().get();
            FriendlyByteBuf buf = PacketByteBufs.create();
            Message<MSG> message = (Message<MSG>) messageEntry.getValue();
            message.encoder.accept(networkPack, buf);
            ClientPlayNetworking.send(messageEntry.getKey(), buf);
        } else {
            Map.Entry<ResourceLocation, Message<? extends NetworkPack<?>>> messageEntry = packMap.entrySet().stream().filter((entry -> entry.getValue().type == networkPack.getClass())).findFirst().get();
            FriendlyByteBuf buf = PacketByteBufs.create();
            Message<MSG> message = (Message<MSG>) messageEntry.getValue();
            message.encoder.accept(networkPack, buf);
            if(sendType.players != null)
                for (ServerPlayer player : sendType.players) {
                    ServerPlayNetworking.send(player, messageEntry.getKey(), buf);
                }
        }
    }

    public static class SendType {
        private final ServerPlayer[] players;
        private final boolean isClient;
        private SendType(boolean isClient, ServerPlayer[] players) {
            this.isClient = isClient;
            this.players = players;
        }

        public static SendType server(ServerPlayer... players) {
            return new SendType(false, players);
        }

        public static SendType server(Collection<ServerPlayer> players) {
            return new SendType(false, (ServerPlayer[]) players.toArray());
        }

        public static SendType server() {
            return new SendType(false, null);
        }

        public static SendType client() {
            return new SendType(true, null);
        }
    }

    public static class Message<MSG extends NetworkPack<?>> {
        private BiConsumer<MSG, FriendlyByteBuf> encoder;
        private Function<FriendlyByteBuf, MSG> decoder;
        private Consumer<ClientNetworkContext> clientConsumer;
        private Consumer<ServerNetworkContext> serverConsumer;
        private final Class<MSG> type;
        private final SimpleChannel channel;
        private EnvType environment;
        public Message(SimpleChannel channel, Class<MSG> type) {
            this.channel = channel;
            this.type = type;
        }

        public Message<MSG> encoder(BiConsumer<MSG, FriendlyByteBuf> encoder) {
            this.encoder = encoder;
            return this;
        }

        public Message<MSG> decoder(Function<FriendlyByteBuf, MSG> decoder) {
            this.decoder = decoder;
            return this;
        }

        public Message<MSG> client(Consumer<ClientNetworkContext> consumer) {
            this.clientConsumer = consumer;
            this.environment = EnvType.CLIENT;
            return this;
        }

        public Message<MSG> server(Consumer<ServerNetworkContext> consumer) {
            this.serverConsumer = consumer;
            this.environment = EnvType.SERVER;
            return this;
        }

        public void add() {
            ResourceLocation res = MagickCore.fromId(type.getName());
            this.channel.packMap.put(res, this);
            if(environment == EnvType.CLIENT) {
                ClientPlayNetworking.registerGlobalReceiver(res, (client, handler, buf, responseSender) -> {
                    MSG pack = this.decoder.apply(buf);
                    client.execute(() -> {
                        ClientNetworkContext<MSG> context = ClientNetworkContext.create(pack, client, handler, responseSender);
                        this.clientConsumer.accept(context);
                    });
                });
            } else {
                ServerPlayNetworking.registerGlobalReceiver(res, (server, player, handler, buf, responseSender) -> {
                    MSG pack = this.decoder.apply(buf);
                    server.execute(() -> {
                        ServerNetworkContext<MSG> context = ServerNetworkContext.create(pack, server, player, handler, responseSender);
                        this.serverConsumer.accept(context);
                    });
                });
            }
        }
    }
}
