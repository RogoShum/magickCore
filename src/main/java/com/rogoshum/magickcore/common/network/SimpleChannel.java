package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.MagickCore;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleChannel {
    private final HashMap<ResourceLocation, Message> packMap = new HashMap<>();
    public <T extends NetworkPack<?>> Message<T> messageBuilder(Class<T> clazz) {
        return new Message<>(this, clazz);
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
