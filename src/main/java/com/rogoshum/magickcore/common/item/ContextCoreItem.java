package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.MagickCore;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.client.item.OrbBottleRenderer;
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
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ContextCoreItem extends BaseItem{
    public ContextCoreItem() {
        super(properties().stacksTo(8));
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return new ManaEnergyRenderer();
            }
        });
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
                entity.remove(Entity.RemovalReason.DISCARDED);
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
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
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
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        Material material = ManaMaterials.getMaterial(LibMaterial.ORIGIN);
        if(stack.hasTag() && stack.getTag().contains("mana_material")) {
            Material manaMaterial = ManaMaterials.getMaterial(stack.getTag().getString("mana_material"));
            if(manaMaterial != ManaMaterials.NONE) {
                material = manaMaterial;
            }
        }
        tooltip.add((new TranslatableComponent(LibItem.MATERIAL).withStyle(ChatFormatting.BLUE)).append(" ").append(new TranslatableComponent(MagickCore.MOD_ID + ".material." + material.getName()).withStyle(ChatFormatting.GRAY)));
        tooltip.add((new TranslatableComponent(LibItem.FORCE).withStyle(ChatFormatting.BLUE)).append(" ").append(new TextComponent(String.valueOf(material.getForce())).withStyle(ChatFormatting.GRAY)));
        tooltip.add((new TranslatableComponent(LibItem.RANGE).withStyle(ChatFormatting.BLUE)).append(" ").append(new TextComponent(String.valueOf(material.getRange())).withStyle(ChatFormatting.GRAY)));
        tooltip.add((new TranslatableComponent(LibItem.TICK).withStyle(ChatFormatting.BLUE)).append(" ").append(new TextComponent(String.valueOf(material.getTick() / 20)).withStyle(ChatFormatting.GRAY)));
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, Level p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(p_77663_3_ instanceof ServerPlayer) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayer) p_77663_3_, LibAdvancements.CONTEXT_CORE);
        }
    }
}
