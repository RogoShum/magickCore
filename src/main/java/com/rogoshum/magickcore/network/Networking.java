package com.rogoshum.magickcore.network;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.item.ManaItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Networking {
    public static SimpleChannel INSTANCE;
    public static final String VERSION = "1.0";
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessage() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(MagickCore.MOD_ID, "entity_state"),
                () -> VERSION,
                (version) -> version.equals(VERSION),
                (version) -> version.equals(VERSION)
        );

        INSTANCE.messageBuilder(EntityStatePack.class, nextID())
                .encoder(EntityStatePack::toBytes)
                .decoder(EntityStatePack::new)
                .consumer(EntityStatePack::handler)
                .add();

        INSTANCE.messageBuilder(ManaDataPack.class, nextID())
                .encoder(ManaDataPack::toBytes)
                .decoder(ManaDataPack::new)
                .consumer(ManaDataPack::handler)
                .add();

        INSTANCE.messageBuilder(ManaItemDataPack.class, nextID())
                .encoder(ManaItemDataPack::toBytes)
                .decoder(ManaItemDataPack::new)
                .consumer(ManaItemDataPack::handler)
                .add();
    }


}
