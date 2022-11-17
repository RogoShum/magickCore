package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.client.particle.LitParticle;
import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.api.mana.IManaContextItem;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;

public class SpiritSwordItem extends ManaItem implements IManaContextItem {
    public SpiritSwordItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        return false;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 114514;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        EntityStateData state = ExtraDataUtil.entityStateData(player);
        state.setMaxElementShieldMana((Math.min(count, 20)));
        super.onUsingTick(stack, player, count);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        EntityStateData state = ExtraDataUtil.entityStateData(player);
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        MagickContext magickContext = MagickContext.create(player.world, data.spellContext());
        MagickElement element = data.manaCapacity().getMana() > 0 ? data.spellContext().element : state.getElement();
        MagickContext context = magickContext.caster(player).victim(entity).element(element);
        MagickReleaseHelper.releaseMagick(MagickContext.create(player.world, context).applyType(ApplyType.HIT_ENTITY));
        return MagickReleaseHelper.releaseMagick(context);
    }
}