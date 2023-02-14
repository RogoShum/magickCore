package com.rogoshum.magickcore.common.item.tool;

import com.rogoshum.magickcore.api.enums.ManaLimit;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.enums.ParticleType;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.magick.MagickElement;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.util.ParticleUtil;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class SuperItem extends BaseItem {
    public SuperItem() {
        super(properties().stacksTo(1));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity playerIn) {
        if(!worldIn.isClientSide) {
            EntityStateData state = ExtraDataUtil.entityStateData(playerIn);
            float mana = state.getManaValue();
            if(playerIn instanceof Player && ((Player) playerIn).isCreative())
                mana = ManaLimit.MAX_MANA.getValue();
            MagickElement element = ExtraDataUtil.entityStateData(playerIn).getElement();
            MagickContext context = MagickContext.create(worldIn).caster(playerIn).noCost().applyType(ApplyType.SUPER).tick((int) mana).element(element);
            boolean success = MagickReleaseHelper.releaseMagick(context);
            if(success) {
                state.setManaValue(0);
                ParticleUtil.spawnBlastParticle(playerIn.level, playerIn.position().add(0, playerIn.getBbHeight() * 0.5, 0), 5, state.getElement(), ParticleType.PARTICLE);
            }
        }
        return super.finishUsingItem(stack, worldIn, playerIn);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        playerIn.startUsingItem(handIn);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 30;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent(LibItem.SUPER_D));
    }
}
