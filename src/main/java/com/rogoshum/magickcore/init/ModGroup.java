package com.rogoshum.magickcore.init;

import com.rogoshum.magickcore.MagickCore;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ModGroup {
	public static final ItemGroup itemGroup = new MagickCoreGroup();
	
	static class MagickCoreGroup extends ItemGroup {
		public MagickCoreGroup() {
	        super(MagickCore.MOD_ID + "_group");
	    }

	    @Override
	    public ItemStack createIcon() {
	        return new ItemStack(ModItems.magick_container.get());
	    }
	}
}
