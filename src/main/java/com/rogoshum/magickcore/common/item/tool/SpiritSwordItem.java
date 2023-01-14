package com.rogoshum.magickcore.common.item.tool;

import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.mana.IManaContextItem;
import com.rogoshum.magickcore.common.item.ManaItem;
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
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import net.minecraft.item.Item.Properties;

public class SpiritSwordItem extends ManaItem implements IManaContextItem {
    public SpiritSwordItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        return false;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return ActionResult.pass(playerIn.getItemInHand(handIn));
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        MagickContext magickContext = MagickContext.create(player.level, data.spellContext());
        MagickElement element = data.spellContext().element;
        MagickContext context = magickContext.caster(player).victim(entity).element(element);
        MagickReleaseHelper.releaseMagick(MagickContext.create(player.level, context).caster(player).victim(entity).noCost().element(element).applyType(ApplyType.HIT_ENTITY));
        Hand hand = player.getMainHandItem() == stack ? Hand.MAIN_HAND : Hand.OFF_HAND;
        player.startUsingItem(hand);
        boolean success = MagickReleaseHelper.releaseMagick(context);
        player.releaseUsingItem();
        return success;
    }
}
