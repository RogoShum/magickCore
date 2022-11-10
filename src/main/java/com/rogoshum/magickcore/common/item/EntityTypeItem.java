package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.common.api.mana.ISpellContext;
import com.rogoshum.magickcore.common.api.mana.IManaMaterial;
import com.rogoshum.magickcore.common.api.entity.IManaEntity;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.common.api.enums.ApplyType;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.util.ExtraDataUtil;
import com.rogoshum.magickcore.common.init.ModGroup;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class EntityTypeItem extends ManaItem implements IManaMaterial {
    private static final HashSet<EntityType<?>> ERROR_TYPE = new HashSet<>();
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
        ExtraDataUtil.itemManaData(itemStack, (data) -> {
            data.spellContext().addChild(SpawnContext.create(entityType));
            data.spellContext().applyType(ApplyType.SPAWN_ENTITY);
        });
        items.add(itemStack);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        ItemStack sample = new ItemStack(this);
        ExtraDataUtil.itemManaData(sample, (data) -> data.spellContext().applyType(ApplyType.SPAWN_ENTITY));
        if (group == ModGroup.ENTITY_TYPE_GROUP) {
            List<EntityType<? extends LivingEntity>> livings = new ArrayList<>();
            List<EntityType<? extends IManaEntity>> mana = new ArrayList<>();
            ForgeRegistries.ENTITIES.getEntries().forEach(type -> {
                if(!ERROR_TYPE.contains(type.getValue())) {
                    try {
                        Entity entity = type.getValue().create(RenderHelper.getPlayer().world);
                        if(entity instanceof LivingEntity)
                            livings.add((EntityType<? extends LivingEntity>) type.getValue());
                        else if(entity instanceof IManaEntity)
                            mana.add((EntityType<? extends IManaEntity>) type.getValue());
                    } catch (Exception e) {
                        ERROR_TYPE.add(type.getValue());
                    }
                }
            });
            mana.forEach(type -> fillEntity(items, sample, type));
            livings.forEach(type -> fillEntity(items, sample, type));
        }
    }

    @Override
    public int getManaNeed(ItemStack stack) {
        SpellContext item = ExtraDataUtil.itemManaData(stack).spellContext();
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
        spellContext.merge(ExtraDataUtil.itemManaData(stack).spellContext());
        return true;
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        if(playerIn.world.isRemote) return false;
        SpellContext item = ExtraDataUtil.itemManaData(stack).spellContext();
        MagickReleaseHelper.releaseMagick(MagickContext.create(playerIn.world, item).caster(playerIn).tick(200).force(10.0f).range(10f).addChild(new TraceContext()));
        return false;
    }
}
