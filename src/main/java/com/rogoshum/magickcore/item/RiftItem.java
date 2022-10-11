package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.init.ModEntities;
import com.rogoshum.magickcore.lib.LibItem;
import com.rogoshum.magickcore.magick.context.child.PositionContext;
import com.rogoshum.magickcore.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
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

public class RiftItem extends BaseItem{
    public RiftItem() {
        super(BaseItem.properties().maxStackSize(8));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        playerIn.setActiveHand(handIn);
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity playerIn) {
        if(!worldIn.isRemote) {
            EntityStateData state = ExtraDataHelper.entityStateData(playerIn);
            MagickContext magickContext = MagickContext.create(worldIn).<MagickContext>applyType(EnumApplyType.SPAWN_ENTITY)
                    .caster(playerIn).element(state.getElement()).tick(10000).force(Math.min(state.getManaValue() / 200f, 1f)).tick((int) Math.min(state.getManaValue(), 900));
            magickContext.addChild(PositionContext.create(playerIn.getPositionVec().add(0, 0, 0)));
            //magickContext.addChild(SpawnContext.create(EnumApplyType.NONE, ModEntities.mana_rift.get()));
            MagickReleaseHelper.releaseMagick(magickContext);
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
