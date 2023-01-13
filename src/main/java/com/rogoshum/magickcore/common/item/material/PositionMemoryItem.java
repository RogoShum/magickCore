package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
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
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if(entity.isInWater() && stack.hasTag() && NBTTagHelper.hasVectorDouble(stack.getTag(), "position")) {
            ItemStack stack1 = new ItemStack(ModItems.MAGICK_CORE.get());
            ItemManaData data = ExtraDataUtil.itemManaData(stack1);
            Vector3d vector3d = NBTTagHelper.getVectorFromNBT(stack.getTag(), "position");
            data.spellContext().addChild(PositionContext.create(vector3d));
            data.spellContext().addChild(SpawnContext.create(ModEntities.SQUARE.get()));
            data.spellContext().applyType(ApplyType.SPAWN_ENTITY);
            entity.setItem(stack1);
        }
        return super.onEntityItemUpdate(stack, entity);
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
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(LibItem.CONTEXT_MATERIAL));
        if(stack.hasTag() && NBTTagHelper.hasVectorDouble(stack.getTag(), "position"))
            tooltip.add(new StringTextComponent(
                    new TranslationTextComponent(MagickCore.MOD_ID + ".description." + LibContext.POSITION).getString()
                    + " " + NBTTagHelper.getVectorFromNBT(stack.getTag(), "position")
            ));
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        NBTTagHelper.putVectorDouble(context.getItemInHand().getOrCreateTag(), "position", Vector3d.atCenterOf(context.getClickedPos()));
        context.getLevel().playSound(null, context.getClickedPos(), ModSounds.soft_buildup.get(), SoundCategory.NEUTRAL, 0.15f, 1.0f);
        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if(playerIn.isShiftKeyDown() && itemstack.hasTag()) {
            if(NBTTagHelper.hasVectorDouble(itemstack.getTag(), "position"))
                NBTTagHelper.removeVectorDouble(itemstack.getTag(), "position");
            return ActionResult.success(itemstack);
        }
        return super.use(worldIn, playerIn, handIn);
    }
}
