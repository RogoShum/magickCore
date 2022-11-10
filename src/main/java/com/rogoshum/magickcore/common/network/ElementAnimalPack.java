package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.common.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.registry.MagickRegistry;
import com.rogoshum.magickcore.common.util.ExtraDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ElementAnimalPack extends EntityPack{
    private final String element;

    public ElementAnimalPack(PacketBuffer buffer) {
        super(buffer);
        element = buffer.readString();
    }

    public ElementAnimalPack(int id, String element) {
        super(id);
        this.element = element;
    }

    public void toBytes(PacketBuffer buf) {
        super.toBytes(buf);
        buf.writeString(this.element);
    }

    @Override
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;
        Entity entity = Minecraft.getInstance().world.getEntityByID(this.id);
        if(entity == null || entity.removed)
            return;
        EntityStateData state = ExtraDataUtil.entityStateData(entity);
        if(state != null) {
            state.setElement(MagickRegistry.getElement(this.element));
        }
    }
}
