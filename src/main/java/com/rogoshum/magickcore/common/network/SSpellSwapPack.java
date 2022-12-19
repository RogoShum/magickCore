package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.gui.SpellSwapBoxGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SSpellSwapPack extends EntityPack {
    private final CompoundNBT tag;

    public SSpellSwapPack(PacketBuffer buffer) {
        super(buffer);
        tag = buffer.readCompoundTag();
    }

    public SSpellSwapPack(int id, PlayerEntity player) {
        super(id);
        CompoundNBT tag = player.getPersistentData();
        if(!tag.contains(PlayerEntity.PERSISTED_NBT_TAG))
            tag.put(PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT());
        tag = tag.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
        if(!tag.contains("MagickCore"))
            tag.put("MagickCore", new CompoundNBT());
        this.tag = tag.getCompound("MagickCore");
    }

    public void toBytes(PacketBuffer buf) {
        super.toBytes(buf);
        buf.writeCompoundTag(this.tag);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) return;
        if(tag == null) return;
        Entity entity = Minecraft.getInstance().world.getEntityByID(this.id);
        if(!(entity instanceof PlayerEntity) || entity.removed)
            return;
        PlayerEntity player = (PlayerEntity) entity;
        CompoundNBT tag = player.getPersistentData();
        if(!tag.contains(PlayerEntity.PERSISTED_NBT_TAG))
            tag.put(PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT());
        tag = tag.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
        tag.put("MagickCore", this.tag);

        Minecraft.getInstance().displayGuiScreen(new SpellSwapBoxGUI(new TranslationTextComponent(MagickCore.MOD_ID + ".test")));
    }
}
