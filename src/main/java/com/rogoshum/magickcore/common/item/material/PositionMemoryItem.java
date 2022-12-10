package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class PositionMemoryItem extends BaseItem implements IManaMaterial {
    public PositionMemoryItem() {
        super(properties());
    }

    @Override
    public boolean disappearAfterRead() {
        return false;
    }

    @Override
    public int getManaNeed(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean upgradeManaItem(ItemStack stack, ISpellContext data) {
        if(stack.hasTag() && NBTTagHelper.hasVectorDouble(stack.getTag(), "position")) {
            Vector3d vector3d = NBTTagHelper.getVectorFromNBT(stack.getTag(), "position");
            data.spellContext().addChild(PositionContext.create(vector3d));
            return true;
        }
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(stack.hasTag() && NBTTagHelper.hasVectorDouble(stack.getTag(), "position"))
            tooltip.add(new StringTextComponent(
                    new TranslationTextComponent(MagickCore.MOD_ID + ".description." + LibContext.POSITION).getString()
                    + " " + NBTTagHelper.getVectorFromNBT(stack.getTag(), "position")
            ));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        NBTTagHelper.putVectorDouble(context.getItem().getOrCreateTag(), "position", Vector3d.copyCentered(context.getPos()));
        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if(playerIn.isSneaking() && itemstack.hasTag()) {
            if(NBTTagHelper.hasVectorDouble(itemstack.getTag(), "position"))
                NBTTagHelper.removeVectorDouble(itemstack.getTag(), "position");
            return ActionResult.resultSuccess(itemstack);
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
