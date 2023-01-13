package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.common.entity.pointed.ContextCreatorEntity;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.init.ManaMaterials;
import com.rogoshum.magickcore.common.init.ModEntities;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.lib.LibMaterial;
import com.rogoshum.magickcore.common.magick.materials.Material;
import com.rogoshum.magickcore.common.util.NBTTagHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ContextCoreItem extends BaseItem{
    public ContextCoreItem() {
        super(properties().stacksTo(8).setISTER(() -> ManaEnergyRenderer::new));
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if(entity.tickCount > 20) {
            boolean upGround = true;
            for (int i = 0; i < 2; ++i) {
                if(!entity.level.isEmptyBlock(entity.blockPosition().offset(0, -i, 0)))
                    upGround = false;
            }
            double speed = 0.043;
            if(!upGround)
                entity.push(0, speed, 0);
            else {
                entity.remove();
                if(!entity.level.isClientSide) {
                    ContextCreatorEntity contextCreator = ModEntities.CONTEXT_CREATOR.get().create(entity.level);
                    contextCreator.setPos(entity.getX(), entity.getY() - 0.5, entity.getZ());
                    if(stack.hasTag() && stack.getTag().contains("mana_material")) {
                        String material = stack.getTag().getString("mana_material");
                        Material manaMaterial = ManaMaterials.getMaterial(material);
                        if(manaMaterial != ManaMaterials.NONE) {
                            contextCreator.getInnerManaData().setMaterial(manaMaterial);
                        }
                    }
                    entity.level.addFreshEntity(contextCreator);
                    entity.playSound(SoundEvents.BEACON_ACTIVATE, 0.5f, 2.0f);
                }
            }
        }

        return false;
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            ItemStack sample = new ItemStack(this);
            ManaMaterials.getMaterials().keySet().forEach((key) -> {
                if(!Objects.equals(key, "origin")) {
                    ItemStack copy = sample.copy();
                    copy.getOrCreateTag().putString("mana_material", key);
                    items.add(copy);
                }
            });
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        Material material = ManaMaterials.getMaterial(LibMaterial.ORIGIN);
        if(stack.hasTag() && stack.getTag().contains("mana_material")) {
            Material manaMaterial = ManaMaterials.getMaterial(stack.getTag().getString("mana_material"));
            if(manaMaterial != ManaMaterials.NONE) {
                material = manaMaterial;
            }
        }
        tooltip.add((new TranslationTextComponent(LibItem.MATERIAL).withStyle(TextFormatting.BLUE)).append(" ").append(new TranslationTextComponent(MagickCore.MOD_ID + ".material." + material.getName()).withStyle(TextFormatting.GRAY)));
        tooltip.add((new TranslationTextComponent(LibItem.FORCE).withStyle(TextFormatting.BLUE)).append(" ").append(new StringTextComponent(String.valueOf(material.getForce())).withStyle(TextFormatting.GRAY)));
        tooltip.add((new TranslationTextComponent(LibItem.RANGE).withStyle(TextFormatting.BLUE)).append(" ").append(new StringTextComponent(String.valueOf(material.getRange())).withStyle(TextFormatting.GRAY)));
        tooltip.add((new TranslationTextComponent(LibItem.TICK).withStyle(TextFormatting.BLUE)).append(" ").append(new StringTextComponent(String.valueOf(material.getTick() / 20)).withStyle(TextFormatting.GRAY)));
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(p_77663_3_ instanceof ServerPlayerEntity) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayerEntity) p_77663_3_, LibAdvancements.CONTEXT_CORE);
        }
    }
}
