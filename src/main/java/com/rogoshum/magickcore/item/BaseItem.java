package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.init.ModGroup;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class BaseItem extends Item{
	public static Properties properties = new Item.Properties().group(ModGroup.itemGroup);
	public BaseItem(Properties properties) {
		super(properties);
	}

}
