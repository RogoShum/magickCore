package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;

import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.magick.context.child.PotionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModGroups {
	public static final CreativeModeTab ITEM_GROUP = new MagickCoreGroup();
	public static final CreativeModeTab ELEMENT_ITEM_GROUP = new ElementItemGroup();
	public static final CreativeModeTab ENTITY_TYPE_GROUP = new EntityTypeGroup();
	public static final CreativeModeTab POTION_TYPE_GROUP = new PotionTypeGroup();
	public static final CreativeModeTab MAGICK_CONTEXT_GROUP = new MagickContextGroup();
	
	static class MagickCoreGroup extends CreativeModeTab {
		public MagickCoreGroup() {
	        super(CreativeModeTab.TABS.length-1, MagickCore.MOD_ID + "_group");
	    }

	    @Override
	    public ItemStack makeIcon() {
	        return new ItemStack(ModItems.MAGICK_CONTAINER.get());
	    }
	}

	static class EntityTypeGroup extends CreativeModeTab {
		public EntityTypeGroup() {
			super(CreativeModeTab.TABS.length-1, MagickCore.MOD_ID + "_entity_type_group");
		}

		@Override
		public ItemStack makeIcon() {
			ItemStack stack = new ItemStack(ModItems.ENTITY_TYPE.get());
			ExtraDataUtil.itemManaData(stack).spellContext().addChild(SpawnContext.create(ModEntities.MANA_ORB.get()));
			return stack;
		}
	}

	static class ElementItemGroup extends CreativeModeTab {
		public ElementItemGroup() {
			super(CreativeModeTab.TABS.length-1, MagickCore.MOD_ID + "_element_item_group");
		}

		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ModItems.ELEMENT_CRYSTAL.get());
		}
	}

	static class MagickContextGroup extends CreativeModeTab {
		public MagickContextGroup() {
			super(CreativeModeTab.TABS.length-1, MagickCore.MOD_ID + "_magick_context_group");
		}

		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ModItems.MAGICK_CORE.get());
		}
	}

	static class PotionTypeGroup extends CreativeModeTab {
		public PotionTypeGroup() {
			super(CreativeModeTab.TABS.length-1, MagickCore.MOD_ID + "_potion_type_group");
		}

		@Override
		public ItemStack makeIcon() {
			ItemStack stack = new ItemStack(ModItems.POTION_TYPE.get());
			ExtraDataUtil.itemManaData(stack).spellContext().addChild(PotionContext.create(ModEffects.SHIELD_REGEN_P.get()));
			return stack;
		}
	}
}
