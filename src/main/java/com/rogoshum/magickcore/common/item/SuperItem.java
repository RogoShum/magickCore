package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.common.api.enums.ManaLimit;
import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.lib.LibItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SuperItem extends BaseItem {
    public SuperItem() {
        super(properties().maxStackSize(1));
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity playerIn) {
        if(!worldIn.isRemote) {
            EntityStateData state = ExtraDataUtil.entityStateData(playerIn);
            float mana = state.getManaValue();
            if(playerIn instanceof PlayerEntity && ((PlayerEntity) playerIn).isCreative())
                mana = ManaLimit.MAX_MANA.getValue();
            MagickElement element = ExtraDataUtil.entityStateData(playerIn).getElement();
            MagickContext context = MagickContext.create(worldIn).caster(playerIn).noCost().applyType(ApplyType.SUPER).tick((int) mana).element(element);
            MagickReleaseHelper.releaseMagick(context);
            state.setManaValue(0);
        }
        return super.onItemUseFinish(stack, worldIn, playerIn);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        playerIn.setActiveHand(handIn);
        return ActionResult.resultConsume(itemstack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 30;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(LibItem.SUPER_D));
    }
}
