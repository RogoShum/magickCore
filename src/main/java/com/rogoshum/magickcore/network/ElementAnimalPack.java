package com.rogoshum.magickcore.network;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.capability.IElementAnimalState;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.init.ModBuff;
import com.rogoshum.magickcore.init.ModElements;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Iterator;
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
        IElementAnimalState state = entity.getCapability(MagickCore.elementAnimal).orElse(null);
        if(state != null) {
            state.setElement(ModElements.getElement(this.element));
        }
    }
}
