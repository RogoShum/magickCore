package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.event.AdvancementsEvent;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.init.ModGroups;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
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
        if (group == ModGroups.ENTITY_TYPE_GROUP) {
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
        spellContext.applyType(ApplyType.SPAWN_ENTITY);
        spellContext.merge(ExtraDataUtil.itemManaData(stack).spellContext());
        return true;
    }

    @Override
    public boolean releaseMagick(LivingEntity playerIn, EntityStateData state, ItemStack stack) {
        if(playerIn.world.isRemote) return false;
        if(playerIn instanceof PlayerEntity && !((PlayerEntity) playerIn).isCreative()) return false;
        SpellContext item = ExtraDataUtil.itemManaData(stack).spellContext();
        MagickReleaseHelper.releaseMagick(MagickContext.create(playerIn.world, item).caster(playerIn).tick(200).force(10.0f).range(10f).addChild(new TraceContext()));
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if(entityIn instanceof ServerPlayerEntity) {
            SpellContext item = ExtraDataUtil.itemManaData(stack).spellContext();
            if(item.containChild(LibContext.SPAWN)) {
                SpawnContext spawnContext = item.getChild(LibContext.SPAWN);
                EntityType<?> type = spawnContext.entityType;
                if(type.getRegistryName() != null) {
                    AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayerEntity) entityIn, "entity_type_" + type.getRegistryName().getPath());
                }
            }
        }
    }
}
