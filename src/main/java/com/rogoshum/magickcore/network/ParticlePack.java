package com.rogoshum.magickcore.network;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.capability.IElementAnimalState;
import com.rogoshum.magickcore.init.ModElements;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ParticlePack extends EntityPack{
    private final String element;

    public ParticlePack(PacketBuffer buffer) {
        super(buffer);
        element = buffer.readString();
    }

    public ParticlePack(int id, String element) {
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
        IElementAnimalState state = entity.getCapability(MagickCore.elementAnimal).orElse(null);
        if(state != null) {
            state.setElement(ModElements.getElement(this.element));
        }
    }
}
