package com.rogoshum.magickcore.tool;

import com.rogoshum.magickcore.api.itemstack.IItemData;
import com.rogoshum.magickcore.api.entity.IEntityData;
import com.rogoshum.magickcore.lib.LibEntityData;
import com.rogoshum.magickcore.lib.LibRegistry;
import com.rogoshum.magickcore.magick.extradata.entity.ElementToolData;
import com.rogoshum.magickcore.magick.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.magick.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.magick.extradata.item.ItemManaData;
import net.minecraft.entity.Entity;

import net.minecraft.item.ItemStack;

import java.util.function.Consumer;


public class ExtraDataHelper {
    public static IEntityData entityData(Entity entity) {
        return (IEntityData) entity;
    }

    public static IItemData itemData(ItemStack item) {
        return (IItemData)(Object)item;
    }

    public static void itemManaData(ItemStack item, Consumer<ItemManaData> consumer) {
        ((IItemData)(Object)item).execute(LibRegistry.ITEM_DATA, consumer);
    }

    public static ItemManaData itemManaData(ItemStack item) {
        return ((IItemData)(Object)item).get(LibRegistry.ITEM_DATA);
    }

    public static void entityStateData(Entity entity, Consumer<EntityStateData> consumer) {
        ((IEntityData)entity).execute(LibEntityData.ENTITY_STATE, consumer);
    }

    public static EntityStateData entityStateData(Entity entity) {
        return ((IEntityData)entity).get(LibEntityData.ENTITY_STATE);
    }

    public static void takenEntityData(Entity entity, Consumer<TakenEntityData> consumer) {
        ((IEntityData)entity).execute(LibEntityData.TAKEN_ENTITY, consumer);
    }

    public static TakenEntityData takenEntityData(Entity entity) {
        return ((IEntityData)entity).get(LibEntityData.TAKEN_ENTITY);
    }

    public static void elementToolData(Entity entity, Consumer<ElementToolData> consumer) {
        ((IEntityData)entity).execute(LibEntityData.ELEMENT_TOOL, consumer);
    }

    public static ElementToolData elementToolData(Entity entity) {
        return ((IEntityData)entity).get(LibEntityData.ELEMENT_TOOL);
    }
}
