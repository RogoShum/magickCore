package com.rogoshum.magickcore.common.network;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class EntityCompoundTagPack extends EntityPack<ClientNetworkContext<?>>{
    private final CompoundTag nbt;

    public EntityCompoundTagPack(FriendlyByteBuf buffer) {
        super(buffer);
        nbt = buffer.readNbt();
    }

    public EntityCompoundTagPack(int id, Entity entity) {
        super(id);
        CompoundTag nbt = new CompoundTag();
        entity.saveAsPassenger(nbt);
        this.nbt = nbt;
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeNbt(nbt);
    }

    public static void handler(ClientNetworkContext<EntityCompoundTagPack> context) {
        EntityCompoundTagPack pack = context.packet();
        Entity entity = Minecraft.getInstance().level.getEntity(pack.id);
        if(entity == null || entity.removed)
            return;

        entity.load(pack.nbt);
    }

    public static void updateEntity(Entity entity) {
        if(entity.level.isClientSide) return;
        Networking.INSTANCE.send(
                SimpleChannel.SendType.server(PlayerLookup.tracking(entity)),
                new EntityCompoundTagPack(entity.getId(), entity));
    }
}
