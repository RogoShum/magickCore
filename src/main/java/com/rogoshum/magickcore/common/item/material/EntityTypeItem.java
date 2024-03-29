package com.rogoshum.magickcore.common.item.material;

import com.rogoshum.magickcore.client.RenderHelper;
import com.rogoshum.magickcore.api.mana.ISpellContext;
import com.rogoshum.magickcore.api.mana.IManaMaterial;
import com.rogoshum.magickcore.api.entity.IManaEntity;
import com.rogoshum.magickcore.client.item.ManaEnergyRenderer;
import com.rogoshum.magickcore.api.enums.ApplyType;
import com.rogoshum.magickcore.common.event.magickevent.AdvancementsEvent;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.item.ManaItem;
import com.rogoshum.magickcore.common.lib.LibItem;
import com.rogoshum.magickcore.common.magick.ManaFactor;
import com.rogoshum.magickcore.common.magick.context.MagickContext;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.init.ModGroups;
import com.rogoshum.magickcore.common.lib.LibContext;
import com.rogoshum.magickcore.common.magick.MagickReleaseHelper;
import com.rogoshum.magickcore.common.magick.context.SpellContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import com.rogoshum.magickcore.common.magick.context.child.TraceContext;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class EntityTypeItem extends ManaItem implements IManaMaterial {
    private static final HashSet<EntityType<?>> ERROR_TYPE = new HashSet<>();
    private static final HashMap<EntityType<?>, ManaFactor> BENEFICIAL_ENERGY = new HashMap<>();
    public EntityTypeItem() {
        super(properties());
    }

    public static ManaFactor getBeneficialEnergy(EntityType<?> entityType) {
        if(BENEFICIAL_ENERGY.containsKey(entityType))
            return BENEFICIAL_ENERGY.get(entityType);
        return ManaFactor.NON_MANA;
    }

    public static void addBeneficialEnergy(EntityType<?> entityType, ManaFactor factor) {
        BENEFICIAL_ENERGY.put(entityType, factor);
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
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent(LibItem.CONTEXT_MATERIAL));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        ItemManaData data = ExtraDataUtil.itemManaData(stack);
        if(data.spellContext().containChild(LibContext.SPAWN)) {
            EntityType<?> type = data.spellContext().<SpawnContext>getChild(LibContext.SPAWN).entityType;
            ManaFactor factor = getBeneficialEnergy(type);
            if(factor.force > 0 || factor.range > 0 || factor.tick > 0) {
                tooltip.add(new TranslatableComponent("magickcore.description.beneficial_energy").append(new TextComponent(":")));
                if(factor.force > 0)
                    tooltip.add(new TranslatableComponent(LibItem.FORCE));
                if(factor.range > 0)
                    tooltip.add(new TranslatableComponent(LibItem.RANGE));
                if(factor.tick > 0)
                    tooltip.add(new TranslatableComponent(LibItem.TICK));
            }
        }
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        ItemStack sample = new ItemStack(this);
        ExtraDataUtil.itemManaData(sample, (data) -> data.spellContext().applyType(ApplyType.SPAWN_ENTITY));
        if (group == ModGroups.ENTITY_TYPE_GROUP) {
            List<EntityType<? extends LivingEntity>> livings = new ArrayList<>();
            List<EntityType<? extends IManaEntity>> mana = new ArrayList<>();
            List<EntityType<? extends Projectile>> projectile = new ArrayList<>();
            Registry.ENTITY_TYPE.forEach(type -> {
                if(!ERROR_TYPE.contains(type)) {
                    try {
                        Entity entity = type.create(RenderHelper.getPlayer().level);
                        if(entity instanceof LivingEntity)
                            livings.add((EntityType<? extends LivingEntity>) type);
                        else if(entity instanceof IManaEntity)
                            mana.add((EntityType<? extends IManaEntity>) type);
                    } catch (Exception e) {
                        ERROR_TYPE.add(type);
                    }
                }
            });
            mana.forEach(type -> fillEntity(items, sample, type));
            livings.forEach(type -> fillEntity(items, sample, type));
            projectile.forEach(type -> fillEntity(items, sample, type));
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
        if(playerIn.level.isClientSide) return false;
        if(playerIn instanceof Player && !((Player) playerIn).isCreative()) return false;
        SpellContext item = ExtraDataUtil.itemManaData(stack).spellContext();
        MagickReleaseHelper.releaseMagick(MagickContext.create(playerIn.level, item).caster(playerIn).tick(200).force(10.0f).range(10f).addChild(new TraceContext()));
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if(entityIn instanceof ServerPlayer) {
            SpellContext item = ExtraDataUtil.itemManaData(stack).spellContext();
            if(item.containChild(LibContext.SPAWN)) {
                SpawnContext spawnContext = item.getChild(LibContext.SPAWN);
                EntityType<?> type = spawnContext.entityType;
                if(type.getDescriptionId() != null) {
                    AdvancementsEvent.STRING_TRIGGER.trigger((ServerPlayer) entityIn, "entity_type_" + new ResourceLocation(type.getDescriptionId()).getPath());
                }
            }
        }
    }
}
