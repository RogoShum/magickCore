package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.client.gui.SpellSwapBoxGUI;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.item.MagickContextItem;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class CSpellSwapPack extends EntityPack<ServerNetworkContext<?>> {
    private final byte operate;
    private final int index;

    public CSpellSwapPack(FriendlyByteBuf buffer) {
        super(buffer);
        operate = buffer.readByte();
        if(operate == 126)
            index = buffer.readInt();
        else index = 0;
    }

    private CSpellSwapPack(int id, byte operate, int index) {
        super(id);
        this.operate = operate;
        this.index = index;
    }

    public static CSpellSwapPack pushItem(int id) {
        return new CSpellSwapPack(id, (byte) 127, 0);
    }

    public static CSpellSwapPack popItem(int id) {
        return new CSpellSwapPack(id, (byte) 125, 0);
    }

    public static CSpellSwapPack swapItem(int id, int index) {
        return new CSpellSwapPack(id, (byte) 126, index);
    }

    public static CSpellSwapPack openGUI(int id) {
        return new CSpellSwapPack(id, (byte) 124, 0);
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeByte(this.operate);
        if(operate == 126)
            buf.writeInt(this.index);
    }

    public static void handler(ServerNetworkContext<CSpellSwapPack> context) {
        CSpellSwapPack pack = context.packet();
        ServerPlayer player = context.player();
        if(player == null || player.removed)
            return;

        NBTTagHelper.PlayerData data = NBTTagHelper.PlayerData.playerData(player);
        if(pack.operate == 125) {
            ItemStack stack = data.popSpell();
            if(!player.addItem(stack))
                player.drop(stack, false, true);
            player.level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), ModSounds.soft_buildup.get(), SoundSource.PLAYERS, 0.25F, 2.0F);
        } else if(pack.operate == 126) {
            if(player.getMainHandItem().getItem() instanceof IManaContextItem) {
                swapSpell(data, player.getMainHandItem(), pack.index);
            } else if(player.getOffhandItem().getItem() instanceof IManaContextItem) {
                swapSpell(data, player.getOffhandItem(), pack.index);
            }
            player.level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), ModSounds.soft_buildup_mid.get(), SoundSource.PLAYERS, 0.25F, 2.0F);
        } else if(pack.operate == 127 && data.getSpells().size() < data.getLimit()) {
            if(player.getMainHandItem().getItem() instanceof MagickContextItem) {
                ItemStack mainHand = player.getMainHandItem();
                ItemStack copy = mainHand.copy();
                copy.setCount(1);
                data.pushSpell(copy);
                mainHand.shrink(1);
                player.level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), ModSounds.soft_buildup_high.get(), SoundSource.PLAYERS, 0.25F, 2.0F);
            } else if(player.getOffhandItem().getItem() instanceof MagickContextItem) {
                ItemStack offHand = player.getOffhandItem();
                ItemStack copy = offHand.copy();
                copy.setCount(1);
                data.pushSpell(copy);
                offHand.shrink(1);
                player.level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), ModSounds.soft_buildup_high.get(), SoundSource.PLAYERS, 0.25F, 2.0F);
            }
        } else if(pack.operate == 124) {
            SSpellSwapPack sSpellSwapPack = new SSpellSwapPack(pack.id, player);
            Networking.INSTANCE.send(SimpleChannel.SendType.server(player), sSpellSwapPack);
        }
        data.save();
    }

    public static void swapSpell(NBTTagHelper.PlayerData data, ItemStack held, int index) {
        ItemManaData manaData = ExtraDataUtil.itemManaData(held);
        ItemStack newContext = new ItemStack(ModItems.MAGICK_CORE.get());
        NBTTagHelper.coreItemFromContext(held, newContext);
        ItemManaData heldData = ExtraDataUtil.itemManaData(newContext);
        boolean hasCore = manaData.contextCore().haveMagickContext();
        ItemStack stack = hasCore ?  data.swapSpell(newContext, index) : data.takeSpell(index);
        heldData.spellContext().copy(ExtraDataUtil.itemManaData(held).spellContext());

        if(!stack.isEmpty()) {
            manaData.spellContext().clear();
            manaData.spellContext().copy(ExtraDataUtil.itemManaData(stack).spellContext());
            NBTTagHelper.contextItemWithCore(held, stack);
            manaData.contextCore().setHave(true);
        } else {
            if (hasCore) {
                manaData.contextCore().setHave(false);
                manaData.spellContext().clear();
                if(data.getSpells().size() < data.getLimit())
                    data.pushSpell(newContext);
            }
        }
    }
}
