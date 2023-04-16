package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.api.entity.IOwnerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class OwnerStatePack extends EntityPack{
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;
        Entity entity = Minecraft.getInstance().level.getEntity(this.id);
        if(entity == null || entity.isRemoved())
            return;
        if(entity instanceof IOwnerEntity) {
            IOwnerEntity iOwnerEntity = (IOwnerEntity) entity;
            iOwnerEntity.setCasterUUID(uuid);
        }
    }
}
