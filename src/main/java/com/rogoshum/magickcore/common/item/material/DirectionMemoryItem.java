package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
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

public class DirectionMemoryItem extends BaseItem implements IManaMaterial {
    public DirectionMemoryItem(Properties properties) {
        super(properties);
    }

    public DirectionMemoryItem() {
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
        if(stack.hasTag() && NBTTagHelper.hasVectorDouble(stack.getTag(), "direction")) {
            Vector3d vector3d = NBTTagHelper.getVectorFromNBT(stack.getTag(), "direction");
            data.spellContext().addChild(DirectionContext.create(vector3d));
            return true;
        }
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(stack.hasTag()) {
            if(NBTTagHelper.hasVectorDouble(stack.getTag(), "position"))
                tooltip.add(new StringTextComponent(
                        new TranslationTextComponent(MagickCore.MOD_ID + ".description." + LibContext.POSITION).getString()
                                + " " + NBTTagHelper.getVectorFromNBT(stack.getTag(), "position")
                ));
            else if(NBTTagHelper.hasVectorDouble(stack.getTag(), "direction"))
            tooltip.add(new StringTextComponent(
                    new TranslationTextComponent(MagickCore.MOD_ID + ".description." + LibContext.DIRECTION).getString()
                            + " " + NBTTagHelper.getVectorFromNBT(stack.getTag(), "direction")
            ));
        }
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        addPosition(context.getItem(), Vector3d.copyCentered(context.getPos()));
        return ActionResultType.SUCCESS;
    }

    public void addPosition(ItemStack stack, Vector3d vec) {
        if(stack.hasTag()) {
            if(NBTTagHelper.hasVectorDouble(stack.getTag(), "position")) {
                Vector3d fir = NBTTagHelper.getVectorFromNBT(stack.getTag(), "position");
                NBTTagHelper.putVectorDouble(stack.getTag(), "direction", vec.subtract(fir).normalize());
                NBTTagHelper.removeVectorDouble(stack.getTag(), "position");
            } else
                NBTTagHelper.putVectorDouble(stack.getTag(), "position", vec);
            if(NBTTagHelper.hasVectorDouble(stack.getTag(), "direction"))
                NBTTagHelper.removeVectorDouble(stack.getTag(), "direction");
        } else
            NBTTagHelper.putVectorDouble(stack.getOrCreateTag(), "position", vec);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        Vector3d dir = playerIn.getLookVec();
        Entity entity = MagickReleaseHelper.getEntityLookedAt(playerIn, 4);
        if(entity != null) {
            dir = entity.getPositionVec().add(0, entity.getHeight() * 0.5, 0);
            addPosition(itemstack, dir);
        } else
            NBTTagHelper.putVectorDouble(itemstack.getOrCreateTag(), "direction", dir);
        return ActionResult.resultSuccess(itemstack);
    }
}
