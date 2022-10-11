package com.rogoshum.magickcore.network;

import com.rogoshum.magickcore.api.ISpellContext;
import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.magick.ManaData;
import com.rogoshum.magickcore.magick.context.SpellContext;
import com.rogoshum.magickcore.registry.MagickRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ManaDataPack extends EntityPack{
    private final CompoundNBT nbt;

    public ManaDataPack(PacketBuffer buffer) {
        super(buffer);
        nbt = buffer.readCompoundTag();
    }

    public ManaDataPack(int id, SpellContext spellContext) {
        super(id);
        CompoundNBT nbt = new CompoundNBT();
        spellContext.serialize(nbt);
        this.nbt = nbt;
    }

    public void toBytes(PacketBuffer buf) {
        super.toBytes(buf);
        buf.writeCompoundTag(nbt);
    }

    @Override
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;

        Entity entity = Minecraft.getInstance().world.getEntityByID(this.id);
        if(entity == null || entity.removed || !(entity instanceof ISpellContext))
            return;

        SpellContext data = ((ISpellContext) entity).spellContext();
        if(data != null) {
            data.deserialize(nbt);
        }
    }
}
