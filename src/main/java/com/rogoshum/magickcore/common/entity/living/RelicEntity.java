package com.rogoshum.magickcore.common.entity.living;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.world.World;

public class RelicEntity extends MobEntity{

	protected RelicEntity(EntityType<? extends MobEntity> type, World worldIn) {
		super(type, worldIn);
		this.getAttributeManager().createInstanceIfAbsent(Attributes.MAX_HEALTH);
	}

}
