package com.rogoshum.magickcore.common.extradata;

import com.rogoshum.magickcore.common.api.itemstack.IItemData;
import com.rogoshum.magickcore.common.api.entity.IEntityData;
import com.rogoshum.magickcore.common.extradata.entity.ElementToolData;
import com.rogoshum.magickcore.common.extradata.entity.EntityStateData;
import com.rogoshum.magickcore.common.extradata.entity.TakenEntityData;
import com.rogoshum.magickcore.common.extradata.item.ItemManaData;
import com.rogoshum.magickcore.common.lib.LibEntityData;
import com.rogoshum.magickcore.common.lib.LibRegistry;
import net.minecraft.entity.Entity;

import net.minecraft.item.ItemStack;

import java.util.function.Consumer;


public class ExtraDataUtil {
    public static IEntityData entityData(Entity entity) {
        return (IEntityData) entity;
    }

    public static void itemManaData(ItemStack item, Consumer<ItemManaData> consumer) {
        consumer.accept(new ItemManaData(item));
    }

    public static ItemManaData itemManaData(ItemStack item) {
        return new ItemManaData(item);
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