package com.rogoshum.magickcore.common.init;

import com.rogoshum.magickcore.MagickCore;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModGroups {
	public static final ItemGroup ITEM_GROUP = new MagickCoreGroup();
	public static final ItemGroup ENTITY_TYPE_GROUP = new EntityTypeGroup();
	public static final ItemGroup ELEMENT_ITEM_GROUP = new ElementItemGroup();
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
			return new ItemStack(ModItems.ENTITY_TYPE.get());
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
}
