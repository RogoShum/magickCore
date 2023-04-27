package com.rogoshum.magickcore.common.item.tool;

import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.client.item.StaffRenderer;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.api.magick.MagickElement;
import com.rogoshum.magickcore.api.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.api.magick.context.MagickContext;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.magick.context.child.TraceContext;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

import net.minecraftforge.client.IItemRenderProperties;

public class SpiritCrystalStaffItem extends ManaItem implements IManaContextItem {
    public SpiritCrystalStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return new StaffRenderer();
            }
        });
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        MagickContext magickContext = MagickContext.create(playerIn.level, data.spellContext());
        MagickElement element = data.spellContext().element;
        MagickContext context = magickContext.caster(playerIn).victim(playerIn).element(element);
        if(context.containChild(LibContext.TRACE)) {
            TraceContext traceContext = context.getChild(LibContext.TRACE);
            traceContext.entity = MagickReleaseHelper.getEntityLookedAt(playerIn);
        }
        boolean did = MagickReleaseHelper.releaseMagick(context);
        if(did && playerIn instanceof Player)
            ((Player) playerIn).getCooldowns().addCooldown(this, 5);
        return did;
    }
}
