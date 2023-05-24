package com.rogoshum.magickcore.common.network;

import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.api.registry.MagickRegistry;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.item.MagickContextItem;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class CTriggerAssemblyPack extends EntityPack {
    private final String element;

    public CTriggerAssemblyPack(FriendlyByteBuf buffer) {
        super(buffer);
        element = buffer.readUtf();
    }

    private CTriggerAssemblyPack(int id, String element) {
        super(id);
        this.element = element;
    }

    public static CTriggerAssemblyPack trigger(String element) {
        return new CTriggerAssemblyPack(0, element);
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeUtf(element);
    }

    @Override
    public void doWork(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) return;
        ServerPlayer player = ctx.get().getSender();
        if(player == null || player.isRemoved())
            return;

        MagickContext context = MagickContext.create(player.level).caster(player).victim(player).noCost().mute().applyType(ApplyType.ASSEMBLY).element(MagickRegistry.getElement(element));
        MagickReleaseHelper.releaseMagick(context);
    }
}
