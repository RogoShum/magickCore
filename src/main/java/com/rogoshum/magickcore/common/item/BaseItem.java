package com.rogoshum.magickcore.common.item;

import com.rogoshum.magickcore.common.init.ModGroups;
import net.minecraft.world.item.Item;

public class BaseItem extends Item {
	public BaseItem(Properties properties) {
		super(properties);
	}

	public static Properties properties(){
		return new Item.Properties().tab(ModGroups.ITEM_GROUP);
	}
}
