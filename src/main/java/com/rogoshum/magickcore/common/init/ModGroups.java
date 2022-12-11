package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;

import com.rogoshum.magickcore.common.extradata.ExtraDataUtil;
import com.rogoshum.magickcore.common.magick.context.child.PotionContext;
import com.rogoshum.magickcore.common.magick.context.child.SpawnContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModGroups {
	public static final ItemGroup ITEM_GROUP = new MagickCoreGroup();
	public static final ItemGroup ELEMENT_ITEM_GROUP = new ElementItemGroup();
	public static final ItemGroup ENTITY_TYPE_GROUP = new EntityTypeGroup();
	public static final ItemGroup POTION_TYPE_GROUP = new PotionTypeGroup();
	public static final ItemGroup MAGICK_CONTEXT_GROUP = new MagickContextGroup();
	
	static class MagickCoreGroup extends ItemGroup {
		public MagickCoreGroup() {
	        super(MagickCore.MOD_ID + "_group");
	    }

	    @Override
	    public ItemStack createIcon() {
	        return new ItemStack(ModItems.MAGICK_CONTAINER.get());
	    }
	}

	static class EntityTypeGroup extends ItemGroup {
		public EntityTypeGroup() {
			super(MagickCore.MOD_ID + "_entity_type_group");
		}

		@Override
		public ItemStack createIcon() {
			ItemStack stack = new ItemStack(ModItems.ENTITY_TYPE.get());
			ExtraDataUtil.itemManaData(stack).spellContext().addChild(SpawnContext.create(ModEntities.MANA_ORB.get()));
			return stack;
		}
	}

	static class ElementItemGroup extends ItemGroup {
		public ElementItemGroup() {
			super(MagickCore.MOD_ID + "_element_item_group");
		}

		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.ELEMENT_CRYSTAL.get());
		}
	}

	static class MagickContextGroup extends ItemGroup {
		public MagickContextGroup() {
			super(MagickCore.MOD_ID + "_magick_context_group");
		}

		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.MAGICK_CORE.get());
		}
	}

	static class PotionTypeGroup extends ItemGroup {
		public PotionTypeGroup() {
			super(MagickCore.MOD_ID + "_potion_type_group");
		}

		@Override
		public ItemStack createIcon() {
			ItemStack stack = new ItemStack(ModItems.POTION_TYPE.get());
			ExtraDataUtil.itemManaData(stack).spellContext().addChild(PotionContext.create(ModEffects.SHIELD_REGEN_P.get()));
			return stack;
		}
	}
}
