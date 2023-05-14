package com.rogoshum.magickcore.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class EntityCompoundTagPack extends EntityPack{
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

    @Override
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;

        Entity entity = Minecraft.getInstance().level.getEntity(this.id);
        if(entity == null || entity.isRemoved())
            return;

        entity.load(nbt);
    }

    public static void updateEntity(Entity entity) {
        if(entity.level.isClientSide) return;
        Networking.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                        entity.position().x, entity.position().y, entity.position().z, 48, entity.level.dimension()
                )),
                new EntityCompoundTagPack(entity.getId(), entity));
    }
}
