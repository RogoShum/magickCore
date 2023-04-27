package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.api.render.RenderHelper;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.lib.LibAdvancements;
import com.rogoshum.magickcore.api.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.init.ModGroups;
import com.rogoshum.magickcore.api.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.api.extradata.ExtraDataUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class MagickContextItem extends ManaItem {
    public MagickContextItem() {
        super(properties().stacksTo(16));
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
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        return false;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if(group == ModGroups.MAGICK_CONTEXT_GROUP) {
            ItemStack sample = new ItemStack(this);
            for (ApplyType type : ApplyType.values()) {
                if(type == ApplyType.NONE || type == ApplyType.SPAWN_ENTITY || type == ApplyType.POTION || type == ApplyType.HIT_BLOCK || type == ApplyType.HIT_ENTITY || type == ApplyType.ELEMENT_TOOL || type == ApplyType.SUPER)continue;
                ExtraDataUtil.itemManaData(sample, (data) -> {
                    data.spellContext().applyType(type).force(7).range(7).tick(300);
                });
                ItemStack itemStack = sample.copy();
                items.add(itemStack);
            }
            ForgeRegistries.ENTITIES.getEntries().forEach(entityType -> {
                if(entityType.getValue().create(RenderHelper.getPlayer().level) instanceof IManaEntity)
                    fillEntity(items, sample, entityType.getValue());
            });
        }
    }

    public void fillEntity(NonNullList<ItemStack> items, ItemStack stack, EntityType<?> entityType) {
        ItemStack itemStack = stack.copy();
        ExtraDataUtil.itemManaData(itemStack, (data) -> {
            data.spellContext().addChild(SpawnContext.create(entityType));
            data.spellContext().applyType(ApplyType.SPAWN_ENTITY);
        });
        items.add(itemStack);
    }

    @Override
    public void inventoryTick(ItemStack p_77663_1_, Level p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(p_77663_3_ instanceof ServerPlayer) {
            AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayer) p_77663_3_, LibAdvancements.MAGICK_CORE);
        }
    }
}
