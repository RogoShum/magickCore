package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.gui.SpellSwapBoxGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
public class SSpellSwapPack extends EntityPack<ClientNetworkContext<?>> {
    private final CompoundTag tag;

    public SSpellSwapPack(FriendlyByteBuf buffer) {
        super(buffer);
        tag = buffer.readNbt();
    }

    public SSpellSwapPack(int id, Player player) {
        super(id);
        CompoundTag tag = player.getPersistentData();
        if(!tag.contains(Player.PERSISTED_NBT_TAG))
            tag.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        tag = tag.getCompound(Player.PERSISTED_NBT_TAG);
        if(!tag.contains("MagickCore"))
            tag.put("MagickCore", new CompoundTag());
        this.tag = tag.getCompound("MagickCore");
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeNbt(this.tag);
    }

    public static void handler(ClientNetworkContext<SSpellSwapPack> context) {
        SSpellSwapPack pack = context.packet();
        if(pack.tag == null) return;
        Entity entity = Minecraft.getInstance().level.getEntity(pack.id);
        if(!(entity instanceof Player) || entity.removed)
            return;
        Player player = (Player) entity;
        CompoundTag tag = player.getPersistentData();
        if(!tag.contains(Player.PERSISTED_NBT_TAG))
            tag.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        tag = tag.getCompound(Player.PERSISTED_NBT_TAG);
        tag.put("MagickCore", pack.tag);

        Minecraft.getInstance().setScreen(new SpellSwapBoxGUI(new TranslatableComponent(MagickCore.MOD_ID + ".test")));
    }
}
