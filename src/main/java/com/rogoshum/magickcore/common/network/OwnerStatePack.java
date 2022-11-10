package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.common.api.entity.IOwnerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class OwnerStatePack extends EntityPack{
    private final UUID uuid;

    public OwnerStatePack(PacketBuffer buffer) {
        super(buffer);
        uuid = buffer.readUniqueId();
    }

    public OwnerStatePack(int id, UUID uuid) {
        super(id);
        this.uuid = uuid;
    }

    public void toBytes(PacketBuffer buf) {
        super.toBytes(buf);
        buf.writeUniqueId(uuid);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;
        Entity entity = Minecraft.getInstance().world.getEntityByID(this.id);
        if(entity == null || entity.removed)
            return;
        if(entity instanceof IOwnerEntity) {
            IOwnerEntity iOwnerEntity = (IOwnerEntity) entity;
            iOwnerEntity.setOwnerUUID(uuid);
        }
    }
}
