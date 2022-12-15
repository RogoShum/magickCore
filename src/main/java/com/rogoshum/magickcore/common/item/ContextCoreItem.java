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
        super(properties().maxStackSize(8).setISTER(() -> ManaEnergyRenderer::new));
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if(entity.ticksExisted > 20) {
            boolean upGround = true;
            for (int i = 0; i < 2; ++i) {
                if(!entity.world.isAirBlock(entity.getPosition().add(0, -i, 0)))
                    upGround = false;
            }
            double speed = 0.043;
            if(!upGround)
                entity.addVelocity(0, speed, 0);
            else {
                entity.remove();
                if(!entity.world.isRemote) {
                    ContextCreatorEntity contextCreator = ModEntities.CONTEXT_CREATOR.get().create(entity.world);
                    contextCreator.setPosition(entity.getPosX(), entity.getPosY() - 0.5, entity.getPosZ());
                    if(stack.hasTag() && stack.getTag().contains("mana_material")) {
                        String material = stack.getTag().getString("mana_material");
                        Material manaMaterial = ManaMaterials.getMaterial(material);
                        if(manaMaterial != ManaMaterials.NONE) {
                            contextCreator.getInnerManaData().setMaterial(manaMaterial);
                        }
                    }
                    entity.world.addEntity(contextCreator);
                    entity.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE, 0.5f, 2.0f);
                }
            }
        }

        return false;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
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
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        Material material = ManaMaterials.getMaterial(LibMaterial.ORIGIN);
        if(stack.hasTag() && stack.getTag().contains("mana_material")) {
            Material manaMaterial = ManaMaterials.getMaterial(stack.getTag().getString("mana_material"));
            if(manaMaterial != ManaMaterials.NONE) {
                material = manaMaterial;
            }
        }
        tooltip.add((new TranslationTextComponent(LibItem.MATERIAL).mergeStyle(TextFormatting.BLUE)).appendString(" ").append(new TranslationTextComponent(MagickCore.MOD_ID + ".material." + material.getName()).mergeStyle(TextFormatting.GRAY)));
        tooltip.add((new TranslationTextComponent(LibItem.FORCE).mergeStyle(TextFormatting.BLUE)).appendString(" ").append(new StringTextComponent(String.valueOf(material.getForce())).mergeStyle(TextFormatting.GRAY)));
        tooltip.add((new TranslationTextComponent(LibItem.RANGE).mergeStyle(TextFormatting.BLUE)).appendString(" ").append(new StringTextComponent(String.valueOf(material.getRange())).mergeStyle(TextFormatting.GRAY)));
        tooltip.add((new TranslationTextComponent(LibItem.TICK).mergeStyle(TextFormatting.BLUE)).appendString(" ").append(new StringTextComponent(String.valueOf(material.getTick() / 20)).mergeStyle(TextFormatting.GRAY)));
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(p_77663_3_ instanceof ServerPlayerEntity) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayerEntity) p_77663_3_, LibAdvancements.CONTEXT_CORE);
        }
    }
}
