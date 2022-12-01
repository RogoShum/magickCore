package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.common.init.ModGroups;

import net.minecraft.item.Item;

public class BaseItem extends Item{
	public BaseItem(Properties properties) {
		super(properties);
	}

	public static Properties properties(){
		return new Item.Properties().group(ModGroups.ITEM_GROUP);
	}
}
