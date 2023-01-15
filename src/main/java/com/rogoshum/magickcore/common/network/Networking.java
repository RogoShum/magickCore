package com.rogoshum.magickcore.common.network;

public class Networking {
    public static SimpleChannel INSTANCE;

    public static void registerMessage() {

        INSTANCE = new SimpleChannel();

        INSTANCE.messageBuilder(EntityStatePack.class)
                .encoder(EntityStatePack::toBytes)
                .decoder(EntityStatePack::new)
                .client(EntityStatePack::handler)
                .add();

        INSTANCE.messageBuilder(EntityCompoundTagPack.class)
                .encoder(EntityCompoundTagPack::toBytes)
                .decoder(EntityCompoundTagPack::new)
                .client(EntityCompoundTagPack::handler)
                .add();

        INSTANCE.messageBuilder(ManaCapacityPack.class)
                .encoder(ManaCapacityPack::toBytes)
                .decoder(ManaCapacityPack::new)
                .client(ManaCapacityPack::handler)
                .add();

        INSTANCE.messageBuilder(ManaItemDataPack.class)
                .encoder(ManaItemDataPack::toBytes)
                .decoder(ManaItemDataPack::new)
                .client(ManaItemDataPack::handler)
                .add();

        INSTANCE.messageBuilder(ElementAnimalPack.class)
                .encoder(ElementAnimalPack::toBytes)
                .decoder(ElementAnimalPack::new)
                .client(ElementAnimalPack::handler)
                .add();

        INSTANCE.messageBuilder(TakenStatePack.class)
                .encoder(TakenStatePack::toBytes)
                .decoder(TakenStatePack::new)
                .client(TakenStatePack::handler)
                .add();

        INSTANCE.messageBuilder(ParticlePack.class)
                .encoder(ParticlePack::toBytes)
                .decoder(ParticlePack::new)
                .client(ParticlePack::handler)
                .add();

        INSTANCE.messageBuilder(OwnerStatePack.class)
                .encoder(OwnerStatePack::toBytes)
                .decoder(OwnerStatePack::new)
                .client(OwnerStatePack::handler)
                .add();
        INSTANCE.messageBuilder(ParticleSamplePack.class)
                .encoder(ParticleSamplePack::toBytes)
                .decoder(ParticleSamplePack::new)
                .client(ParticleSamplePack::handler)
                .add();

        INSTANCE.messageBuilder(SSpellSwapPack.class)
                .encoder(SSpellSwapPack::toBytes)
                .decoder(SSpellSwapPack::new)
                .client(SSpellSwapPack::handler)
                .add();

        INSTANCE.messageBuilder(CSpellSwapPack.class)
                .encoder(CSpellSwapPack::toBytes)
                .decoder(CSpellSwapPack::new)
                .server(CSpellSwapPack::handler)
                .add();
    }
}
