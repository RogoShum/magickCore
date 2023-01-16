package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.child.DirectionContext;
import com.rogoshum.magickcore.common.magick.context.child.PositionContext;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

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
            Vec3 vector3d = NBTTagHelper.getVectorFromNBT(stack.getTag(), "direction");
            data.spellContext().addChild(DirectionContext.create(vector3d));
            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent(LibItem.CONTEXT_MATERIAL));
        if(stack.hasTag()) {
            if(NBTTagHelper.hasVectorDouble(stack.getTag(), "position"))
                tooltip.add(new TextComponent(
                        new TranslatableComponent(MagickCore.MOD_ID + ".description." + LibContext.POSITION).getString()
                                + " " + NBTTagHelper.getVectorFromNBT(stack.getTag(), "position")
                ));
            else if(NBTTagHelper.hasVectorDouble(stack.getTag(), "direction"))
            tooltip.add(new TextComponent(
                    new TranslatableComponent(MagickCore.MOD_ID + ".description." + LibContext.DIRECTION).getString()
                            + " " + NBTTagHelper.getVectorFromNBT(stack.getTag(), "direction")
            ));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        addPosition(context.getItemInHand(), Vec3.atCenterOf(context.getClickedPos()));
        context.getLevel().playSound(null, context.getClickedPos(), ModSounds.soft_buildup.get(), SoundSource.NEUTRAL, 0.15f, 1.0f);
        return InteractionResult.SUCCESS;
    }

    public void addPosition(ItemStack stack, Vec3 vec) {
        if(stack.hasTag()) {
            if(NBTTagHelper.hasVectorDouble(stack.getTag(), "position")) {
                Vec3 fir = NBTTagHelper.getVectorFromNBT(stack.getTag(), "position");
                NBTTagHelper.putVectorDouble(stack.getTag(), "direction", vec.subtract(fir).normalize());
                NBTTagHelper.removeVectorDouble(stack.getTag(), "position");
            } else {
                NBTTagHelper.putVectorDouble(stack.getTag(), "position", vec);
                if(NBTTagHelper.hasVectorDouble(stack.getTag(), "direction"))
                    NBTTagHelper.removeVectorDouble(stack.getTag(), "direction");
            }
        } else
            NBTTagHelper.putVectorDouble(stack.getOrCreateTag(), "position", vec);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if(playerIn.isShiftKeyDown() && itemstack.hasTag()) {
            if(NBTTagHelper.hasVectorDouble(itemstack.getTag(), "direction"))
                NBTTagHelper.removeVectorDouble(itemstack.getTag(), "direction");
            if(NBTTagHelper.hasVectorDouble(itemstack.getTag(), "position"))
                NBTTagHelper.removeVectorDouble(itemstack.getTag(), "position");
            return InteractionResultHolder.success(itemstack);
        }
        Vec3 dir = playerIn.getLookAngle();
        Entity entity = MagickReleaseHelper.getEntityLookedAt(playerIn, 4);
        if(entity != null) {
            dir = entity.position().add(0, entity.getBbHeight() * 0.5, 0);
            addPosition(itemstack, dir);
        } else
            NBTTagHelper.putVectorDouble(itemstack.getOrCreateTag(), "direction", dir);
        worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), ModSounds.soft_buildup.get(), SoundSource.NEUTRAL, 0.15f, 1.0f);
        return InteractionResultHolder.success(itemstack);
    }
}
