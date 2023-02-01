package com.rogoshum.magickcore.common.integration.curios;

import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import com.rogoshum.magickcore.common.network.EntityPack;
import com.rogoshum.magickcore.common.network.ServerNetworkContext;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CCastSpellPack extends EntityPack<ServerNetworkContext<?>> {
    private final byte operate;

    public CCastSpellPack(FriendlyByteBuf buffer) {
        super(buffer);
        operate = buffer.readByte();
    }

    private CCastSpellPack(int id, byte operate) {
        super(id);
        this.operate = operate;
    }

    public static CCastSpellPack cast(int id) {
        return new CCastSpellPack(id, (byte) 0);
    }

    public static CCastSpellPack take(int id) {
        return new CCastSpellPack(id, (byte) 1);
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeByte(operate);
    }

    public static void handler(ServerNetworkContext<CCastSpellPack> ctx) {
        Player player = ctx.player();
        CCastSpellPack pack = ctx.packet();
        if(player == null || player.removed)
            return;
/*
        ItemStack ring = CuriosHelper.getSpiritRing(player);
        if(ring == null || ring.isEmpty()) return;
        if(pack.operate == 0) {
            ItemManaData data = ExtraDataUtil.itemManaData(ring);
            EntityStateData state = ExtraDataUtil.entityStateData(player);
            MagickContext magickContext = MagickContext.create(player.level, data.spellContext());
            MagickElement element = data.spellContext().element;
            MagickContext context = magickContext.caster(player).victim(player).element(element);
            if(context.containChild(LibContext.TRACE)) {
                TraceContext traceContext = context.getChild(LibContext.TRACE);
                traceContext.entity = MagickReleaseHelper.getEntityLookedAt(player);
            }
            if(MagickReleaseHelper.releaseMagick(context))
                ParticleUtil.spawnBlastParticle(player.level, player.position().add(0, player.getBbHeight() * 0.5, 0), 3, state.getElement(), ParticleType.PARTICLE);
        } else {
            ItemStack copy = ring.copy();
            ring.shrink(1);
            if(!player.addItem(copy))
                player.drop(copy, false, true);
        }

 */
    }
}
