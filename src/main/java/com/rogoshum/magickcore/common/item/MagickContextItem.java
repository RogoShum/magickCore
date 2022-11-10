package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.api.entity.IManaEntity;
import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.common.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.init.ModGroup;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.util.ExtraDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.registries.ForgeRegistries;

public class MagickContextItem extends ManaItem{
    public MagickContextItem() {
        super(properties().maxStackSize(16).setISTER(() -> ManaEnergyRenderer::new));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        return false;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if(group == ModGroup.MAGICK_CONTEXT_GROUP) {
            ItemStack sample = new ItemStack(this);
            for (ApplyType type : ApplyType.values()) {
                if(type == ApplyType.NONE || type == ApplyType.SPAWN_ENTITY) continue;
                ExtraDataUtil.itemManaData(sample, (data) -> data.spellContext().applyType(type).force(10).range(5).tick(300));
                ItemStack itemStack = sample.copy();
                items.add(itemStack);
            }
            ForgeRegistries.ENTITIES.getEntries().forEach(entityType -> {
                if(entityType.getValue().create(RenderHelper.getPlayer().world) instanceof IManaEntity)
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
}
