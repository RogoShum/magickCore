package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.api.itemstack.IManaData;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ManaItem extends BaseItem implements IManaData {
    public ManaItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        playerIn.setActiveHand(handIn);
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        AtomicInteger i = new AtomicInteger(MathHelper.hsvToRGB(0.0f, 0.0F, 1.0F));
        ExtraDataUtil.itemData(stack).<ItemManaData>execute(LibRegistry.ITEM_DATA, data -> {
            com.rogoshum.magickcore.common.magick.Color color = data.spellContext().element.getRenderer().getColor();
            if(color.equals(com.rogoshum.magickcore.common.magick.Color.ORIGIN_COLOR) && RenderHelper.getPlayer() != null) {
                color = ExtraDataUtil.entityStateData(RenderHelper.getPlayer()).getElement().color();
            }
            float[] hsv = Color.RGBtoHSB((int)(color.r() * 255), (int)(color.g() * 255), (int)(color.b() * 255), null);

            i.set(MathHelper.hsvToRGB(hsv[0], hsv[1], hsv[2]));
        });

        return i.get();
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        return 1f - data.manaCapacity().getMana() / data.manaCapacity().getMaxMana();
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return super.getRarity(stack);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        AtomicReference<TranslationTextComponent> transText = new AtomicReference<>(new TranslationTextComponent(this.getTranslationKey(stack)));
        /*
        ExtraDataHelper.itemData(stack).<ItemManaData>execute(LibRegistry.ITEM_DATA, data -> {
            transText.set(new TranslationTextComponent(MagickCore.MOD_ID + ".material." + data.spellContext().getMaterial().getName()).appendString(" ").append(new TranslationTextComponent(super.getTranslationKey(stack))));
        });
        */
        return transText.get();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        ExtraDataUtil.itemData(stack).<ItemManaData>execute(LibRegistry.ITEM_DATA, data -> {
            String information = data.spellContext().toString();
            if(!information.isEmpty()) {
                String[] tips = information.split("\n");
                for (String tip : tips) {
                    tooltip.add(new StringTextComponent(tip));
                }
            }
        });
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        /*if (this.isInGroup(group)) {
            ItemStack stack = new ItemStack(this);

        }

         */
        super.fillItemGroup(group, items);
    }

    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        CompoundNBT nbt = super.getShareTag(stack);
        /*if(nbt == null)
            nbt = new CompoundNBT();
        CompoundNBT tag = new CompoundNBT();
        ExtraDataUtil.itemData(stack).<ItemManaData>execute(LibRegistry.ITEM_DATA, data -> data.write(tag));
        nbt.put(ItemExtraData.ITEM_DATA, tag);


         */
        return nbt;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {


        super.readShareTag(stack, nbt);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        //updateData(stack, entityIn, itemSlot);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        //updateData(stack, entity, 0);
        return super.onEntityItemUpdate(stack, entity);
    }

    private void updateData(ItemStack stack, Entity entityIn, int slot) {
        /*
        if(!entityIn.world.isRemote())
        {
            IManaCapacity data = (IManaCapacity)stack.getItem();
            Networking.INSTANCE.send(
                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entityIn),
                    new ManaItemDataPack(entityIn.getEntityId(), slot, data.getElement(stack).getType(), data.getTrace(stack)
                            , data.getManaType(stack).getLabel(), data.getRange(stack), data.getForce(stack)
                            , data.getMana(stack), data.getTickTime(stack)));
        }
        *
         */
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 10;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        ExtraDataUtil.entityData(entityLiving).<EntityStateData>execute(LibEntityData.ENTITY_STATE, data -> releaseMagick(entityLiving, data, stack));

        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }

    public abstract boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack);
}
