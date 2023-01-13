package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.common.init.ModGroups;

import net.minecraft.item.Item;

import net.minecraft.item.Item.Properties;

public class BaseItem extends Item{
	public BaseItem(Properties properties) {
		super(properties);
	}

	public static Properties properties(){
		return new Item.Properties().tab(ModGroups.ITEM_GROUP);
	}
}
