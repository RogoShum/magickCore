package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.client.Minecraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

public class EntityStatePack extends EntityPack<ClientNetworkContext<?>>{
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

    public static void handler(ClientNetworkContext<EntityStatePack> context) {
        EntityStatePack pack = context.packet();
        if(pack.tag == null) return;
        Entity entity = Minecraft.getInstance().level.getEntity(pack.id);
        if(entity == null || entity.removed)
            return;
        EntityStateData state = ExtraDataUtil.entityStateData(entity);
        if(state != null) {
            state.read(pack.tag);
        }
    }
}
