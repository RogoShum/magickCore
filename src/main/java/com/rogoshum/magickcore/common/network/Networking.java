package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.MagickCore;
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
                new ResourceLocation(MagickCore.MOD_ID, "network"),
                () -> VERSION,
                (version) -> version.equals(VERSION),
                (version) -> version.equals(VERSION)
        );

        INSTANCE.messageBuilder(EntityStatePack.class, nextID())
                .encoder(EntityStatePack::toBytes)
                .decoder(EntityStatePack::new)
                .consumer(EntityStatePack::handler)
                .add();

        INSTANCE.messageBuilder(EntityCompoundTagPack.class, nextID())
                .encoder(EntityCompoundTagPack::toBytes)
                .decoder(EntityCompoundTagPack::new)
                .consumer(EntityCompoundTagPack::handler)
                .add();

        INSTANCE.messageBuilder(ManaCapacityPack.class, nextID())
                .encoder(ManaCapacityPack::toBytes)
                .decoder(ManaCapacityPack::new)
                .consumer(ManaCapacityPack::handler)
                .add();

        INSTANCE.messageBuilder(ManaItemDataPack.class, nextID())
                .encoder(ManaItemDataPack::toBytes)
                .decoder(ManaItemDataPack::new)
                .consumer(ManaItemDataPack::handler)
                .add();

        INSTANCE.messageBuilder(ElementAnimalPack.class, nextID())
                .encoder(ElementAnimalPack::toBytes)
                .decoder(ElementAnimalPack::new)
                .consumer(ElementAnimalPack::handler)
                .add();

        INSTANCE.messageBuilder(TakenStatePack.class, nextID())
                .encoder(TakenStatePack::toBytes)
                .decoder(TakenStatePack::new)
                .consumer(TakenStatePack::handler)
                .add();

        INSTANCE.messageBuilder(ParticlePack.class, nextID())
                .encoder(ParticlePack::toBytes)
                .decoder(ParticlePack::new)
                .consumer(ParticlePack::handler)
                .add();

        INSTANCE.messageBuilder(OwnerStatePack.class, nextID())
                .encoder(OwnerStatePack::toBytes)
                .decoder(OwnerStatePack::new)
                .consumer(OwnerStatePack::handler)
                .add();
    }


}
