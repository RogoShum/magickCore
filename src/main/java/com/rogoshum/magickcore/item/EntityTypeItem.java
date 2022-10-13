package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.api.ISpellContext;
import com.rogoshum.magickcore.api.IManaMaterial;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.enums.EnumApplyType;
import com.rogoshum.magickcore.init.ModEntities;
import com.rogoshum.magickcore.init.ModGroup;
import com.rogoshum.magickcore.lib.LibContext;
import com.rogoshum.magickcore.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.magick.context.MagickContext;
import com.rogoshum.magickcore.magick.context.SpellContext;
import com.rogoshum.magickcore.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.magick.context.child.TraceContext;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.tool.ExtraDataHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTypeItem extends ManaItem implements IManaMaterial {
    public EntityTypeItem() {
        super(properties().setISTER(() -> ManaEnergyRenderer::new));
    }

    @Override
    public boolean disappearAfterRead() {
        return false;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    public void fillEntity(NonNullList<ItemStack> items, ItemStack stack, EntityType<?> entityType) {
        ItemStack itemStack = stack.copy();
        ExtraDataHelper.itemManaData(itemStack, (data) -> data.spellContext().addChild(SpawnContext.create(entityType)));
        items.add(itemStack);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        ItemStack sample = new ItemStack(this);
        ExtraDataHelper.itemManaData(sample, (data) -> data.spellContext().applyType(EnumApplyType.SPAWN_ENTITY));
        if (group == ModGroup.entityTypeGroup) {
            ForgeRegistries.ENTITIES.getEntries().forEach(type -> {
                if(type.getValue().create(Minecraft.getInstance().world) instanceof LivingEntity)
                    fillEntity(items, sample, type.getValue());
            });
        } else if (group == ModGroup.manaEntityTypeGroup) {
            ForgeRegistries.ENTITIES.getEntries().forEach(type -> {
                if(type.getValue().create(Minecraft.getInstance().world) instanceof IManaEntity)
                    fillEntity(items, sample, type.getValue());
            });
        }
    }

    @Override
    public int getManaNeed(ItemStack stack) {
        SpellContext item = ExtraDataHelper.itemManaData(stack).spellContext();
        if(item.containChild(LibContext.SPAWN)) {
            SpawnContext spawnContext = item.getChild(LibContext.SPAWN);
            EntityType<?> type = spawnContext.entityType;
            if(type == null)
                return 0;
            return (int)(500 * (type.getHeight() + type.getWidth()));
        }
        return 0;
    }

    @Override
    public boolean upgradeManaItem(ItemStack stack, ISpellContext data) {
        SpellContext spellContext = data.spellContext();
        spellContext.merge(ExtraDataHelper.itemManaData(stack).spellContext());
        return true;
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        if(playerIn.world.isRemote) return false;
        SpellContext item = ExtraDataHelper.itemManaData(stack).spellContext();
        MagickReleaseHelper.releaseMagick(MagickContext.create(playerIn.world, item).caster(playerIn).tick(100).force(5.0f).range(5f).addChild(new TraceContext()));
        return false;
    }
}
