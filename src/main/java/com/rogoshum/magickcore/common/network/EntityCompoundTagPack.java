package com.rogoshum.magickcore.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class EntityCompoundTagPack extends EntityPack{
    private final CompoundNBT nbt;

    public EntityCompoundTagPack(PacketBuffer buffer) {
        super(buffer);
        nbt = buffer.readNbt();
    }

    public EntityCompoundTagPack(int id, Entity entity) {
        super(id);
        CompoundNBT nbt = new CompoundNBT();
        entity.saveAsPassenger(nbt);
        this.nbt = nbt;
    }

    public void toBytes(PacketBuffer buf) {
        super.toBytes(buf);
        buf.writeNbt(nbt);
    }

    @Override
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;

        Entity entity = Minecraft.getInstance().level.getEntity(this.id);
        if(entity == null || entity.removed)
            return;

        entity.load(nbt);
    }

    public static void updateEntity(Entity entity) {
        if(entity.level.isClientSide) return;
        Networking.INSTANCE.send(
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                new EntityCompoundTagPack(entity.getId(), entity));
    }
}
