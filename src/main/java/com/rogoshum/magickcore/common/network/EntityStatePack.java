package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EntityStatePack extends EntityPack{
    private final CompoundTag tag;

    public EntityStatePack(FriendlyByteBuf buffer) {
        super(buffer);
        tag = buffer.readNbt();
    }

    public EntityStatePack(int id, CompoundTag tag) {
        super(id);
        this.tag = tag;
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeNbt(this.tag);
    }

    @Override
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;
        if(tag == null) return;
        Entity entity = Minecraft.getInstance().level.getEntity(this.id);
        if(entity == null || entity.isRemoved())
            return;
        EntityStateData state = ExtraDataUtil.entityStateData(entity);
        if(state != null) {
            state.read(tag);
        }
    }
}
