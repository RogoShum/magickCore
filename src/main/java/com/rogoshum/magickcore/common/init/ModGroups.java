package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;

import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.magick.context.child.PotionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModGroups {
	public static final CreativeModeTab ITEM_GROUP = FabricItemGroupBuilder.create(MagickCore.fromId("group"))
			.icon(() -> new ItemStack(ModItems.MAGICK_CONTAINER.get()))
			.build();
	public static final CreativeModeTab ELEMENT_ITEM_GROUP = FabricItemGroupBuilder.create(MagickCore.fromId("entity_type_group"))
			.icon(() -> {
				ItemStack stack = new ItemStack(ModItems.ENTITY_TYPE.get());
				ExtraDataUtil.itemManaData(stack).spellContext().addChild(SpawnContext.create(ModEntities.MANA_ORB.get()));
				return stack;
			}).build();
	public static final CreativeModeTab ENTITY_TYPE_GROUP = FabricItemGroupBuilder.create(MagickCore.fromId("element_item_group"))
			.icon(() -> new ItemStack(ModItems.ELEMENT_CRYSTAL.get()))
			.build();
	public static final CreativeModeTab POTION_TYPE_GROUP = FabricItemGroupBuilder.create(MagickCore.fromId("magick_context_group"))
			.icon(() -> new ItemStack(ModItems.MAGICK_CORE.get()))
			.build();
	public static final CreativeModeTab MAGICK_CONTEXT_GROUP = FabricItemGroupBuilder.create(MagickCore.fromId("potion_type_group"))
			.icon(() -> {
				ItemStack stack = new ItemStack(ModItems.POTION_TYPE.get());
				ExtraDataUtil.itemManaData(stack).spellContext().addChild(PotionContext.create(ModEffects.SHIELD_REGEN_P.get()));
				return stack;
			}).build();
}
