package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ModGroup {
	public static final ItemGroup itemGroup = new MagickCoreGroup();
	public static final ItemGroup entityTypeGroup = new EntityTypeGroup();
	public static final ItemGroup manaEntityTypeGroup = new ManaEntityTypeGroup();
	
	static class MagickCoreGroup extends ItemGroup {
		public MagickCoreGroup() {
	        super(MagickCore.MOD_ID + "_group");
	    }

	    @Override
	    public ItemStack createIcon() {
	        return new ItemStack(ModItems.magick_container.get());
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

	static class ManaEntityTypeGroup extends ItemGroup {
		public ManaEntityTypeGroup() {
			super(MagickCore.MOD_ID + "_mana_entity_type_group");
		}

		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.ENTITY_TYPE.get());
		}
	}
}
