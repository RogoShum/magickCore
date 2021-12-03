package com.rogoshum.magickcore.item;

import com.rogoshum.magickcore.init.ModGroup;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;

public class BaseItem extends Item{
	public BaseItem(Properties properties) {
		super(properties);
	}

	public static Properties properties(){
		return new Item.Properties().group(ModGroup.itemGroup);
	}
}
