package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.entity.IOwnerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.UUID;
import java.util.function.Supplier;

public class OwnerStatePack extends EntityPack<ClientNetworkContext<?>>{
    private final UUID uuid;

    public OwnerStatePack(FriendlyByteBuf buffer) {
        super(buffer);
        uuid = buffer.readUUID();
    }

    public OwnerStatePack(int id, UUID uuid) {
        super(id);
        this.uuid = uuid;
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeUUID(uuid);
    }

    public static void handler(ClientNetworkContext<OwnerStatePack> context) {
        OwnerStatePack pack = context.packet();
        Entity entity = Minecraft.getInstance().level.getEntity(pack.id);
        if(entity == null || entity.removed)
            return;
        if(entity instanceof IOwnerEntity) {
            IOwnerEntity iOwnerEntity = (IOwnerEntity) entity;
            iOwnerEntity.setOwnerUUID(pack.uuid);
        }
    }
}
