package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.api.magick.context.child.DimensionContext;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.api.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.init.ModItems;
import com.rogoshum.magickcore.common.init.ModSounds;
import com.rogoshum.magickcore.common.item.BaseItem;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.api.magick.context.child.PositionContext;
import com.rogoshum.magickcore.api.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

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
    public boolean singleMaterial() {
        return true;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if(entity.isInWater() && stack.hasTag() && NBTTagHelper.hasVectorDouble(stack.getTag(), "position")) {
            ItemStack stack1 = new ItemStack(ModItems.MAGICK_CORE.get());
            ItemManaData data = ExtraDataUtil.itemManaData(stack1);
            Vec3 vector3d = NBTTagHelper.getVectorFromNBT(stack.getTag(), "position");
            data.spellContext().addChild(PositionContext.create(vector3d));
            data.spellContext().addChild(DimensionContext.create(new ResourceLocation(stack.getTag().getString("dimension"))));
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
            Vec3 vector3d = NBTTagHelper.getVectorFromNBT(stack.getTag(), "position");
            data.spellContext().addChild(PositionContext.create(vector3d));
            data.spellContext().addChild(DimensionContext.create(new ResourceLocation(stack.getTag().getString("dimension"))));
            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent(LibItem.CONTEXT_MATERIAL));
        if(stack.hasTag() && NBTTagHelper.hasVectorDouble(stack.getTag(), "position"))
            tooltip.add(new TextComponent(
                    new TranslatableComponent(MagickCore.MOD_ID + ".description." + LibContext.POSITION).getString()
                    + " " + NBTTagHelper.getVectorFromNBT(stack.getTag(), "position")
            ));
        if(stack.hasTag() && stack.getTag().contains("dimension")) {
            ResourceLocation res = new ResourceLocation(stack.getTag().getString("dimension"));
            tooltip.add(new TextComponent(
                    new TranslatableComponent(MagickCore.MOD_ID + ".description." + LibContext.DIMENSION).getString()
                            + " " + new TranslatableComponent(res.toString()).getString()));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        NBTTagHelper.putVectorDouble(context.getItemInHand().getOrCreateTag(), "position", Vec3.atCenterOf(context.getClickedPos()));
        context.getItemInHand().getOrCreateTag().putString("dimension", context.getLevel().dimension().location().toString());
        context.getLevel().playSound(null, context.getClickedPos(), ModSounds.soft_buildup.get(), SoundSource.NEUTRAL, 0.15f, 1.0f);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if(playerIn.isShiftKeyDown() && itemstack.hasTag()) {
            if(NBTTagHelper.hasVectorDouble(itemstack.getTag(), "position"))
                NBTTagHelper.removeVectorDouble(itemstack.getTag(), "position");
            itemstack.removeTagKey("dimension");
            return InteractionResultHolder.success(itemstack);
        }
        return super.use(worldIn, playerIn, handIn);
    }
}
