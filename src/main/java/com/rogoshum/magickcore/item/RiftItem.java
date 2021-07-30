package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.enums.EnumManaType;
import com.rogoshum.magickcore.enums.EnumTargetType;
import com.rogoshum.magickcore.capability.IEntityState;
import com.rogoshum.magickcore.entity.ManaRuneEntity;
import com.rogoshum.magickcore.helper.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModEntites;
import com.rogoshum.magickcore.lib.LibItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
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

public class RiftItem extends BaseItem{
    public RiftItem() {
        super(BaseItem.properties.maxStackSize(8));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        playerIn.setActiveHand(handIn);
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity playerIn) {
        if(!worldIn.isRemote) {
            List<Entity> list = playerIn.world.getEntitiesWithinAABB(ManaRuneEntity.class, playerIn.getBoundingBox().grow(16));
            IEntityState state = playerIn.getCapability(MagickCore.entityState).orElse(null);
            if(list.size() >= 1) {
                MagickReleaseHelper.releasePointEntity(ModEntites.mana_rift, playerIn, list.get(0).getPositionVec().add(0, 0, 0), state.getElement(), null
                        , Math.min(state.getManaValue() / 200f, 1f), (int) Math.min(state.getManaValue(), 900)
                        , 0, EnumTargetType.NONE, EnumManaType.NONE);
                list.get(0).remove();
                playerIn.getActiveItemStack().shrink(1);
            }
        }
        return super.onItemUseFinish(stack, worldIn, playerIn);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 5;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(LibItem.RIFT_D));
    }
}
