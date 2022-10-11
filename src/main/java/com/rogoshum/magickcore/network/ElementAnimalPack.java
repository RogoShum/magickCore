package com.rogoshum.magickcore.network;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.init.ModElements;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.registry.MagickRegistry;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
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
        EntityStateData state = ExtraDataHelper.entityStateData(entity);
        if(state != null) {
            state.setElement(MagickRegistry.getElement(this.element));
        }
    }
}
